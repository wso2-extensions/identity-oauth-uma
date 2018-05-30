/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.wso2.carbon.identity.oauth.uma.xacml.service.handler;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jaxen.JaxenException;
import org.wso2.balana.utils.exception.PolicyBuilderException;
import org.wso2.balana.utils.policy.PolicyBuilder;
import org.wso2.balana.utils.policy.dto.RequestElementDTO;
import org.wso2.carbon.identity.application.authentication.framework.exception.FrameworkException;
import org.wso2.carbon.identity.entitlement.EntitlementException;
import org.wso2.carbon.identity.entitlement.common.EntitlementPolicyConstants;
import org.wso2.carbon.identity.entitlement.common.dto.RequestDTO;
import org.wso2.carbon.identity.entitlement.common.dto.RowDTO;
import org.wso2.carbon.identity.entitlement.common.util.PolicyCreatorUtil;
import org.wso2.carbon.identity.oauth.uma.authorization.connector.PolicyEvaluator;
import org.wso2.carbon.identity.oauth.uma.permission.service.model.Resource;
import org.wso2.carbon.identity.oauth.uma.xacml.service.constants.XACMLAppUMAConstants;
import org.wso2.carbon.identity.oauth.uma.xacml.service.internal.AppUMADataholder;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLStreamException;

/**
 * Resource validation implementation. This uses XACML policies to evaluate resource defined by the relevant
 * service provider and the scopes related to the resource.
 */
public class XACMLUMAHandler implements PolicyEvaluator {

    private static final Log log = LogFactory.getLog(XACMLUMAHandler.class);
    private static final String DECISION_XPATH = "//ns:Result/ns:Decision/text()";
    private static final String XACML_NS = "urn:oasis:names:tc:xacml:3.0:core:schema:wd-17";
    private static final String XACML_NS_PREFIX = "ns";
    private static final String RULE_EFFECT_PERMIT = "Permit";
    private static final String RULE_EFFECT_NOT_APPLICABLE = "NotApplicable";

    public XACMLUMAHandler() {

    }

    /**
     * @param userName
     * @param resource
     * @return
     * @throws IdentityOAuth2Exception
     */
    public boolean isAuthorized(String userName, List<Resource> resource) throws IdentityOAuth2Exception {

        if (log.isDebugEnabled()) {
            log.debug("In policy authorization flow..." + "UserName: " + userName);
        }
        boolean isValid = false;

        try {
            for (Resource singleResource : resource) {
                RequestDTO requestDTO = createRequestDTO(singleResource, userName);
                RequestElementDTO requestElementDTO = PolicyCreatorUtil.createRequestElementDTO(requestDTO);

                String requestString = PolicyBuilder.getInstance().buildRequest(requestElementDTO);
                if (log.isDebugEnabled()) {
                    log.debug("XACML Authorization request :\n" + requestString);
                }

                String responseString =
                        AppUMADataholder.getInstance().getEntitlementService().getDecision(requestString);
                if (log.isDebugEnabled()) {
                    log.debug("XACML Authorization response :\n" + responseString);
                }
                String authzResponse = evaluateXACMLResponse(responseString);
                if (RULE_EFFECT_NOT_APPLICABLE.equalsIgnoreCase(authzResponse)) {
                    log.warn(String.format(
                            "No applicable rule for service provider '%s@%s', Hence authorizing the user by default. " +
                                    "Add an authorization policy (or unset authorization) to fix this warning."
                                    + "UserName: " + userName));
                    isValid = true;
                } else if (RULE_EFFECT_PERMIT.equalsIgnoreCase(authzResponse)) {
                    isValid = true;
                }
            }
        } catch (PolicyBuilderException | EntitlementException | FrameworkException e) {
            throw new IdentityOAuth2Exception(e.getMessage());
        }
        return isValid;
    }

    private RequestDTO createRequestDTO(Resource resource, String userName) {

        List<RowDTO> rowDTOs = new ArrayList<>();
        String listString = StringUtils.join(resource.getResourceScopes(), ",");

        RowDTO actionDTO = createRowDTO(listString, XACMLAppUMAConstants.AUTH_ACTION_ID,
                XACMLAppUMAConstants.ACTION_CATEGORY);
        RowDTO resourceDTO = createRowDTO(resource.getResourceId(), XACMLAppUMAConstants.RESOURCE_ID,
                XACMLAppUMAConstants.RESOURCE_CATEGORY);
        RowDTO userDTO =
                createRowDTO(userName, XACMLAppUMAConstants.USERNAME_ID, XACMLAppUMAConstants.USER_CATEGORY);

        rowDTOs.add(actionDTO);
        rowDTOs.add(resourceDTO);
        rowDTOs.add(userDTO);

        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setRowDTOs(rowDTOs);
        return requestDTO;

    }

    private RowDTO createRowDTO(String resourceName, String attributeId, String categoryValue) {

        RowDTO rowDTO = new RowDTO();
        rowDTO.setAttributeValue(resourceName);
        rowDTO.setAttributeDataType(EntitlementPolicyConstants.STRING_DATA_TYPE);
        rowDTO.setAttributeId(attributeId);
        rowDTO.setCategory(categoryValue);
        return rowDTO;
    }

    private String evaluateXACMLResponse(String xacmlResponse) throws FrameworkException {

        try {
            AXIOMXPath axiomxPath = new AXIOMXPath(DECISION_XPATH);
            axiomxPath.addNamespace(XACML_NS_PREFIX, XACML_NS);
            OMElement rootElement =
                    new StAXOMBuilder(new ByteArrayInputStream(xacmlResponse.getBytes(StandardCharsets.UTF_8)))
                            .getDocumentElement();
            return axiomxPath.stringValueOf(rootElement);
        } catch (JaxenException | XMLStreamException e) {
            throw new FrameworkException("Exception occurred when getting decision from xacml response.", e);
        }
    }

}

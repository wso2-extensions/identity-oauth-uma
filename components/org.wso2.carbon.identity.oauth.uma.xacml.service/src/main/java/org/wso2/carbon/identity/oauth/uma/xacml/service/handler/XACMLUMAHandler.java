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
import org.wso2.carbon.identity.oauth.common.exception.InvalidOAuthClientException;
import org.wso2.carbon.identity.oauth.dao.OAuthAppDO;
import org.wso2.carbon.identity.oauth.uma.permission.service.model.Resource;
import org.wso2.carbon.identity.oauth.uma.xacml.service.constants.XACMLAppUMAConstants;
import org.wso2.carbon.identity.oauth.uma.xacml.service.internal.AppUMADataholder;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.oauth2.util.OAuth2Util;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLStreamException;

/**
 * ResourceService use for resource management.
 */
public class XACMLUMAHandler {

    private static final Log log = LogFactory.getLog(XACMLUMAHandler.class);
    private static final String DECISION_XPATH = "//ns:Result/ns:Decision/text()";
    private static final String XACML_NS = "urn:oasis:names:tc:xacml:3.0:core:schema:wd-17";
    private static final String XACML_NS_PREFIX = "ns";
    private static final String RULE_EFFECT_PERMIT = "Permit";
    private static final String RULE_EFFECT_NOT_APPLICABLE = "NotApplicable";

    public XACMLUMAHandler() {

    }

    /**
     * @param resource
     * @param clientId
     * @return
     * @throws IdentityOAuth2Exception
     */
    public boolean isAuthouthorized(Resource resource, String clientId) throws IdentityOAuth2Exception {

        if (log.isDebugEnabled()) {
            log.debug("In policy authorization flow...");
        }
        boolean isValidated = false;
        try {
            OAuthAppDO authApp = OAuth2Util.getAppInformationByClientId(clientId);
            RequestDTO requestDTO = createRequestDTO(resource, authApp);
            RequestElementDTO requestElementDTO = PolicyCreatorUtil.createRequestElementDTO(requestDTO);

            String requestString = PolicyBuilder.getInstance().buildRequest(requestElementDTO);
            if (log.isDebugEnabled()) {
                log.debug("XACML Authorization request :\n" + requestString + resource + clientId);
            }

            String responseString =
                    AppUMADataholder.getInstance().getEntitlementService().getDecision(requestString);
            if (log.isDebugEnabled()) {
                log.debug("XACML Authorization response :\n" + responseString + resource + clientId);
            }
            String authzResponse = evaluateXACMLResponse(responseString);
            if (log.isDebugEnabled()) {
                log.debug("Create the response after evaluating the XACML request  :\n" + requestString);
            }
            if (RULE_EFFECT_NOT_APPLICABLE.equalsIgnoreCase(authzResponse)) {
                log.warn(String.format(
                        "No applicable rule for service provider '%s@%s', Hence authorizing the user by default. " +
                                "Add an authorization policy (or unset authorization) to fix this warning." + resource
                                + clientId));
                isValidated = true;
            } else if (RULE_EFFECT_PERMIT.equalsIgnoreCase(authzResponse)) {
                isValidated = true;
            }
        } catch (PolicyBuilderException e) {
            log.error(String.format("Exception occurred when building  XACML request for token with id  %s of user %s."
                    , clientId), e);
        } catch (EntitlementException e) {
            log.error(String.format("Exception occurred when evaluating XACML request for token with id %s of user %s."
                    , clientId), e);
        } catch (FrameworkException e) {
            log.error("Exception occurred when reading XACML response for token with id %s of user %s.", e);
        } catch (InvalidOAuthClientException e) {
            log.error(String.format("Exception occurred when getting app information for client id %s of user %s. " +
                            "Error occurred when retrieving corresponding app for this specific client id  ",
                    clientId), e);
        }
        return isValidated;
    }

    private RequestDTO createRequestDTO(Resource resource, OAuthAppDO authApp) {

        String listString = "";
        List<RowDTO> rowDTOs = new ArrayList<>();
        for (String scopes : resource.getResourceScopes()) {
            listString += scopes + ",";
        }
        RowDTO actionDTO = createRowDTO(listString, XACMLAppUMAConstants.AUTH_ACTION_ID,
                XACMLAppUMAConstants.ACTION_CATEGORY);
        RowDTO resourceDTO = createRowDTO(resource.getResourceId(), XACMLAppUMAConstants.RESOURCE_ID,
                XACMLAppUMAConstants.RESOURCE_CATEGORY);
        RowDTO spDTO = createRowDTO(authApp.getApplicationName(), XACMLAppUMAConstants.SP_NAME_ID,
                XACMLAppUMAConstants.SP_CATEGORY);

        rowDTOs.add(actionDTO);
        rowDTOs.add(spDTO);
        rowDTOs.add(resourceDTO);

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

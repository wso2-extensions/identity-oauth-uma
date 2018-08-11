/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.uma.grant;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.application.authentication.framework.model.AuthenticatedUser;

import org.wso2.carbon.identity.oauth.uma.common.exception.UMAClientException;
import org.wso2.carbon.identity.oauth.uma.common.exception.UMAServerException;
import org.wso2.carbon.identity.oauth.uma.permission.service.dao.PermissionTicketDAO;
import org.wso2.carbon.identity.oauth.uma.permission.service.model.Resource;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.oauth2.ResponseHeader;
import org.wso2.carbon.identity.oauth2.model.RequestParameter;
import org.wso2.carbon.identity.oauth2.token.OAuthTokenReqMessageContext;
import org.wso2.carbon.identity.oauth2.token.handlers.grant.AbstractAuthorizationGrantHandler;
import org.wso2.carbon.identity.uma.grant.connector.PolicyEvaluator;
import org.wso2.carbon.identity.uma.grant.internal.AuthorizeServiceComponent;

import java.security.Key;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Grant type for User Managed Access 2.0.
 */
public class GrantType extends AbstractAuthorizationGrantHandler {

    private static Log log = LogFactory.getLog(GrantType.class);
    private static Map<Integer, Key> privateKeys = new ConcurrentHashMap<>();

    boolean authStatus = false;
    public String subject;
    String grantType = null;
    String permissionTicket = null;
    String idToken = null;
    boolean isMatched = false;

    @Override
    public boolean validateGrant(OAuthTokenReqMessageContext tokReqMsgCtx) throws IdentityOAuth2Exception {


        if (log.isDebugEnabled()) {
            log.debug("In GrantType validateGrant method.");
        }

        // extract request parameters
        RequestParameter[] parameters = tokReqMsgCtx.getOauth2AccessTokenReqDTO().getRequestParameters();

        // find out grant type
        for (RequestParameter parameter : parameters) {
            if (UMAGrantConstants.UMA_GRANT_PARAM.equals(parameter.getKey())) {
                if (parameter.getValue() != null) {
                    grantType = parameter.getValue()[0];
                }
            }

            // find out permission ticket
            if (UMAGrantConstants.PERMISSION_TICKET.equals(parameter.getKey())) {
                if (parameter.getValue() != null) {
                    permissionTicket = parameter.getValue()[0];
                }
            }

            //find out Username from the IDToken by calling oauth2utills.getID token
            if (UMAGrantConstants.ID_TOKEN.equals(parameter.getKey())) {
                if (parameter.getValue() != null) {
                    idToken = parameter.getValue()[0];
                    isMatched = true;
                }
            }
        }

        if (grantType != null) {

            //validate grant type and permission ticket and extract subject from idToken
            authStatus = isValidGrantType(grantType, permissionTicket, subject);

            if (authStatus) {

                AuthenticatedUser authenticatedUser = new AuthenticatedUser();
                authenticatedUser.setUserName(grantType);
                tokReqMsgCtx.setAuthorizedUser(authenticatedUser);
                tokReqMsgCtx.setScope(tokReqMsgCtx.getOauth2AccessTokenReqDTO().getScope());

            } else {

                ResponseHeader responseHeader = new ResponseHeader();
                responseHeader.setKey("SampleHeader-999");
                responseHeader.setValue("Provided details are invalid.");
                tokReqMsgCtx.addProperty("RESPONSE_HEADERS", new ResponseHeader[]{responseHeader});
            }
        }
        return authStatus;
    }

    /**
     * @param grantType
     * @param permissionTicket
     * @param username
     * @return
     */

    private boolean isValidGrantType(String grantType, String permissionTicket, String username) throws
            IdentityOAuth2Exception {

        boolean isCheck = true;
        PermissionTicketDAO permissionTicketDAO = new PermissionTicketDAO();
        List<Resource> resources;

        try {
            if (grantType.equals("urn:ietf:params:oauth:grant-type:uma-ticket")) {
                resources = permissionTicketDAO.validatePermissionTicket(permissionTicket);
                log.info("Valid permission ticket :" + permissionTicket);

//todo: create a list and iterate services since from this implementation
//todo: lastly loaded osgi service will only be taken

                PolicyEvaluator policyEvaluator = AuthorizeServiceComponent.getPolicyEvaluator();
                policyEvaluator.isAuthorized(username, resources);

                return true;
            }
        } catch (UMAClientException e) {

            if (log.isDebugEnabled()) {
                log.debug("Invalid permission ticket. :\n" + permissionTicket);
            }
            return false;

        } catch (UMAServerException e) {

            if (log.isDebugEnabled()) {
                log.debug("Server error occurred. :\n" + permissionTicket);
            }

            return false;
        }
        return isCheck;
    }
}


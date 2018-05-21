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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.oauth.uma.permission.endpoint;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.jaxrs.ext.MessageContext;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.auth.service.AuthenticationContext;
import org.wso2.carbon.identity.core.util.IdentityUtil;
import org.wso2.carbon.identity.oauth.uma.common.HandleErrorResponseConstants;
import org.wso2.carbon.identity.oauth.uma.common.UMAConstants;
import org.wso2.carbon.identity.oauth.uma.common.exception.UMAException;
import org.wso2.carbon.identity.oauth.uma.common.exception.UMAServerException;
import org.wso2.carbon.identity.oauth.uma.permission.endpoint.dto.ErrorResponseDTO;
import org.wso2.carbon.identity.oauth.uma.permission.endpoint.dto.PermissionTicketResponseDTO;
import org.wso2.carbon.identity.oauth.uma.permission.endpoint.dto.ResourceModelDTO;
import org.wso2.carbon.identity.oauth.uma.permission.endpoint.exception.PermissionEndpointException;
import org.wso2.carbon.identity.oauth.uma.permission.service.PermissionService;
import org.wso2.carbon.identity.oauth.uma.permission.service.model.PermissionTicketModel;
import org.wso2.carbon.identity.oauth.uma.permission.service.model.Resource;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.Response;

/**
 * PermissionApiServiceImpl is used to obtain a permission ticket which represents requested resources with the scopes.
 */
public class PermissionApiServiceImpl extends PermissionApiService {

    private static Log log = LogFactory.getLog(PermissionApiServiceImpl.class);
    //The access token (PAT - Protection API Access Token) should contain the scope uma_protection to access the
    // permission endpoint.
    public static final String PAT_SCOPE = "uma_protection";
    public static final String OAUTH2_ALLOWED_SCOPES = "oauth2-allowed-scopes";
    private static final String AUTH_CONTEXT = "auth-context";
    private static final String CONSUMER_KEY = "consumer-key";

    /**
     * Requests a permission ticket.
     *
     * @param requestedPermission requested resource ids and their relevant scopes.
     * @return Response with the status of the creation of a permission ticket.
     */
    @Override
    public Response requestPermission(ResourceModelDTO requestedPermission, MessageContext context) {

        if (!isValidTokenScope(context) && getResourceOwner(context) == null && getConsumerKey(context) == null) {
            if (log.isDebugEnabled()) {
                log.debug("Required context information not available in the access token.");
            }
            return Response.status(Response.Status.UNAUTHORIZED).entity(getErrorResponseDTO
                    ("unauthorized", "Unauthorized user")).build();
        }
        if (requestedPermission == null) {
            if (log.isDebugEnabled()) {
                log.debug("Invalid request.");
            }
            return Response.status(Response.Status.BAD_REQUEST).entity(getErrorResponseDTO
                    ("invalid_request", "Empty request body")).build();
        }

        PermissionService permissionService = (PermissionService) PrivilegedCarbonContext.getThreadLocalCarbonContext()
                .getOSGiService(PermissionService.class, null);

        PermissionTicketModel permissionTicketModel = null;
        try {
            permissionTicketModel = permissionService.issuePermissionTicket(getPermissionTicketRequest(
                    requestedPermission), PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId(),
                    getResourceOwner(context));
        } catch (UMAException e) {
            handleErrorResponse("Error when requesting permission for consumer key: " + getConsumerKey(context),
                    e);
        }

        PermissionTicketResponseDTO permissionTicketResponseDTO = new PermissionTicketResponseDTO();
        if (permissionTicketModel != null) {
            permissionTicketResponseDTO.setTicket(permissionTicketModel.getTicket());
        } else {
            handleErrorResponse("Error when requesting permission for consumer key: " + getConsumerKey(context),
                    new UMAServerException(UMAConstants.ErrorMessages.ERROR_UNEXPECTED));
        }

        if (log.isDebugEnabled()) {
            if (IdentityUtil.isTokenLoggable("PermissionTicket")) {
                log.debug("Permission Ticket created: " + permissionTicketResponseDTO.getTicket());
            } else {
                // Avoid logging token since its a sensitive information.
                log.debug("Permission Ticket created.");
            }
        }
        return Response.status(Response.Status.CREATED).entity(permissionTicketResponseDTO).build();
    }

    /**
     * Validate if the the token contains the required scope.
     *
     * @param context message context
     * @return scope validation state of the access token
     */
    private boolean isValidTokenScope(MessageContext context) {

        String[] tokenScopes = null;
        if (context.getHttpServletRequest().getAttribute(AUTH_CONTEXT) instanceof AuthenticationContext) {
            tokenScopes = (String[]) ((AuthenticationContext) context.getHttpServletRequest()
                    .getAttribute(AUTH_CONTEXT)).getParameter(OAUTH2_ALLOWED_SCOPES);
        }
        return ArrayUtils.contains(tokenScopes, PAT_SCOPE);
    }

    /**
     * Retrieve resource owner name
     *
     * @param context message context
     */
    private String getResourceOwner(MessageContext context) {

        if (context.getHttpServletRequest().getAttribute(AUTH_CONTEXT) instanceof AuthenticationContext) {
            AuthenticationContext authContext = (AuthenticationContext) context.getHttpServletRequest()
                    .getAttribute(AUTH_CONTEXT);
            if (authContext.getUser() != null) {
                return authContext.getUser().getUserName();
            }
        }
        return null;
    }

    /**
     * Retrieve consumer key which represents the resource server.
     *
     * @param context message context
     * @return consumerKey
     */
    private String getConsumerKey(MessageContext context) {

        if (context.getHttpServletRequest().getAttribute(AUTH_CONTEXT) instanceof AuthenticationContext) {
            AuthenticationContext authContext = (AuthenticationContext) context.getHttpServletRequest()
                    .getAttribute(AUTH_CONTEXT);
            if (authContext.getParameter(CONSUMER_KEY) != null) {
                return String.valueOf(authContext.getParameter(CONSUMER_KEY));
            }
        }
        return null;
    }

    /**
     * List requested permissions.
     *
     * @param requestedPermission Start Index of the result set to enforce pagination
     * @return List of requested resources
     */
    private List<Resource> getPermissionTicketRequest(ResourceModelDTO requestedPermission) {

        List<Resource> resourceList = new ArrayList<>();
        requestedPermission.forEach(resourceModelInnerDTO -> {
            Resource resource = new Resource();
            resource.setResourceId(resourceModelInnerDTO.getResourceId());
            List<String> resourceScopesList = new ArrayList<>();
            resourceScopesList.addAll(resourceModelInnerDTO.getResourceScopes());
            resource.setResourceScopes(resourceScopesList);
            resourceList.add(resource);
        });
        return resourceList;
    }

    /**
     * Logs the error, builds a PermissionEndpointException with specified details and throws it.
     *
     * @param message      error message
     * @param umaException
     * @throws PermissionEndpointException
     */
    private void handleErrorResponse(String message, UMAException umaException) throws PermissionEndpointException {

        String code = umaException.getCode();
        String errorCode = null;
        Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;
        boolean isHTTPStatusOnly = true;
        boolean isServerException = umaException instanceof UMAServerException;
        if (isServerException) {
            log.error(message, umaException);
        } else {
            log.error(message + " - " + umaException.getErrorLogMessage());
            if (log.isDebugEnabled()) {
                log.debug(message, umaException);
            }
            if (HandleErrorResponseConstants.RESPONSE_DATA_MAP.containsKey(code)) {
                String statusCode = HandleErrorResponseConstants.RESPONSE_DATA_MAP.get(code)[0];
                errorCode = HandleErrorResponseConstants.RESPONSE_DATA_MAP.get(code)[1];
                status = Response.Status.fromStatusCode(Integer.parseInt(statusCode));
                isHTTPStatusOnly = false;
            }
        }
        throw buildPermissionEndpointException(status, errorCode, umaException.getMessage(), isHTTPStatusOnly);
    }

    private PermissionEndpointException buildPermissionEndpointException(Response.Status status,
                                                                         String errorCode, String description,
                                                                         boolean isHTTPStatusOnly) {

        if (isHTTPStatusOnly) {
            return new PermissionEndpointException(status);
        } else {
            return new PermissionEndpointException(status, getErrorResponseDTO(errorCode, description));
        }
    }

    /**
     * Returns a generic errorResponseDTO
     *
     * @param errorCode   specifies the error code.
     * @param description describes the error code briefly.
     * @return A generic errorResponseDTO with the specified details
     */
    private ErrorResponseDTO getErrorResponseDTO(String errorCode, String description) {

        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
        errorResponseDTO.setError(errorCode);
        errorResponseDTO.setErrorDescription(description);
        return errorResponseDTO;
    }

}

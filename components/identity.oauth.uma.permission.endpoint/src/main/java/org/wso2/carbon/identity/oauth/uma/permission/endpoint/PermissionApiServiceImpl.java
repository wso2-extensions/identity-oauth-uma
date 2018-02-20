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
import org.wso2.carbon.identity.oauth.uma.permission.endpoint.dto.ErrorResponseDTO;
import org.wso2.carbon.identity.oauth.uma.permission.endpoint.dto.PermissionTicketResponseDTO;
import org.wso2.carbon.identity.oauth.uma.permission.endpoint.dto.ResourceModelDTO;
import org.wso2.carbon.identity.oauth.uma.permission.endpoint.exception.PermissionEndpointException;
import org.wso2.carbon.identity.oauth.uma.permission.service.PermissionService;
import org.wso2.carbon.identity.oauth.uma.permission.service.UMAConstants;
import org.wso2.carbon.identity.oauth.uma.permission.service.exception.PermissionDAOException;
import org.wso2.carbon.identity.oauth.uma.permission.service.exception.UMAException;
import org.wso2.carbon.identity.oauth.uma.permission.service.exception.UMAResourceException;
import org.wso2.carbon.identity.oauth.uma.permission.service.model.PermissionTicketDO;
import org.wso2.carbon.identity.oauth.uma.permission.service.model.Resource;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.Response;

/**
 * PermissionApiServiceImpl is used to obtain a permission ticket which represents requested resources with the scopes.
 */
public class PermissionApiServiceImpl extends PermissionApiService {

    private static Log log = LogFactory.getLog(PermissionApiServiceImpl.class);
    private final String patScope = "uma_protection";
    private final String authContext = "auth-context";
    private final String oauth2AllowedScopes = "oauth2-allowed-scopes";

    /**
     * Requests a permission ticket.
     *
     * @param requestedPermission requested resource ids and their relevant scopes.
     * @return Response with the status of the creation of a permission ticket.
     */
    @Override
    public Response requestPermission(ResourceModelDTO requestedPermission, MessageContext context) {

        String[] tokenScopes = (String[]) ((AuthenticationContext) context.getHttpServletRequest()
                .getAttribute(authContext)).getParameter(oauth2AllowedScopes);
        if (!ArrayUtils.contains(tokenScopes, patScope)) {
            log.error("Access token doesn't contain valid scope.");
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        if (requestedPermission == null) {
            log.error("Empty request body.");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        String tenantDomain = ((AuthenticationContext) context.getHttpServletRequest().getAttribute(authContext)).
                getUser().getTenantDomain();

        PermissionService permissionService = (PermissionService) PrivilegedCarbonContext.getThreadLocalCarbonContext()
                .getOSGiService(PermissionService.class, null);

        PermissionTicketDO permissionTicketDO = null;
        try {
            permissionTicketDO = permissionService.issuePermissionTicket(getPermissionTicketRequest(
                    requestedPermission), tenantDomain);
        } catch (UMAResourceException e) {
            handleErrorResponse(e, false);
        } catch (PermissionDAOException e) {
            handleErrorResponse(e, true);
        } catch (Throwable throwable) {
            handleErrorResponse(throwable, true);
        }

        PermissionTicketResponseDTO permissionTicketResponseDTO = new PermissionTicketResponseDTO();
        permissionTicketResponseDTO.setTicket(permissionTicketDO.getTicket());

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

    private List<Resource> getPermissionTicketRequest(ResourceModelDTO requestedPermission) {

        List<Resource> resourceList = new ArrayList<>();
        requestedPermission.forEach(resourceModelInnerDTO -> {
            Resource resource = new Resource();
            resource.setResourceId(resourceModelInnerDTO.getResource_id());
            List<String> resourceScopesList = new ArrayList<>();
            resourceModelInnerDTO.getResource_scopes().forEach(resourceScope -> {
                resourceScopesList.add(resourceScope);
            });
            resource.setResourceScopes(resourceScopesList);
            resourceList.add(resource);
        });
        return resourceList;
    }

    private void handleErrorResponse(Throwable throwable, boolean isServerException)
            throws PermissionEndpointException {

        String code;
        String errorCode = null;
        Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;
        boolean isStatusOnly = true;
        if (throwable instanceof UMAException) {
            code = ((UMAException) throwable).getCode();
        } else {
            code = UMAConstants.ErrorMessages.ERROR_UNEXPECTED.getCode();
        }
        if (isServerException) {
            if (throwable == null) {
                log.error(status.getReasonPhrase());
            } else {
                log.error(status.getReasonPhrase(), throwable);
            }
        } else {
            log.error("Client error while requesting permission ticket.", throwable);
            if (code != null) {
                if (HandleErrorResponseConstants.RESPONSE_DATA_MAP.containsKey(code)) {
                    String statusCode = HandleErrorResponseConstants.RESPONSE_DATA_MAP.get(code)[0];
                    errorCode = HandleErrorResponseConstants.RESPONSE_DATA_MAP.get(code)[1];
                    status = Response.Status.fromStatusCode(Integer.parseInt(statusCode));
                    isStatusOnly = false;
                }
            }
        }
        throw buildPermissionEndpointException(status, errorCode, throwable == null ? "" : throwable.getMessage(),
                isStatusOnly);
    }

    private PermissionEndpointException buildPermissionEndpointException(Response.Status status,
                                                                         String errorCode, String description,
                                                                         boolean isStatusOnly) {

        if (isStatusOnly) {
            return new PermissionEndpointException(status);
        } else {
            ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
            errorResponseDTO.setError(errorCode);
            errorResponseDTO.setErrorDescription(description);
            return new PermissionEndpointException(status, errorResponseDTO);
        }
    }
}

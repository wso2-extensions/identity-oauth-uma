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

package org.wso2.carbon.identity.oauth.uma.endpoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.core.util.IdentityUtil;
import org.wso2.carbon.identity.oauth.uma.endpoint.dto.ErrorResponseDTO;
import org.wso2.carbon.identity.oauth.uma.endpoint.dto.PermissionTicketResponseDTO;
import org.wso2.carbon.identity.oauth.uma.endpoint.dto.ResourceModelDTO;
import org.wso2.carbon.identity.oauth.uma.endpoint.exception.PermissionEndpointException;
import org.wso2.carbon.identity.oauth.uma.service.PermissionService;
import org.wso2.carbon.identity.oauth.uma.service.UMAConstants;
import org.wso2.carbon.identity.oauth.uma.service.exception.PermissionDAOException;
import org.wso2.carbon.identity.oauth.uma.service.exception.UMAException;
import org.wso2.carbon.identity.oauth.uma.service.exception.UMAResourceException;
import org.wso2.carbon.identity.oauth.uma.service.model.PermissionTicketDO;
import org.wso2.carbon.identity.oauth.uma.service.model.Resource;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.Response;

/**
 * PermissionApiServiceImpl is used to obtain a permission ticket which represents requested resources with the scopes.
 */
public class PermissionApiServiceImpl extends PermissionApiService {

    private static Log log = LogFactory.getLog(PermissionApiServiceImpl.class);

    /**
     * Requests a permission ticket.
     *
     * @param requestedPermission requested resource ids and their relevant scopes.
     * @return Response with the status of the creation of a permission ticket.
     */
    @Override
    public Response requestPermission(ResourceModelDTO requestedPermission) {

        PermissionService permissionService = (PermissionService) PrivilegedCarbonContext.getThreadLocalCarbonContext()
                .getOSGiService(PermissionService.class, null);
        if (requestedPermission == null) {
            log.error("Empty request body.");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        PermissionTicketDO permissionTicketDO = null;
        try {
            permissionTicketDO = permissionService.issuePermissionTicket(getPermissionTicketRequest(
                    requestedPermission));
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
                if (PermissionEndpointConstants.RESPONSE_DATA_MAP.containsKey(code)) {
                    String statusCode = PermissionEndpointConstants.RESPONSE_DATA_MAP.get(code)[0];
                    errorCode = PermissionEndpointConstants.RESPONSE_DATA_MAP.get(code)[1];
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

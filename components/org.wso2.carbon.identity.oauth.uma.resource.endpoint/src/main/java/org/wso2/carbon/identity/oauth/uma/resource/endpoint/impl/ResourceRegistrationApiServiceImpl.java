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

package org.wso2.carbon.identity.oauth.uma.resource.endpoint.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.jaxrs.ext.MessageContext;
import org.wso2.carbon.identity.auth.service.AuthenticationContext;
import org.wso2.carbon.identity.oauth.uma.resource.endpoint.HandleErrorResponseConstants;
import org.wso2.carbon.identity.oauth.uma.resource.endpoint.ResourceRegistrationApiService;
import org.wso2.carbon.identity.oauth.uma.resource.endpoint.dto.CreateResourceDTO;
import org.wso2.carbon.identity.oauth.uma.resource.endpoint.dto.ErrorDTO;
import org.wso2.carbon.identity.oauth.uma.resource.endpoint.dto.ReadResourceDTO;
import org.wso2.carbon.identity.oauth.uma.resource.endpoint.dto.ResourceDetailsDTO;
import org.wso2.carbon.identity.oauth.uma.resource.endpoint.dto.UpdateResourceDTO;

import org.wso2.carbon.identity.oauth.uma.resource.endpoint.exceptions.ResourceEndpointException;
import org.wso2.carbon.identity.oauth.uma.resource.endpoint.util.ResourceUtils;

import org.wso2.carbon.identity.oauth.uma.resource.service.ResourceConstants;
import org.wso2.carbon.identity.oauth.uma.resource.service.exceptions.UMAClientException;
import org.wso2.carbon.identity.oauth.uma.resource.service.exceptions.UMAException;
import org.wso2.carbon.identity.oauth.uma.resource.service.exceptions.UMAServiceException;
import org.wso2.carbon.identity.oauth.uma.resource.service.model.Resource;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ws.rs.core.Response;


/**
 * ResourceRegistrationApiServiceImpl is used to handling resource management.
 */
public class ResourceRegistrationApiServiceImpl extends ResourceRegistrationApiService {

    private static final Log log = LogFactory.getLog(ResourceRegistrationApiServiceImpl.class);
    private static String tenantDomain = null;
    private static String resourceOwnerName = null;
    private static String consumerKey = null;

    /**
     * Register a resource with resource details
     *
     * @param requestedResource details of the resource to be registered
     * @return Response with the status of the registration
     */
    @Override
    public Response registerResource(ResourceDetailsDTO requestedResource, MessageContext context) {

        tenantDomain =  ((AuthenticationContext) context.getHttpServletRequest().getAttribute("auth-context"))
                .getUser().getTenantDomain();
        ;
        resourceOwnerName = ((AuthenticationContext) context.getHttpServletRequest().getAttribute("auth-context"))
                .getUser().getUserName();
        consumerKey = (String) ((AuthenticationContext) context.getHttpServletRequest().getAttribute("auth-context"))
                .getParameter("consumer-key");
        Response response = null;

        if (requestedResource == null) {
            log.error("Request body cannot be empty.");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        try {
            Resource registerResource = ResourceUtils.getResourceService()
                    .registerResource(ResourceUtils.getResource(requestedResource), resourceOwnerName, tenantDomain
                            , consumerKey);
            CreateResourceDTO createResourceDTO = ResourceUtils.createResponse(registerResource);
            return Response.status(Response.Status.CREATED).entity(createResourceDTO).build();

        } catch (UMAServiceException e) {
            handleErrorResponse(e, log);
        } catch (UMAClientException e) {
            handleErrorResponse(e, log);
            log.error("Client error when retrieving resource from the resource server.", e);
        } catch (Throwable throwable) {
            handleErrorResponse((UMAException) throwable, log);
            log.error("Internal server error occurred. ", throwable);
        }
        return response;
    }

    /**
     * Retrieve the resource of the given resourceId
     *
     * @param resourceId Resourceid of the resource which need to get retrieved
     * @return Response with the retrieved resource/ retrieval status
     */
    @Override
    public Response getResource(String resourceId, MessageContext context) {

        Response response = null;

        try {
            if (isResourceId(resourceId)) {
                try {
                    Resource resourceRegistration = ResourceUtils.getResourceService().getResourceById
                            (resourceId);
                    ReadResourceDTO readResourceDTO = ResourceUtils.readResponse(resourceRegistration);
                    return Response.status(Response.Status.OK).entity(readResourceDTO).build();
                } catch (UMAServiceException e) {
                    handleErrorResponse(e, log);

                } catch (Throwable throwable) {
                    handleErrorResponse((UMAException) throwable, log);
                }
            } else {
                throw new UMAClientException(ResourceConstants.ErrorMessages.ERROR_CODE_NOT_FOUND_RESOURCE_ID);
            }
        } catch (UMAClientException e) {
            handleErrorResponse(e, log);
            log.error("Client error when retrieving resource from the resource server.", e);

        }
        return response;
    }

    /**
     * Retrieve the available resourceId list
     *
     * @param context
     * @return Response with the retrieved resourceId's/ retrieval status
     */
    @Override
    public Response getResourceIds(MessageContext context) {

        resourceOwnerName = ((AuthenticationContext) context.getHttpServletRequest().getAttribute("auth-context"))
                .getUser().getUserName();
        consumerKey = (String) ((AuthenticationContext) context.getHttpServletRequest().getAttribute("auth-context"))
                .getParameter("consumer-key");

        try {
            List<String> resourceRegistration = ResourceUtils.getResourceService().getResourceIds(resourceOwnerName,
                    consumerKey);
            Response response = Response.ok().entity(resourceRegistration).build();
            return response;
        } catch (UMAClientException e) {
            handleErrorResponse(e, log);
            log.error("Invalid request.Request with valid resource Id to update the resource. ", e);
        } catch (UMAException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return null;
    }

    /**
     * Update a resource
     *
     * @param updatedResource details of the resource to be updated
     * @param resourceId      ID of the resource to be updated
     * @return Response with the updated resource
     */
    @Override
    public Response updateResource(String resourceId, ResourceDetailsDTO updatedResource, MessageContext context) {

        Response response = null;
        try {
            if (isResourceId(resourceId)) {

                try {
                    Resource resourceRegistration = ResourceUtils.getResourceService().updateResource(resourceId,
                            ResourceUtils.
                                    getResource(updatedResource));
                    resourceRegistration.setResourceId(resourceId);
                    UpdateResourceDTO updateResourceDTO = ResourceUtils.updateResponse(resourceRegistration);
                    return Response.status(Response.Status.CREATED).entity(updateResourceDTO).build();
                } catch (UMAServiceException e) {
                    handleErrorResponse(e, log);
                    log.error("Invalid request.Request with valid resource Id to update the resource. ", e);

                } catch (Throwable throwable) {
                    handleErrorResponse((UMAException) throwable, log);
                    log.error("Internal server error occurred. ", throwable);
                }
            } else {
                throw new UMAClientException(ResourceConstants.ErrorMessages.ERROR_CODE_NOT_FOUND_RESOURCE_ID);
            }
        } catch (UMAClientException e) {
            handleErrorResponse(e, log);
            log.error("Client error when retrieving resource from the resource server.", e);

        }
        return response;
    }

    /**
     * Delete the resource for the given resourceId
     *
     * @param resourceId resourceId of the resource which need to get deleted
     * @return Response with the status of resource deletion
     */
    @Override
    public Response deleteResource(String resourceId, MessageContext messageContext) {

        Response response = null;

        try {
            if (isResourceId(resourceId)) {

                try {
                    if (ResourceUtils.getResourceService().deleteResource(resourceId)) {
                        return Response.status(Response.Status.NO_CONTENT).build();
                    }

                } catch (UMAServiceException e) {
                    handleErrorResponse(e, log);
                    log.error("Invalid request. ", e);

                } catch (Throwable throwable) {
                    handleErrorResponse((UMAException) throwable, log);
                    log.error("Internal server error occurred. ", throwable);
                }
            } else {
                throw new UMAClientException(ResourceConstants.ErrorMessages.ERROR_CODE_NOT_FOUND_RESOURCE_ID);
            }
        } catch (UMAClientException e) {
            handleErrorResponse(e, log);
            log.error("Client error when retrieving resource from the resource server.", e);

        }
        return response;

    }

    /**
     * Handle error responses.
     *
     * @param umaException exception that should pass to handle error.
     */
    public static void handleErrorResponse(UMAException umaException, Log log) throws ResourceEndpointException {

        HandleErrorResponseConstants handleErrorResponseConstants = new HandleErrorResponseConstants();
        if (handleErrorResponseConstants.getResponseMap().containsKey(umaException.getErrorCode())) {
            String statusCode = handleErrorResponseConstants.getResponseMap().get(umaException.getErrorCode())[0];
            Response.Status status = Response.Status.fromStatusCode(Integer.parseInt(statusCode));
            String errorCode = handleErrorResponseConstants.getResponseMap().get(umaException.getErrorCode())[1];

            throw buildResourceEndpointException(status, errorCode, umaException.getMessage());
        }
    }

    private static ResourceEndpointException buildResourceEndpointException(Response.Status status,
                                                                            String code, String description) {

        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setCode(code);
        errorDTO.setDescription(description);
        return new ResourceEndpointException(status, errorDTO);
    }

    /**
     * Validate resourceId
     *
     * @param resourceId resourceId of the resource which need to get deleted
     * @return validator obtain
     */
    public boolean isResourceId(String resourceId) throws UMAClientException {

        String validPattern = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
        Pattern pattern = Pattern.compile(validPattern);
        Matcher match = pattern.matcher(resourceId);

        if (match.find()) {
            return true;
        } else {
            return false;
        }
    }
}

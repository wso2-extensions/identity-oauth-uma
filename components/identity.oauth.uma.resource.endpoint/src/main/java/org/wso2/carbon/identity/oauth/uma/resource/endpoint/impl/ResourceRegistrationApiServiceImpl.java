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
import org.wso2.carbon.identity.oauth.uma.resource.endpoint.ResourceEndpointConstants;
import org.wso2.carbon.identity.oauth.uma.resource.endpoint.ResourceRegistrationApiService;
import org.wso2.carbon.identity.oauth.uma.resource.endpoint.dto.CreateResourceDTO;
import org.wso2.carbon.identity.oauth.uma.resource.endpoint.dto.ErrorDTO;
import org.wso2.carbon.identity.oauth.uma.resource.endpoint.dto.ReadResourceDTO;
import org.wso2.carbon.identity.oauth.uma.resource.endpoint.dto.ResourceDetailsDTO;
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

    /**
     * Register a resource with resource details
     *
     * @param requestedResource details of the resource to be registered
     * @return Response with the status of the registration
     */
    @Override
    public Response registerResource(ResourceDetailsDTO requestedResource) {

        if (requestedResource == null) {
            log.error("Request body cannot be empty.");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        try {
            Resource registerResource = ResourceUtils.getResourceService()
                    .registerResource(ResourceUtils.getResource(requestedResource));
            CreateResourceDTO createResourceDTO = ResourceUtils.createResponse(registerResource);
            return Response.status(Response.Status.CREATED).entity(createResourceDTO).build();
        } catch (UMAServiceException e) {
            handleErrorResponse(e, log);
        } catch (UMAClientException e) {
            if (log.isDebugEnabled()) {
                log.debug("Client error when registering resource in resource server.", e);
            }
            handleErrorResponse(e, log);
        } catch (Throwable throwable) {
            log.error("Internal server error occurred. ", throwable);
        }
        return null;
    }

    /**
     * Retrieve the resource of the given resourceId
     *
     * @param resourceId Resourceid of the resource which need to get retrieved
     * @return Response with the retrieved resource/ retrieval status
     */
    @Override
    public Response getResource(String resourceId) {

        Response response = null;

        try {
            if (!isResourceId(resourceId)) {
                try {
                    Resource resourceRegistration = ResourceUtils.getResourceService().getResourceById
                            (resourceId);
                    ReadResourceDTO readResourceDTO = ResourceUtils.readResponse(resourceRegistration);
                    return Response.status(Response.Status.OK).entity(readResourceDTO).build();
                } catch (UMAServiceException e) {
                    handleErrorResponse(e, log);
                } catch (Throwable throwable) {
                    log.error("Internal server error occurred. ", throwable);
                }
            }
        } catch (UMAClientException e) {
            handleErrorResponse(e, log);
            if (log.isDebugEnabled()) {
                log.debug("Client error when retrieving resource from the resource server.", e);
            }
        }
        return response;
    }

    /**
     * Retrieve the available resourceId list
     *
     * @param resourceOwnerId
     * @return Response with the retrieved resourceId's/ retrieval status
     */
    @Override
    public Response getResourceIds(String resourceOwnerId) {

        // For testing purpose currently resource owner id is taken to validate pat access token.
        resourceOwnerId = "123";
        try {
            List<String> resourceRegistration = ResourceUtils.getResourceService().getResourceIds(resourceOwnerId);
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
     * @param resourceId ID of the resource to be updated
     * @return Response with the updated resource
     */
    @Override
    public Response updateResource(String resourceId, ResourceDetailsDTO updatedResource) {

        Response response = null;
        try {
            if (!isResourceId(resourceId)) {

                try {
                    Resource resourceRegistration = ResourceUtils.getResourceService().updateResource(resourceId,
                            ResourceUtils.
                                    getResource(updatedResource));
                    return Response.status(Response.Status.CREATED).entity(updatedResource).build();
                } catch (UMAServiceException e) {
                    handleErrorResponse(e, log);
                    log.error("Invalid request.Request with valid resource Id to update the resource. ", e);

                } catch (Throwable throwable) {
                    log.error("Internal server error occurred. ", throwable);
                }
            }
        } catch (UMAClientException e) {
            handleErrorResponse(e, log);
            if (log.isDebugEnabled()) {
                log.debug("Client error when retrieving resource from the resource server.", e);
            }
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
    public Response deleteResource(String resourceId) {

        try {
            if (!isResourceId(resourceId)) {

                try {
                    if (ResourceUtils.getResourceService().deleteResource(resourceId)) {
                        return Response.status(Response.Status.NO_CONTENT).build();
                    }

                } catch (UMAServiceException e) {
                    handleErrorResponse(e, log);
                    log.error("Invalid request. ", e);
                } catch (Throwable throwable) {
                    log.error("Internal server error occurred. ", throwable);
                }
            }
        } catch (UMAClientException e) {
            handleErrorResponse(e, log);
            if (log.isDebugEnabled()) {
                log.debug("Client error when retrieving resource from the resource server.", e);
            }
        }
        return null;
    }

    /**
     * Validate resourceId
     *
     * @param umaException exception that should pass to handle error.
     */
    public static void handleErrorResponse(UMAException umaException, Log log) throws ResourceEndpointException {

        ResourceEndpointConstants resourceEndpointConstants = new ResourceEndpointConstants();
        if (resourceEndpointConstants.getResponseMap().containsKey(umaException.getErrorCode())) {
            String statusCode = resourceEndpointConstants.getResponseMap().get(umaException.getErrorCode())[0];
            Response.Status status = Response.Status.fromStatusCode(Integer.parseInt(statusCode));
            String errorCode = resourceEndpointConstants.getResponseMap().get(umaException.getErrorCode())[1];

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

        boolean validator = true;
        String pattern = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
        Pattern pat = Pattern.compile(pattern);
        Matcher match = pat.matcher(resourceId);

        if (match.find()) {
            validator = false;
        } else {
            throw new UMAClientException(ResourceConstants.ErrorMessages.ERROR_CODE_NOT_FOUND_RESOURCE_ID);
        }
        return validator;
    }
}

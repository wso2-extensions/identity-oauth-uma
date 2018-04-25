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

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.cxf.jaxrs.ext.MessageContext;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.auth.service.AuthenticationContext;
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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import javax.ws.rs.core.Response;

import static org.wso2.carbon.identity.oauth.uma.resource.service.ResourceConstants.RESOURCE_PATH;

/**
 * ResourceRegistrationApiServiceImpl is used to handling resource management.
 */
public class ResourceRegistrationApiServiceImpl extends ResourceRegistrationApiService {

    private static final Log log = LogFactory.getLog(ResourceRegistrationApiServiceImpl.class);
    // These variable related to PAT_SCOPE ("uma_protection") is defined in the UMA 2.o specification.
    private static final String PAT_SCOPE = "uma_protection";
    // These variables are assigning from auth-rest valve.
    private static final String AUTH_CONTEXT = "auth-context";
    private static final String OAUTH2_ALLOWED_SCOPES = "oauth2-allowed-scopes";
    private static final String CONSUMER_KEY = "consumer-key";

    /**
     * Register a resource with resource details
     *
     * @param requestedResource details of the resource to be registered
     * @return Response with the status of the registration
     */
    @Override
    public Response registerResource(ResourceDetailsDTO requestedResource, MessageContext context) {

        String resourceOwner = getResourceOwner(context);
        int tenantId = PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId();
        String consumerKey = getConsumerKey(context);

        if (isValidToken(context) && resourceOwner != null && consumerKey != null) {

            if (log.isDebugEnabled()) {
                log.debug("Access token is valid and all required context information " +
                        "are existing in the token.");
            }

            if (CollectionUtils.isEmpty(requestedResource.getResource_Scopes())) {

                if (log.isDebugEnabled()) {
                    log.debug("Resource scopes not existing in the flow of resource registration. ");
                }
                return Response.status(Response.Status.BAD_REQUEST).entity(getErrorDTO("Bad_Request",
                        "ResourceId is not in format or message context is null. ")).build();
            }
            try {
                Resource registerResource = ResourceUtils.getResourceService()
                        .registerResource(ResourceUtils.getResource(requestedResource), resourceOwner,
                                tenantId, consumerKey);
                CreateResourceDTO createResourceDTO = ResourceUtils.createResponse(registerResource);

                return Response.status(Response.Status.CREATED).entity(createResourceDTO)
                        .location(getResourceLocationURI(createResourceDTO)).build();
            } catch (UMAException e) {
                handleErrorResponse("Error when requesting context information: " +
                        isValidToken(context) + getResourceOwner(context) + getConsumerKey(context), e);
            } catch (URISyntaxException e) {
                log.error("string could not be parsed as a URI reference.", e);
            }
        }
        return getUnauthorizedResponseObject();
    }

    /**
     * Retrieve the resource of the given resourceId
     *
     * @param resourceId Resourceid of the resource which need to get retrieved
     * @return Response with the retrieved resource/ retrieval status
     */
    @Override
    public Response getResource(String resourceId, MessageContext context) {

        try {
            if (isValidToken(context)) {

                if (log.isDebugEnabled()) {
                    log.debug("Access token is valid and all required context information " +
                            "are existing in the token.");
                }

                if (isResourceIdValid(resourceId)) {

                    if (log.isDebugEnabled()) {
                        log.debug("Valid resourceId :" + resourceId);
                    }

                    Resource resourceRegistration = ResourceUtils.getResourceService().getResourceById
                            (resourceId);
                    ReadResourceDTO readResourceDTO = ResourceUtils.readResponse(resourceRegistration);

                    if (log.isDebugEnabled()) {
                        log.debug("Obtained resource details relevant to specific resourceId.");
                    }
                    return Response.status(Response.Status.OK).entity(readResourceDTO).build();
                }
            }
        } catch (UMAException e) {
            handleErrorResponse("Error when requesting information: " + isValidToken(context) + resourceId, e);
        }
        return getUnauthorizedResponseObject();
    }

    /**
     * Retrieve the available resourceId list
     *
     * @param context
     * @return Response with the retrieved resourceId's/ retrieval status
     */
    @Override
    public Response getResourceIds(MessageContext context) {

        String resourceOwner = getResourceOwner(context);
        String consumerKey = getConsumerKey(context);

        if (isValidToken(context) && resourceOwner != null && consumerKey != null) {

            if (log.isDebugEnabled()) {
                log.debug("Access token is valid and all required context information " +
                        "are existing in the token.");
            }

            try {
                List<String> resourceRegistration = ResourceUtils.getResourceService()
                        .getResourceIds(resourceOwner, consumerKey);
                if (log.isDebugEnabled()) {
                    log.debug("Obtained resources belong to a specific resource Owner.");
                }

                return Response.ok().entity(resourceRegistration).build();

            } catch (UMAException e) {
                handleErrorResponse("Error when requesting context information : " +
                        isValidToken(context) + getResourceOwner(context) + getConsumerKey(context), e);
            }
        }
        return getUnauthorizedResponseObject();
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

        try {
            if (isValidToken(context)) {

                if (log.isDebugEnabled()) {
                    log.debug("Access token is valid and all required context information " +
                            "are existing in the token.");
                }
                if (isResourceIdValid(resourceId)) {

                    if (log.isDebugEnabled()) {
                        log.debug("Valid resourceId :" + resourceId);
                    }

                    Resource resourceRegistration = ResourceUtils.getResourceService().updateResource(resourceId,
                            ResourceUtils.getResource(updatedResource));
                    resourceRegistration.setResourceId(resourceId);
                    UpdateResourceDTO updateResourceDTO = ResourceUtils.updateResponse(resourceRegistration);

                    if (log.isDebugEnabled()) {
                        log.debug("Updated resource details which was persisted on the database.");
                    }

                    return Response.status(Response.Status.CREATED).entity(updateResourceDTO).build();
                }
            }
        } catch (UMAException e) {
            handleErrorResponse("Error when requesting information: " + isValidToken(context) + resourceId, e);
        }
        return getUnauthorizedResponseObject();
    }

    /**
     * Delete the resource for the given resourceId
     *
     * @param resourceId resourceId of the resource which need to get deleted
     * @return Response with the status of resource deletion
     */

    @Override
    public Response deleteResource(String resourceId, MessageContext context) {

        try {
            if (isValidToken(context)) {

                if (log.isDebugEnabled()) {
                    log.debug("Access token is valid and all required context information " +
                            "are existing in the token.");
                }

                if (isResourceIdValid(resourceId)) {

                    if (log.isDebugEnabled()) {
                        log.debug("Valid resourceId :" + resourceId);
                    }

                    if (ResourceUtils.getResourceService().deleteResource(resourceId)) {

                        if (log.isDebugEnabled()) {
                            log.debug("Delete resource details successfully.");
                        }
                        return Response.status(Response.Status.NO_CONTENT).build();
                    }
                }
            }
        } catch (UMAException e) {
            handleErrorResponse("Error when requesting information: " + isValidToken(context) + resourceId, e);
        }
        return getUnauthorizedResponseObject();
    }

    /**
     * @param umaException
     * @throws ResourceEndpointException
     */
    private void handleErrorResponse(String message, UMAException umaException)
            throws ResourceEndpointException {

        String code = umaException.getErrorCode();
        String errorCode = null;
        Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;
        boolean isStatusOnly = true;
        boolean isServerException = umaException instanceof UMAServiceException;

        if (isServerException) {
            log.error(message, umaException);
        } else {
            log.error(message + " - " + umaException.getErrorLogMessage());
            if (log.isDebugEnabled()) {
                log.debug(message, umaException);
            }
            if (HandleErrorResponseConstants.RESPONSE_MAP.containsKey(code)) {
                String statusCode = HandleErrorResponseConstants.RESPONSE_MAP.get(code)[0];
                status = Response.Status.fromStatusCode(Integer.parseInt(statusCode));
                errorCode = HandleErrorResponseConstants.RESPONSE_MAP.get(code)[1];
                isStatusOnly = false;
            }
        }
        throw buildResourceEndpointException(status, errorCode, umaException.getMessage(), isStatusOnly);
    }

    public ResourceEndpointException buildResourceEndpointException(Response.Status status, String errorCode,
                                                                    String description, boolean isStatusOnly) {

        if (isStatusOnly) {
            return new ResourceEndpointException(status);
        } else {
            return new ResourceEndpointException(status, getErrorDTO(errorCode, description));
        }
    }

    /**
     * Validate resourceId
     *
     * @param resourceId resourceId of the resource which need to get deleted
     * @return boolean
     */
    private static final boolean isResourceIdValid(String resourceId) throws UMAClientException {

        if (resourceId == null || StringUtils.isEmpty(resourceId)) {
            if (log.isDebugEnabled()) {
                log.debug("ResourceId is invalid :" + resourceId);
            }
            throw new UMAClientException(ResourceConstants.ErrorMessages.ERROR_CODE_INVALID_RESOURCE_ID);
        } else {
            return true;
        }
    }

    /**
     * Retrieve resource owner name
     *
     * @param context message context
     * @return resource owner name
     */
    private String getResourceOwner(MessageContext context) {

        String resourceOwnerName = null;
        if (context.getHttpServletRequest().getAttribute(AUTH_CONTEXT) instanceof AuthenticationContext) {
            AuthenticationContext authContext = (AuthenticationContext) context.getHttpServletRequest()
                    .getAttribute(AUTH_CONTEXT);
            if (authContext.getUser() != null) {
                return authContext.getUser().getUserName();
            }
        }
        return resourceOwnerName;
    }

    /**
     * Retrieve ConsumerKey
     *
     * @param context message context
     * @return clientId
     */
    private String getConsumerKey(MessageContext context) {

        String consumerKey = null;
        if (context.getHttpServletRequest().getAttribute(AUTH_CONTEXT) instanceof AuthenticationContext) {
            AuthenticationContext authContext = (AuthenticationContext) context.getHttpServletRequest()
                    .getAttribute(AUTH_CONTEXT);
            if (authContext.getParameter(CONSUMER_KEY) != null) {
                return String.valueOf(authContext.getParameter(CONSUMER_KEY));
            }
        }
        return consumerKey;
    }

    /**
     * Retrieve Scopes
     *
     * @param context message context
     * @return scopes
     */
    private boolean isValidToken(MessageContext context) {

        String[] tokenScopes = null;
        if (context.getHttpServletRequest().getAttribute(AUTH_CONTEXT) instanceof AuthenticationContext) {
            tokenScopes = (String[]) ((AuthenticationContext) context.getHttpServletRequest()
                    .getAttribute(AUTH_CONTEXT)).getParameter(OAUTH2_ALLOWED_SCOPES);
        }
        return ArrayUtils.contains(tokenScopes, PAT_SCOPE);
    }

    /**
     * Making errorDTO
     *
     * @param errorCode   Related error code to error
     * @param description Description related to the error
     * @return errorDTO
     */
    private ErrorDTO getErrorDTO(String errorCode, String description) {

        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setCode(errorCode);
        errorDTO.setDescription(description);
        return errorDTO;
    }

    private Response getUnauthorizedResponseObject() {

        if (log.isDebugEnabled()) {
            log.debug("Context information not in the access token.");
        }
        return Response.status(Response.Status.UNAUTHORIZED).entity(getErrorDTO
                ("Unauthorized", "Unauthorized User.")).build();
    }

    private URI getResourceLocationURI(CreateResourceDTO response) throws URISyntaxException {

        return new URI(RESOURCE_PATH + "/" + response.getResourceId());

    }
}

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

package org.wso2.carbon.identity.oauth.uma.resource.service;

import org.wso2.carbon.identity.oauth.uma.common.exception.UMAClientException;
import org.wso2.carbon.identity.oauth.uma.common.exception.UMAServerException;
import org.wso2.carbon.identity.oauth.uma.resource.service.model.Resource;

import java.util.List;

/**
 * This interface holds the implemented methods to ResourceServiceImpl.
 */

public interface ResourceService {

    /**
     * Delete registered resource.
     *
     * @param resourceId Resource ID.
     * @return True if deleting resource is successful.
     * @throws UMAServerException Server side related error.
     * @throws UMAClientException Client side related error.
     */
    boolean deleteResource(String resourceId) throws UMAServerException, UMAClientException;

    /**
     * Get registered resource IDs.
     *
     * @param resourceOwnerName Resource owner username.
     * @param userDomain        User store domain.
     * @param consumerKey       Client ID of the resource server.
     * @return List of resource IDs.
     * @throws UMAServerException Server side related error.
     * @throws UMAClientException Client side related error.
     */
    List<String> getResourceIds(String resourceOwnerName, String userDomain, String consumerKey) throws
            UMAServerException, UMAClientException;

    /**
     * Register resource as an UMA protected resource.
     *
     * @param resourceRegistration Resource to be registered.
     * @param resourceOwnerName    Resource owner username.
     * @param tenantId             Tenant ID.
     * @param consumerKey          Client ID of the resource server.
     * @param userDomain           User store domain.
     * @return Registered resource.
     * @throws UMAServerException Server side related error.
     * @throws UMAClientException Client side related error.
     */
    Resource registerResource(Resource resourceRegistration, String resourceOwnerName, int tenantId,
                              String consumerKey, String userDomain) throws UMAServerException, UMAClientException;

    /**
     * Get registered resource by ID.
     *
     * @param resourceId Resource ID.
     * @return Resource.
     * @throws UMAServerException Server side related error.
     * @throws UMAClientException Client side related error.
     */
    Resource getResourceById(String resourceId)
            throws UMAServerException, UMAClientException;

    /**
     * Update registered resource.
     *
     * @param resourceId           Resource ID.
     * @param resourceRegistration Resource to be updated.
     * @return True if updating resource is successful.
     * @throws UMAServerException Server side related error.
     * @throws UMAClientException Client side related error.
     */
    boolean updateResource(String resourceId, Resource resourceRegistration)
            throws UMAServerException, UMAClientException;

    /**
     * Check if the user is a resource owner.
     *
     * @param resourceId  Resource ID.
     * @param userName    Username.
     * @param userDomain  User store domain.
     * @param consumerKey Client ID of the resource server.
     * @return True if the user is the resource owner.
     * @throws UMAServerException Server side related error.
     * @throws UMAClientException Client side related error.
     */
    boolean isResourceOwner(String resourceId, String userName, String userDomain, String consumerKey)
            throws UMAServerException, UMAClientException;
}

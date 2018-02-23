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

package org.wso2.carbon.identity.oauth.uma.resource.service.impl;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.oauth.uma.resource.service.ResourceService;
import org.wso2.carbon.identity.oauth.uma.resource.service.dao.ResourceDAO;
import org.wso2.carbon.identity.oauth.uma.resource.service.exceptions.UMAServiceException;
import org.wso2.carbon.identity.oauth.uma.resource.service.model.Resource;

import java.util.List;

/**
 * ResourceService use for resource management.
 */
public class ResourceServiceImpl implements ResourceService {

    private static final Log log = LogFactory.getLog(ResourceServiceImpl.class);

    private static ResourceDAO resourceDAO = new ResourceDAO();

    @Override
    public Resource registerResource(Resource resourceRegistration, String resourceOwnerName, String tenantDomain,
                                     String consumerKey) throws UMAServiceException {

        resourceRegistration = resourceDAO.registerResource(resourceRegistration, resourceOwnerName, tenantDomain,
                consumerKey);
        log.info("Resource registered successfully.");
        return resourceRegistration;
    }

    /**
     * Retrieve the available Resource list
     *
     * @param resourceOwnerName To ientify resources belongs to same owner
     * @return resource list
     * @throws UMAServiceException
     */
    @Override
    public List<String> getResourceIds(String resourceOwnerName, String consumerKey) throws UMAServiceException {

        List<String> resourceRegistration = resourceDAO.retrieveResourceIDs(resourceOwnerName, consumerKey);
        log.info("Retrieved resourceId's successfully.");
        return resourceRegistration;
    }

    /**
     * @param resourceId resource ID of the resource which need to get retrieved
     * @return Retrieved resource using resource ID
     * @throws UMAServiceException
     */
    @Override
    public Resource getResourceById(String resourceId) throws UMAServiceException {

        Resource resourceRegistration;

        resourceRegistration = resourceDAO.retrieveResource(resourceId);
        log.info("Retrieved resource detail's successfully.");
        return resourceRegistration;

    }

    /**
     * Update the resource of the given resource ID
     *
     * @param resourceRegistration details of updated resource
     * @return updated resource
     * @throws UMAServiceException
     */
    @Override
    public Resource updateResource(String resourceId, Resource resourceRegistration)
            throws  UMAServiceException {

        resourceDAO.updateResource(resourceId, resourceRegistration);
        log.info("Resource details updated successfully.");
        return resourceRegistration;
    }

    /**
     * Delete the resource for the given resource ID
     *
     * @param resourceId Resource ID of the resource which need to get deleted
     * @throws UMAServiceException
     */
    @Override
    public boolean deleteResource(String resourceId) throws UMAServiceException {

        Resource resourceRegistration = null;
        log.info("Resource deleted successfully from the database.");
        return resourceDAO.deleteResource(resourceId);

    }
}

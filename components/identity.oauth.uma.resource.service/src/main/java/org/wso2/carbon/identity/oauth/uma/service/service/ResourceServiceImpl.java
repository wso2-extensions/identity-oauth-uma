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

package org.wso2.carbon.identity.oauth.uma.service.service;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.oauth.uma.service.ResourceService;
import org.wso2.carbon.identity.oauth.uma.service.dao.ResourceDAO;
import org.wso2.carbon.identity.oauth.uma.service.exceptions.UMAClientException;
import org.wso2.carbon.identity.oauth.uma.service.exceptions.UMAException;
import org.wso2.carbon.identity.oauth.uma.service.exceptions.UMAServiceException;
import org.wso2.carbon.identity.oauth.uma.service.model.Resource;

import java.sql.SQLException;
import java.util.List;

/**
 * ResourceService use for resource management
 */

public class ResourceServiceImpl implements ResourceService {

    private static final Log log = LogFactory.getLog(ResourceServiceImpl.class);

    private static ResourceDAO resourceDAO = new ResourceDAO();

    @Override
    public Resource registerResource(Resource resourceRegistration) throws
            UMAException {

        resourceRegistration = resourceDAO.registerResource(resourceRegistration);
        return resourceRegistration;
    }

    /**
     * Retrieve the available Resource list
     *
     * @param resourceOwnerId To ientify resources belongs to same owner
     * @return resource list
     * @throws UMAException
     */

    @Override
    public List<String> getResourceIds(String resourceOwnerId) throws UMAException {

        List<String> resourceRegistration = resourceDAO.retrieveResourceIDs(resourceOwnerId);

        return resourceRegistration;
    }

    /**
     * @param resourceId resource ID of the resource which need to get retrieved
     * @return Retrieved resource using resource ID
     * @throws UMAException
     */

    @Override
    public Resource getResourceById(String resourceId)
            throws UMAServiceException, UMAClientException {

        //Method which used to evaluate xacml policy

        /*XACMLBasedAuthorizationHandler xacmlBasedAuthorizationHandler = new XACMLBasedAuthorizationHandler();
        xacmlBasedAuthorizationHandler.isAuthorized();
*/
        Resource resourceRegistration;

        resourceRegistration = resourceDAO.retrieveResource(resourceId);
        return resourceRegistration;

    }

    /**
     * Update the resource of the given resource ID
     *
     * @param resourceRegistration details of updated resource
     * @return updated resource
     * @throws UMAException
     */

    @Override
    public Resource updateResource(String resourceId, Resource resourceRegistration)
            throws SQLException, UMAException {

        resourceDAO.updateResource(resourceId, resourceRegistration);

        return resourceRegistration;
    }

    /**
     * Delete the resource for the given resource ID
     *
     * @param resourceId Resource ID of the resource which need to get deleted
     * @throws UMAException
     */

    @Override
    public boolean deleteResource(String resourceId) throws UMAException, SQLException {

        Resource resourceRegistration = null;
        return resourceDAO.deleteResource(resourceId);

    }
}

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

import org.wso2.carbon.identity.oauth.uma.resource.service.exceptions.UMAClientException;
import org.wso2.carbon.identity.oauth.uma.resource.service.exceptions.UMAException;
import org.wso2.carbon.identity.oauth.uma.resource.service.exceptions.UMAServiceException;
import org.wso2.carbon.identity.oauth.uma.resource.service.model.Resource;


import java.sql.SQLException;
import java.util.List;

/**
 * This interface holds the implemented methods to ResourceServiceImpl.
 */

public interface ResourceService {

    public boolean deleteResource(String resourceId) throws UMAException, SQLException;

    public List<String> getResourceIds(String resourceOwnerId) throws UMAException;

    public Resource registerResource(Resource resourceRegistration)
            throws UMAException;

    public Resource getResourceById(String resourceId)
            throws UMAServiceException, UMAClientException;

    public Resource updateResource(String resourceId, Resource resourceRegistration)
            throws SQLException, UMAException;
}

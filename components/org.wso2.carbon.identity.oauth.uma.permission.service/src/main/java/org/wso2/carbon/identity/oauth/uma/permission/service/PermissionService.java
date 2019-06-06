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

package org.wso2.carbon.identity.oauth.uma.permission.service;

import org.wso2.carbon.identity.oauth.uma.common.exception.UMAClientException;
import org.wso2.carbon.identity.oauth.uma.common.exception.UMAServerException;
import org.wso2.carbon.identity.oauth.uma.permission.service.model.PermissionTicketModel;
import org.wso2.carbon.identity.oauth.uma.permission.service.model.Resource;

import java.util.List;

/**
 * PermissionService is the service interface used for permission registration.
 */
public interface PermissionService {

    PermissionTicketModel issuePermissionTicket(List<Resource> resourceList, int tenantId, String resourceOwnerName,
                                                String clientId, String userDomain)
            throws UMAClientException, UMAServerException;

    /**
     * Validate the access token issued for permission ticket.
     * @param accessToken Access token string.
     * @return List of resources associated with this access token.
     * @throws UMAClientException Client side related error.
     * @throws UMAServerException Server side related error.
     */
    List<Resource> validateAccessToken(String accessToken) throws UMAClientException, UMAServerException;
}

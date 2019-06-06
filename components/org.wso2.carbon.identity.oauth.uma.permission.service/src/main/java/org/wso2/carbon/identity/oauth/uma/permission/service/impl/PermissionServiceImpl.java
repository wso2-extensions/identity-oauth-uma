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

package org.wso2.carbon.identity.oauth.uma.permission.service.impl;

import org.wso2.carbon.identity.oauth.config.OAuthServerConfiguration;
import org.wso2.carbon.identity.oauth.uma.common.UMAConstants;
import org.wso2.carbon.identity.oauth.uma.common.exception.UMAClientException;
import org.wso2.carbon.identity.oauth.uma.common.exception.UMAServerException;
import org.wso2.carbon.identity.oauth.uma.permission.service.PermissionService;
import org.wso2.carbon.identity.oauth.uma.permission.service.dao.PermissionTicketDAO;
import org.wso2.carbon.identity.oauth.uma.permission.service.model.PermissionTicketModel;
import org.wso2.carbon.identity.oauth.uma.permission.service.model.Resource;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.oauth2.model.AccessTokenDO;
import org.wso2.carbon.identity.oauth2.util.OAuth2Util;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * PermissionServiceImpl service is used for permission registration.
 */
public class PermissionServiceImpl implements PermissionService {

    @Override
    public PermissionTicketModel issuePermissionTicket(List<Resource> resourceList, int tenantId, String
            resourceOwnerName, String clientId, String userDomain) throws UMAClientException, UMAServerException {

        PermissionTicketModel permissionTicketModel = new PermissionTicketModel();

        //TODO: Make this an extension point.
        String ticketString = UUID.randomUUID().toString();
        permissionTicketModel.setTicket(ticketString);
        permissionTicketModel.setCreatedTime(new Timestamp(new Date().getTime()));
        permissionTicketModel.setStatus(UMAConstants.PermissionTicketStates.PERMISSION_TICKET_STATE_ACTIVE);
        permissionTicketModel.setTenantId(tenantId);
        long createdTimeInMillis = permissionTicketModel.getCreatedTime().getTime();
        permissionTicketModel.setExpiryTime(calculatePermissionTicketExpiryTime(createdTimeInMillis));

        PermissionTicketDAO.persistPermissionTicket(resourceList, permissionTicketModel, resourceOwnerName, clientId,
                userDomain);

        return permissionTicketModel;
    }

    @Override
    public List<Resource> validateAccessToken(String accessToken) throws UMAClientException, UMAServerException {

        try {
            AccessTokenDO tokenDO = OAuth2Util.getAccessTokenDOfromTokenIdentifier(accessToken);
            String tokenId = tokenDO.getTokenId();
            String permissionTicket = PermissionTicketDAO.retrievePermissionTicketForTokenId(tokenId);
            return PermissionTicketDAO.getResourcesForPermissionTicket(permissionTicket);
        } catch (IdentityOAuth2Exception e) {
            throw new UMAServerException("Error occurred while retrieving token information.", e);
        }
    }

    private Timestamp calculatePermissionTicketExpiryTime(long createdTimeInMillis) {

        //TODO:Add a new configuration to define permission ticket validity period in identity.xml
        //For now the validity time period available for authorization code in identity.xml is used as the validity
        //time period for permission ticket.
        long validityPeriodInMillis = OAuthServerConfiguration.getInstance().
                getAuthorizationCodeValidityPeriodInSeconds() * 1000;

        long expiryTimeInMillis = createdTimeInMillis + validityPeriodInMillis;

        return new Timestamp(expiryTimeInMillis);
    }
}

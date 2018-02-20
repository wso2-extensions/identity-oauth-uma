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

package org.wso2.carbon.identity.oauth.uma.permission.service.dao;

import org.wso2.carbon.identity.core.util.IdentityDatabaseUtil;
import org.wso2.carbon.identity.oauth.uma.permission.service.UMAConstants;
import org.wso2.carbon.identity.oauth.uma.permission.service.exception.PermissionDAOException;
import org.wso2.carbon.identity.oauth.uma.permission.service.exception.UMAResourceException;
import org.wso2.carbon.identity.oauth.uma.permission.service.model.PermissionTicketDO;
import org.wso2.carbon.identity.oauth.uma.permission.service.model.Resource;
import org.wso2.carbon.identity.oauth2.util.NamedPreparedStatement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Data Access Layer functionality for Permission Endpoint. This includes storing requested permissions
 * (requested resource ids with their scopes).
 */
public class PermissionTicketDAO {

    private static final String STORE_PT_QUERY = "INSERT INTO IDN_PERMISSION_TICKET " +
            "(PT, TIME_CREATED, VALIDITY_PERIOD, TICKET_STATE, TENANT_DOMAIN) VALUES " +
            "(:" + UMAConstants.SQLPlaceholders.PERMISSION_TICKET + ";,:" + UMAConstants.SQLPlaceholders.TIME_CREATED +
            ";,:" + UMAConstants.SQLPlaceholders.VALIDITY_PERIOD + ";,:" + UMAConstants.SQLPlaceholders.STATE + ";,:" +
            UMAConstants.SQLPlaceholders.TENANT_DOMAIN + ";)";
    private static final String STORE_PT_RESOURCE_IDS_QUERY = "INSERT INTO IDN_PT_RESOURCE " +
            "(PT_RESOURCE_ID, PT_ID) VALUES " +
            "((SELECT ID FROM IDN_RESOURCE WHERE RESOURCE_ID = :" + UMAConstants.SQLPlaceholders.RESOURCE_ID + ";),:"
            + UMAConstants.SQLPlaceholders.ID + ";)";
    private static final String STORE_PT_RESOURCE_SCOPES_QUERY = "INSERT INTO IDN_PT_RESOURCE_SCOPE " +
            "(PT_RESOURCE_ID, PT_SCOPE_ID) VALUES (:" + UMAConstants.SQLPlaceholders.ID + ";, " +
            "(SELECT ID FROM IDN_RESOURCE_SCOPE WHERE SCOPE_NAME = :" + UMAConstants.SQLPlaceholders.RESOURCE_SCOPE
            + ";))";
    private static final String VALIDATE_REQUESTED_RESOURCE_IDS_WITH_REGISTERED_RESOURCE_IDS = "SELECT ID " +
            "FROM IDN_RESOURCE WHERE RESOURCE_ID = ?";
    private static final String VALIDATE_REQUESTED_RESOURCE_SCOPES_WITH_REGISTERED_RESOURCE_SCOPES = "SELECT ID FROM " +
            "IDN_RESOURCE_SCOPE WHERE SCOPE_NAME = ?";

    /**
     * Issue a permission ticket. Permission ticket represents the resources requested by the resource server on
     * client's behalf
     *
     * @param resourceList       A list with the resource ids and the corresponding scopes.
     * @param permissionTicketDO Model class for permission ticket values.
     * @throws PermissionDAOException Exception thrown when there is a database issue.
     * @throws UMAResourceException   Exception thrown when there is an invalid resource ID/scope.
     */
    public static void persistPTandRequestedPermissions(List<Resource> resourceList,
                                                        PermissionTicketDO permissionTicketDO) throws
            UMAResourceException, PermissionDAOException {

        try (Connection connection = IdentityDatabaseUtil.getDBConnection()) {
            checkResourceIdsExistence(connection, resourceList);
            checkResourceScopesExistence(connection, resourceList);
            connection.setAutoCommit(false);
            NamedPreparedStatement ptNamedPreparedStatement = new NamedPreparedStatement(connection, STORE_PT_QUERY);
            ptNamedPreparedStatement.setString(UMAConstants.SQLPlaceholders.PERMISSION_TICKET,
                    permissionTicketDO.getTicket());
            ptNamedPreparedStatement.setTimeStamp(UMAConstants.SQLPlaceholders.TIME_CREATED,
                    new Timestamp(new Date().getTime()), permissionTicketDO.getCreatedTime());
            ptNamedPreparedStatement.setLong(UMAConstants.SQLPlaceholders.VALIDITY_PERIOD,
                    permissionTicketDO.getValidityPeriod());
            ptNamedPreparedStatement.setString(UMAConstants.SQLPlaceholders.STATE, permissionTicketDO.getStatus());
            ptNamedPreparedStatement.setString(UMAConstants.SQLPlaceholders.TENANT_DOMAIN,
                    permissionTicketDO.getTenantDomain());
            try (PreparedStatement preparedStatement = ptNamedPreparedStatement.getPreparedStatement()) {
                preparedStatement.execute();

                // Checking if the PT is persisted in the db.
                long id;
                try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        id = resultSet.getLong(1);
                    } else {
                        throw new PermissionDAOException(UMAConstants.ErrorMessages
                                .ERROR_INTERNAL_SERVER_ERROR_FAILED_TO_PERSIST_PT);
                    }
                }

                for (Resource resource : resourceList) {
                    NamedPreparedStatement resourceNamedPreparedStatement = new NamedPreparedStatement(connection,
                            STORE_PT_RESOURCE_IDS_QUERY);
                    resourceNamedPreparedStatement.setString(UMAConstants.SQLPlaceholders.RESOURCE_ID,
                            resource.getResourceId());
                    resourceNamedPreparedStatement.setLong(UMAConstants.SQLPlaceholders.ID, id);
                    try (PreparedStatement resourceIdStatement =
                                 resourceNamedPreparedStatement.getPreparedStatement()) {
                        resourceIdStatement.execute();

                        try (ResultSet resultSet = resourceIdStatement.getGeneratedKeys()) {
                            if (resultSet.next()) {
                                long resourceId = resultSet.getLong(1);
                                NamedPreparedStatement scopeNamedPreparedStatement = new NamedPreparedStatement
                                        (connection, STORE_PT_RESOURCE_SCOPES_QUERY);
                                try (PreparedStatement resourceScopeStatement =
                                             scopeNamedPreparedStatement.getPreparedStatement()) {
                                    for (String scope : resource.getResourceScopes()) {
                                        scopeNamedPreparedStatement.setLong(UMAConstants.SQLPlaceholders.ID,
                                                resourceId);
                                        scopeNamedPreparedStatement.setString(
                                                UMAConstants.SQLPlaceholders.RESOURCE_SCOPE, scope);
                                        scopeNamedPreparedStatement.getPreparedStatement().addBatch();
                                    }
                                    resourceScopeStatement.executeBatch();
                                }
                            }
                        }
                    }
                }
            }
            connection.commit();
        } catch (SQLException e) {
            throw new PermissionDAOException(UMAConstants.ErrorMessages
                    .ERROR_INTERNAL_SERVER_ERROR_FAILED_TO_PERSIST_REQUESTED_PERMISSIONS, e);
        }
    }

    private static void checkResourceIdsExistence(Connection connection, List<Resource> resourceList) throws
            UMAResourceException, PermissionDAOException {

        for (Resource resource : resourceList) {
            try (PreparedStatement resourceIdStatement = connection.prepareStatement(
                    VALIDATE_REQUESTED_RESOURCE_IDS_WITH_REGISTERED_RESOURCE_IDS)) {
                resourceIdStatement.setString(1, resource.getResourceId());
                try (ResultSet resultSet = resourceIdStatement.executeQuery()) {
                    if (!resultSet.next()) {
                        throw new UMAResourceException(UMAConstants.ErrorMessages
                                .ERROR_BAD_REQUEST_INVALID_RESOURCE_ID);
                    }
                }
            } catch (SQLException e) {
                throw new PermissionDAOException(UMAConstants.ErrorMessages
                        .ERROR_INTERNAL_SERVER_ERROR_FAILED_TO_PERSIST_REQUESTED_PERMISSIONS, e);
            }
        }
    }

    private static void checkResourceScopesExistence(Connection connection, List<Resource> resourceList) throws
            UMAResourceException, PermissionDAOException {

        for (Resource resource : resourceList) {
            try (PreparedStatement resourceScopeStatement = connection.prepareStatement(
                    VALIDATE_REQUESTED_RESOURCE_SCOPES_WITH_REGISTERED_RESOURCE_SCOPES)) {
                for (String scope : resource.getResourceScopes()) {
                    resourceScopeStatement.setString(1, scope);
                    try (ResultSet resultSet = resourceScopeStatement.executeQuery()) {
                        if (!resultSet.next()) {
                            throw new UMAResourceException(UMAConstants.ErrorMessages
                                    .ERROR_BAD_REQUEST_INVALID_RESOURCE_SCOPE);
                        }
                    }
                }
            } catch (SQLException e) {
                throw new PermissionDAOException(UMAConstants.ErrorMessages
                        .ERROR_INTERNAL_SERVER_ERROR_FAILED_TO_PERSIST_REQUESTED_PERMISSIONS, e);
            }
        }
    }
}

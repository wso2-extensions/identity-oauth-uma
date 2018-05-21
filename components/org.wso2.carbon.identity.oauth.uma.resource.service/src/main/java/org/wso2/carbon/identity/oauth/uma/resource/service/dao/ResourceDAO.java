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

package org.wso2.carbon.identity.oauth.uma.resource.service.dao;

import com.mysql.jdbc.Statement;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.core.util.IdentityDatabaseUtil;
import org.wso2.carbon.identity.oauth.uma.common.UMAConstants;
import org.wso2.carbon.identity.oauth.uma.common.exception.UMAClientException;
import org.wso2.carbon.identity.oauth.uma.common.exception.UMAServerException;
import org.wso2.carbon.identity.oauth.uma.resource.service.model.Resource;
import org.wso2.carbon.identity.oauth.uma.resource.service.model.ScopeDataDO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Layer functionality for Resource management. This includes storing, updating, deleting
 * and retrieving resources.
 */
public class ResourceDAO {

    private static final String ICON_URI = "icon_uri";
    private static final String DESCRIPTION = "description";
    private static final String TYPE = "type";
    private static final Log log = LogFactory.getLog(ResourceDAO.class);

    /**
     * Add a resource
     *
     * @param resource details of the registered resource
     * @return resourceId of registered resource description
     * @throws UMAServerException ResourceException
     */
    public static Resource registerResource(Resource resource, String resourceOwnerName, int tenantId,
                                            String consumerKey) throws UMAServerException, UMAClientException {

        try (Connection connection = IdentityDatabaseUtil.getDBConnection()) {
            connection.setAutoCommit(false);
            Savepoint savepoint = connection.setSavepoint();
            if (checkExsistanceOfSameResourceName(resourceOwnerName, resource.getName())) {
                throw new UMAClientException(UMAConstants.ErrorMessages
                        .ERROR_CODE_RESOURCE_NAME_DUPLICATE, "Duplicate resource name: " + resource.getName());
            } else {
                PreparedStatement preparedStatement = connection.prepareStatement(SQLQueries.STORE_RESOURCE_DETAILS,
                        Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, resource.getResourceId());
                preparedStatement.setString(2, resource.getName());
                preparedStatement.setTimestamp(3, resource.getTimecreated());
                preparedStatement.setString(4, resourceOwnerName);
                preparedStatement.setInt(5, tenantId);
                preparedStatement.setString(6, consumerKey);
                preparedStatement.execute();

                try (ResultSet resultSet1 = preparedStatement.getGeneratedKeys()) {
                    long id = 0;
                    if (resultSet1.next()) {
                        id = resultSet1.getLong(1);
                    }
                    mapMetaDataWithResource(connection, id, resource);
                    mapScopeWithResource(connection, id, resource.getScopeDataDOArray());
                    connection.commit();
                    if (log.isDebugEnabled()) {
                        log.debug("Successfully added the resource details to the database.");
                    }
                } catch (SQLException e) {
                    try {
                        connection.rollback(savepoint);
                    } catch (SQLException e1) {
                        throw new UMAServerException(UMAConstants.ErrorMessages.ERROR_CODE_FAIL_TO_GET_RESOURCE,
                                "Rollback error. Could not rollback purpose adding.", e);
                    }
                    throw new UMAServerException(UMAConstants.ErrorMessages.ERROR_CODE_FAIL_TO_GET_RESOURCE,
                            "Database error. Could not add resource details.", e);
                }
            }
        } catch (SQLException e) {
            throw new UMAServerException(UMAConstants.ErrorMessages.ERROR_CODE_FAIL_TO_GET_RESOURCE,
                    "Database error. Could not add resource details.", e);
        }
        return resource;
    }

    private static void mapMetaDataWithResource(Connection connection, long id, Resource resourceRegistation)
            throws UMAServerException {

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(SQLQueries.STORE_RESOURCE_META_DETAILS);
            preparedStatement.setLong(1, id);
            if (StringUtils.isNotEmpty(resourceRegistation.getDescription())) {
                preparedStatement.setString(2, DESCRIPTION);
                preparedStatement.setString(3, resourceRegistation.getDescription());
                preparedStatement.execute();
            }
            if (StringUtils.isNotEmpty(resourceRegistation.getType())) {
                preparedStatement.setString(2, TYPE);
                preparedStatement.setString(3, resourceRegistation.getType());
                preparedStatement.execute();
            }
            if (StringUtils.isNotEmpty(resourceRegistation.getIconUri())) {
                preparedStatement.setString(2, ICON_URI);
                preparedStatement.setString(3, resourceRegistation.getDescription());
                preparedStatement.execute();
            }

        } catch (SQLException e) {
            throw new UMAServerException("Database error. Could not map metadata to resource.", e);
        }
    }

    private static void mapScopeWithResource(Connection connection, long id, List<ScopeDataDO>
            scopeData) throws UMAServerException {

        try {
            for (ScopeDataDO scopeDataDO : scopeData) {
                PreparedStatement preparedStatement = connection.prepareStatement(SQLQueries.STORE_SCOPES);
                preparedStatement.setLong(1, id);
                preparedStatement.setString(2, scopeDataDO.getScopeName());
                preparedStatement.execute();
            }
        } catch (SQLException e) {
            throw new UMAServerException("Database error. Could not map scope to resource.", e);
        }
    }

    /**
     * Get a resource by resourceId
     *
     * @param resourceid Id of the resource
     * @return resource description for the provided ID
     * @throws UMAServerException
     */
    public static Resource retrieveResource(String resourceid) throws UMAServerException, UMAClientException {

        Resource resourceRegistration = null;
        try (Connection connection = IdentityDatabaseUtil.getDBConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(SQLQueries.RETRIEVE_RESOURCES_BY_ID);
            preparedStatement.setString(1, resourceid);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                throw new UMAClientException(UMAConstants.ErrorMessages
                        .ERROR_CODE_NOT_FOUND_RESOURCE_ID, "Resource id : " + resourceid + " not found.");
            } else {
                resourceRegistration = new Resource();
                do {
                    if (!resourceRegistration.getScopes().contains(resultSet.getString(5))) {
                        resourceRegistration.getScopes().add(resultSet.getString(5));
                    }

                    if (resultSet.getString(1) != null) {
                        resourceRegistration.setResourceId(resultSet.getString(1));
                    }
                    if (resultSet.getString(2) != null) {
                        resourceRegistration.setName(resultSet.getString(2));
                    }
                    if (resultSet.getString("PROPERTY_KEY").equals(ICON_URI)) {
                        resourceRegistration.setIconUri(resultSet.getString("PROPERTY_VALUE"));
                    }
                    if (resultSet.getString("PROPERTY_KEY").equals(TYPE)) {
                        resourceRegistration.setType(resultSet.getString("PROPERTY_VALUE"));
                    }
                    if (resultSet.getString("PROPERTY_KEY").equals(DESCRIPTION)) {
                        resourceRegistration.setDescription(resultSet.getString("PROPERTY_VALUE"));
                    }
                } while (resultSet.next());
            }
            if (log.isDebugEnabled()) {
                log.debug("Successfully retrieved the resource details from the database.");
            }
        } catch (SQLException e) {
            throw new UMAServerException(UMAConstants.ErrorMessages.ERROR_CODE_NOT_FOUND_RESOURCE_ID, "Database" +
                    "error.Could not get resource.Resource Id can not be found in data base: " + resourceid, e);

        }
        return resourceRegistration;
    }

    /**
     * Get all available resources
     *
     * @param resourceOwnerName ResourceOwner name
     * @return available resource list
     * @throws UMAServerException
     */
    public static List<String> retrieveResourceIDs(String resourceOwnerName, String consumerKey)
            throws UMAServerException {

        List<String> resourceSetIdList = new ArrayList<>();
        try (Connection connection = IdentityDatabaseUtil.getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(SQLQueries.GET_ALL_RESOURCES)) {
                preparedStatement.setString(1, resourceOwnerName);
                preparedStatement.setString(2, consumerKey);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {

                    while (resultSet.next()) {
                        String resourceId = resultSet.getString(1);
                        resourceSetIdList.add(resourceId);
                    }
                }
            }
            connection.commit();
            if (log.isDebugEnabled()) {
                log.debug("Successfully listed the resourceID's in the database.");
            }
        } catch (SQLException e) {
            throw new UMAServerException(UMAConstants.ErrorMessages.ERROR_CODE_FAIL_TO_GET_RESOURCE, "Database " +
                    "error.Could not obtain resource List.", e);
        }
        return resourceSetIdList;
    }

    /**
     * Delete a resource description of the provided resource ID
     *
     * @param resourceId Resource ID of the resource
     * @throws UMAServerException
     */
    public static boolean deleteResource(String resourceId) throws UMAServerException {

        try (Connection connection = IdentityDatabaseUtil.getDBConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(SQLQueries.DELETE_RESOURCE_SCOPES);
            preparedStatement.setString(1, resourceId);
            preparedStatement.executeUpdate();
            preparedStatement = connection.prepareStatement(SQLQueries.DELETE_RESOURCE_META_DETAILS);
            preparedStatement.setString(1, resourceId);
            preparedStatement.executeUpdate();
            preparedStatement = connection.prepareStatement(SQLQueries.DELETE_RESOURCE_DETAILS);
            preparedStatement.setString(1, resourceId);
            int rowsAffected = preparedStatement.executeUpdate();
            connection.commit();
            if (log.isDebugEnabled()) {
                log.debug("Successfully deleted the resource details from the database.");
            }
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new UMAServerException(UMAConstants.ErrorMessages.ERROR_CODE_FAIL_TO_GET_RESOURCE,
                    "Database error. Could not delete resource categories:", e);
        }
    }

    /**
     * Update a resource of the provided resource ID
     *
     * @param resourceRegistration details of the updated resource
     * @param resourceId           Resource ID of the resource
     * @throws UMAServerException
     */
    public static boolean updateResource(String resourceId, Resource resourceRegistration)
            throws UMAServerException {

        try (Connection connection = IdentityDatabaseUtil.getDBConnection()) {
            Savepoint savepoint = null;
            try {
                connection.setAutoCommit(false);
                savepoint = connection.setSavepoint();
                PreparedStatement preparedStatement = connection.prepareStatement(SQLQueries.DELETE_SCOPES);
                preparedStatement.setString(1, resourceId);
                preparedStatement.execute();
                preparedStatement = connection.prepareStatement(SQLQueries.UPDATE_SCOPES);
                preparedStatement.setString(1, resourceId);
                for (String scopes : resourceRegistration.getScopes()) {
                    preparedStatement.setString(2, scopes);
                    preparedStatement.execute();
                }

                preparedStatement = connection.prepareStatement(SQLQueries.UPDATE_METADATA);
                preparedStatement.setString(2, resourceId);
                if (StringUtils.isNotEmpty(resourceRegistration.getDescription())) {
                    preparedStatement.setString(3, DESCRIPTION);
                    preparedStatement.setString(1, resourceRegistration.getDescription());
                    preparedStatement.execute();
                }
                if (StringUtils.isNotEmpty(resourceRegistration.getType())) {
                    preparedStatement.setString(3, TYPE);
                    preparedStatement.setString(1, resourceRegistration.getType());
                    preparedStatement.execute();
                }
                if (StringUtils.isNotEmpty(resourceRegistration.getIconUri())) {
                    preparedStatement.setString(3, ICON_URI);
                    preparedStatement.setString(1, resourceRegistration.getIconUri());
                    preparedStatement.execute();
                }
                preparedStatement = connection.prepareStatement(SQLQueries.UPDATE_RESOURCE);
                preparedStatement.setString(2, resourceId);
                preparedStatement.setString(1, resourceRegistration.getName());
                preparedStatement.execute();

                connection.commit();
                if (log.isDebugEnabled()) {
                    log.debug("Successfully updated the resource details to the database.");
                }
            } catch (SQLException e) {
                try {
                    connection.rollback(savepoint);
                } catch (SQLException e1) {
                    throw new UMAServerException(UMAConstants.ErrorMessages.ERROR_CODE_FAIL_TO_GET_RESOURCE,
                            "Rollback error. Could not rollback purpose adding.", e);
                }
                throw new UMAServerException(UMAConstants.ErrorMessages.ERROR_CODE_FAIL_TO_GET_RESOURCE,
                        "Database error. Could not add resource details.", e);
            }
        } catch (SQLException e) {
            throw new UMAServerException(UMAConstants.ErrorMessages.ERROR_CODE_FAIL_TO_GET_RESOURCE,
                    "Database error. Could not add resource details.", e);
        }
        return false;
    }

    private static boolean checkExsistanceOfSameResourceName(String resourceOwnerName, String resourceName)
            throws UMAServerException {

        try (Connection connection = IdentityDatabaseUtil.getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement
                    (SQLQueries.CHECK_EXISTANCE_OF_RESOURCE_NAME)) {
                preparedStatement.setString(1, resourceOwnerName);
                preparedStatement.setString(2, resourceName);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return true;
                    }
                }
            }

        } catch (SQLException e) {
            throw new UMAServerException(UMAConstants.ErrorMessages
                    .INTERNAL_SERVER_ERROR_FAILED_TO_PERSIST_REQUESTED_RESOURCES,
                    "Database error. Could not identify a resource.", e);
        }

        return false;
    }
}

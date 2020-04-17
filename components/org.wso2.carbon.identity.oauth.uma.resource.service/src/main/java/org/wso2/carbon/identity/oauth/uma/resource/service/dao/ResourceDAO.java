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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.database.utils.jdbc.NamedJdbcTemplate;
import org.wso2.carbon.database.utils.jdbc.exceptions.DataAccessException;
import org.wso2.carbon.database.utils.jdbc.exceptions.TransactionException;
import org.wso2.carbon.identity.oauth.uma.common.JdbcUtils;
import org.wso2.carbon.identity.oauth.uma.common.UMAConstants;
import org.wso2.carbon.identity.oauth.uma.common.exception.UMAClientException;
import org.wso2.carbon.identity.oauth.uma.common.exception.UMAServerException;
import org.wso2.carbon.identity.oauth.uma.resource.service.model.Resource;
import org.wso2.carbon.identity.oauth.uma.resource.service.model.ScopeDataDO;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Data Access Layer functionality for Resource management. This includes storing, updating, deleting
 * and retrieving resources.
 */
public class ResourceDAO {

    private static final String ICON_URI = "icon_uri";
    private static final String PROPERTY_KEY = "PROPERTY_KEY";
    private static final String PROPERTY_VALUE = "PROPERTY_VALUE";
    private static final String DESCRIPTION = "description";
    private static final String TYPE = "type";
    private static final String UTC = "UTC";
    private static final Log log = LogFactory.getLog(ResourceDAO.class);

    /**
     * Add a resource
     *
     * @param resource details of the registered resource
     * @return resourceId of registered resource description
     * @throws UMAServerException ResourceException
     */
    public static Resource registerResource(Resource resource, String resourceOwnerName, int tenantId,
                                            String clientId, String userDomain) throws UMAServerException,
            UMAClientException {

        if (checkDuplicationOfResourceName(resourceOwnerName, userDomain, resource.getName()) != null) {
            throw new UMAClientException(UMAConstants.ErrorMessages
                    .ERROR_CONFLICT_RESOURCE_NAME_DUPLICATE, "Duplicate resource name: " + resource.getName());
        }
        NamedJdbcTemplate namedJdbcTemplate = JdbcUtils.getNewNamedTemplate();
        try {
            namedJdbcTemplate.withTransaction(namedTemplate -> {
                int insertedId = namedTemplate
                        .executeInsert(SQLQueries.STORE_RESOURCE,
                                (namedPreparedStatement -> {
                                    namedPreparedStatement.setString(UMAConstants.SQLPlaceholders.RESOURCE_ID,
                                            resource.getResourceId());
                                    namedPreparedStatement.setString(UMAConstants.SQLPlaceholders.RESOURCE_NAME,
                                            resource.getName());
                                    namedPreparedStatement.setTimeStamp(UMAConstants.SQLPlaceholders.TIME_CREATED,
                                            new Timestamp(new Date().getTime()),
                                            Calendar.getInstance(TimeZone.getTimeZone(UTC)));
                                    namedPreparedStatement.setString(UMAConstants.SQLPlaceholders.RESOURCE_OWNER_NAME,
                                            resourceOwnerName);
                                    namedPreparedStatement.setString(UMAConstants.SQLPlaceholders.CLIENT_ID, clientId);
                                    namedPreparedStatement.setInt(UMAConstants.SQLPlaceholders.TENANT_ID, tenantId);
                                    namedPreparedStatement.setString(UMAConstants.SQLPlaceholders.USER_DOMAIN,
                                            userDomain);
                                }), resource, true);
                storeResourceMetaData(insertedId, resource);
                storeResourceScopes(insertedId, resource.getScopeDataDOArray());
                if (log.isDebugEnabled()) {
                    log.debug("Successfully registered the resource.");
                }
                return resource;
            });
        } catch (TransactionException e) {
            throw new UMAServerException(UMAConstants.ErrorMessages.
                    ERROR_INTERNAL_SERVER_ERROR_FAILED_TO_PERSIST_RESOURCE, e);
        }
        return resource;
    }

    /**
     * Get a resource by resourceId
     *
     * @param resourceId Id of the resource
     * @return resource description for the provided ID
     * @throws UMAServerException
     */
    public static Resource retrieveResource(String resourceId) throws UMAServerException, UMAClientException {

        Resource resource = new Resource();
        NamedJdbcTemplate namedJdbcTemplate = JdbcUtils.getNewNamedTemplate();
        Integer id;
        try {
            id = checkResourceExistence(resourceId);

            resource.setResourceId(resourceId);

            String name = namedJdbcTemplate.fetchSingleRecord(SQLQueries.GET_RESOURCE_NAME, (resultSet, i) ->
                    resultSet.getString(1), namedPreparedStatement -> {
                namedPreparedStatement.setString(UMAConstants.SQLPlaceholders.RESOURCE_ID, resourceId);
            });
            if (name != null) {
                resource.setName(name);
            }

            List<String> scopes = new ArrayList<>();
            namedJdbcTemplate.executeQuery(SQLQueries.GET_RESOURCE_SCOPES, (resultSet, rowNumber) ->
                    scopes.add(resultSet.getString(1)), namedPreparedStatement ->
                    namedPreparedStatement.setInt(UMAConstants.SQLPlaceholders.ID, id));

            resource.setScopes(scopes);

            namedJdbcTemplate.executeQuery(SQLQueries.GET_RESOURCE_META_DATA, (resultSet, rowNumber) -> {
                        if (resultSet.getString(PROPERTY_KEY).equals(ICON_URI)) {
                            resource.setIconUri(resultSet.getString(PROPERTY_VALUE));
                        }
                        if (resultSet.getString(PROPERTY_KEY).equals(TYPE)) {
                            resource.setType(resultSet.getString(PROPERTY_VALUE));
                        }
                        if (resultSet.getString(PROPERTY_KEY).equals(DESCRIPTION)) {
                            resource.setDescription(resultSet.getString(PROPERTY_VALUE));
                        }
                        return null;
                    }, namedPreparedStatement -> namedPreparedStatement.setInt(UMAConstants.SQLPlaceholders.ID, id)
            );
            if (log.isDebugEnabled()) {
                log.debug("Successfully retrieved resource description for resource: " + resourceId);
            }

        } catch (DataAccessException e) {
            throw new UMAServerException(UMAConstants.ErrorMessages
                    .ERROR_INTERNAL_SERVER_ERROR_FAILED_TO_GET_RESOURCE, e);
        }

        return resource;
    }

    /**
     * Get all available resources
     *
     * @param resourceOwnerName name of the resource owner.
     * @param userDomain        user store domain of the resource owner.
     * @param clientId          client id representing the resource server.
     * @return available resource list
     * @throws UMAServerException
     */
    public static List<String> retrieveResourceIDs(String resourceOwnerName, String userDomain, String clientId)
            throws UMAServerException {

        List<String> resourceIdsList = new ArrayList<>();
        NamedJdbcTemplate namedJdbcTemplate = JdbcUtils.getNewNamedTemplate();
        try {
            namedJdbcTemplate.executeQuery(SQLQueries.GET_ALL_RESOURCES,
                    (resultSet, rowNumber) -> resourceIdsList.add(resultSet.getString(1)),
                    namedPreparedStatement -> {
                        namedPreparedStatement.setString(UMAConstants.SQLPlaceholders.RESOURCE_OWNER_NAME,
                                resourceOwnerName);
                        namedPreparedStatement.setString(UMAConstants.SQLPlaceholders.USER_DOMAIN, userDomain);
                        namedPreparedStatement.setString(UMAConstants.SQLPlaceholders.CLIENT_ID, clientId);
                    });
            if (log.isDebugEnabled()) {
                log.debug("Successfully listed the resource ids in the database for resource owner: " +
                        resourceOwnerName + "in user domain: " + userDomain);
            }
        } catch (DataAccessException e) {
            throw new UMAServerException(UMAConstants.ErrorMessages
                    .ERROR_INTERNAL_SERVER_ERROR_FAILED_TO_LIST_RESOURCES, e);
        }
        return resourceIdsList;
    }

    /**
     * Delete a resource description of the provided resource ID
     *
     * @param resourceId Resource ID of the resource
     * @throws UMAServerException
     */
    public static boolean deleteResource(String resourceId) throws UMAServerException, UMAClientException {

        try {
            Integer id = checkResourceExistence(resourceId);
            deleteResourceData(id);
            if (log.isDebugEnabled()) {
                log.debug("Successfully deleted resource description for resource id: " + resourceId);
            }
        } catch (DataAccessException | TransactionException e) {
            throw new UMAServerException(UMAConstants.ErrorMessages
                    .ERROR_INTERNAL_SERVER_ERROR_FAILED_TO_DELETE_RESOURCE, e);
        }

        return true;

    }

    /**
     * Update a resource of the provided resource ID
     *
     * @param resourceRegistration details of the updated resource
     * @param resourceId           Resource ID of the resource
     * @throws UMAServerException
     */
    public static boolean updateResource(String resourceId, Resource resourceRegistration) throws UMAServerException,
            UMAClientException {

        NamedJdbcTemplate namedJdbcTemplate = JdbcUtils.getNewNamedTemplate();
        try {
            Integer id = checkResourceExistence(resourceId);
            try {
                namedJdbcTemplate.withTransaction(namedTemplate -> {
                    deleteScope(id);
                    storeResourceScopes(id, resourceRegistration.getScopeDataDOArray());
                    deleteMeta(id);
                    storeResourceMetaData(id, resourceRegistration);
                    updateResourceDetails(id, resourceRegistration);
                    return true;
                });
            } catch (TransactionException e) {
                throw new UMAServerException(UMAConstants.ErrorMessages
                        .ERROR_INTERNAL_SERVER_ERROR_FAILED_TO_UPDATE_RESOURCE, e);
            }
            if (log.isDebugEnabled()) {
                log.debug("Successfully updated resource description for resource id: " + resourceId);
            }

        } catch (DataAccessException e) {
            throw new UMAServerException(UMAConstants.ErrorMessages
                    .ERROR_INTERNAL_SERVER_ERROR_FAILED_TO_DELETE_RESOURCE, e);
        }

        return true;
    }

    private static void deleteScope(int id) throws UMAServerException, TransactionException {

        NamedJdbcTemplate namedJdbcTemplate = JdbcUtils.getNewNamedTemplate();

        namedJdbcTemplate.withTransaction(namedTemplate -> {
            namedTemplate.executeUpdate(SQLQueries.DELETE_RESOURCE_SCOPES, namedPreparedStatement ->
                    namedPreparedStatement.setInt(UMAConstants.SQLPlaceholders.ID, id));
            return null;
        });
    }

    private static void deleteMeta(int id) throws UMAServerException, TransactionException {

        NamedJdbcTemplate namedJdbcTemplate = JdbcUtils.getNewNamedTemplate();
        namedJdbcTemplate.withTransaction(namedTemplate -> {
            namedTemplate.executeUpdate(SQLQueries.DELETE_RESOURCE_META_DETAILS, namedPreparedStatement ->
                    namedPreparedStatement.setInt(UMAConstants.SQLPlaceholders.ID, id));
            return null;
        });

    }

    private static void deleteResourceData(int id) throws UMAServerException, DataAccessException,
            TransactionException {

        NamedJdbcTemplate namedJdbcTemplate = JdbcUtils.getNewNamedTemplate();

        namedJdbcTemplate.withTransaction(namedTemplate -> {
            namedTemplate.executeUpdate(SQLQueries.DELETE_RESOURCE, namedPreparedStatement ->
                    namedPreparedStatement.setInt(UMAConstants.SQLPlaceholders.ID, id));
            return null;
        });
    }

    private static void updateResourceDetails(int id, Resource resourceRegistration) throws
            UMAServerException, TransactionException {

        NamedJdbcTemplate namedJdbcTemplate = JdbcUtils.getNewNamedTemplate();

        namedJdbcTemplate.withTransaction(namedTemplate -> {
            namedTemplate.executeUpdate(SQLQueries.UPDATE_RESOURCE, namedPreparedStatement ->
            {
                namedPreparedStatement.setInt(UMAConstants.SQLPlaceholders.ID, id);
                namedPreparedStatement.setString(UMAConstants.SQLPlaceholders.RESOURCE_NAME,
                        resourceRegistration.getName());
            });
            return null;
        });
    }

    private static String checkDuplicationOfResourceName(String resourceOwnerName, String userDomain,
                                                         String resourceName) throws UMAServerException {

        String name;
        NamedJdbcTemplate namedJdbcTemplate = JdbcUtils.getNewNamedTemplate();
        try {
            name = namedJdbcTemplate.fetchSingleRecord(SQLQueries.CHECK_RESOURCE_NAME_EXISTENCE,
                    (resultSet, rowNumber) -> resultSet.getString(1), namedPreparedStatement -> {
                        namedPreparedStatement.setString(UMAConstants.SQLPlaceholders.RESOURCE_NAME, resourceName);
                        namedPreparedStatement.setString(UMAConstants.SQLPlaceholders.RESOURCE_OWNER_NAME,
                                resourceOwnerName);
                        namedPreparedStatement.setString(UMAConstants.SQLPlaceholders.USER_DOMAIN, userDomain);
                    });
        } catch (DataAccessException e) {
            throw new UMAServerException(UMAConstants.ErrorMessages
                    .ERROR_INTERNAL_SERVER_ERROR_FAILED_TO_PERSIST_RESOURCE, e);
        }
        return name;
    }

    private static void storeResourceMetaData(int id, Resource resourceRegistation)
            throws UMAServerException, TransactionException {

        NamedJdbcTemplate namedJdbcTemplate = JdbcUtils.getNewNamedTemplate();
        if (StringUtils.isNotEmpty(resourceRegistation.getDescription())) {
            namedJdbcTemplate.withTransaction(namedTemplate -> {
                namedTemplate.executeInsert(SQLQueries.STORE_RESOURCE_META_DETAILS, (namedPreparedStatement -> {
                    namedPreparedStatement.setLong(UMAConstants.SQLPlaceholders.ID, id);
                    namedPreparedStatement.setString(UMAConstants.SQLPlaceholders.PROPERTY_KEY,
                            DESCRIPTION);
                    namedPreparedStatement.setString(UMAConstants.SQLPlaceholders.PROPERTY_VALUE,
                            resourceRegistation.getDescription());
                }), resourceRegistation, false);
                return null;
            });
        }
        if (StringUtils.isNotEmpty(resourceRegistation.getIconUri())) {
            namedJdbcTemplate.withTransaction(namedTemplate -> {
                namedTemplate.executeInsert(SQLQueries.STORE_RESOURCE_META_DETAILS, (namedPreparedStatement -> {
                    namedPreparedStatement.setLong(UMAConstants.SQLPlaceholders.ID, id);
                    namedPreparedStatement.setString(UMAConstants.SQLPlaceholders.PROPERTY_KEY, ICON_URI);
                    namedPreparedStatement.setString(UMAConstants.SQLPlaceholders.PROPERTY_VALUE,
                            resourceRegistation.getIconUri());
                }), resourceRegistation, false);
                return null;
            });
        }
        if (StringUtils.isNotEmpty(resourceRegistation.getType())) {
            namedJdbcTemplate.withTransaction(namedTemplate -> {
                namedTemplate.executeInsert(SQLQueries.STORE_RESOURCE_META_DETAILS, (namedPreparedStatement -> {
                    namedPreparedStatement.setLong(UMAConstants.SQLPlaceholders.ID, id);
                    namedPreparedStatement.setString(UMAConstants.SQLPlaceholders.PROPERTY_KEY, TYPE);
                    namedPreparedStatement.setString(UMAConstants.SQLPlaceholders.PROPERTY_VALUE,
                            resourceRegistation.getType());
                }), resourceRegistation, false);
                return null;
            });
        }
    }

    private static void storeResourceScopes(int id, List<ScopeDataDO> scopeData) throws UMAServerException,
            TransactionException {

        NamedJdbcTemplate namedJdbcTemplate = JdbcUtils.getNewNamedTemplate();
        for (ScopeDataDO scopeDataDO : scopeData) {
            namedJdbcTemplate.withTransaction(namedTemplate -> namedTemplate.executeInsert(
                    SQLQueries.STORE_RESOURCE_SCOPES, (namedPreparedStatement -> {
                        namedPreparedStatement.setLong(UMAConstants.SQLPlaceholders.ID, id);
                        namedPreparedStatement.setString(UMAConstants.SQLPlaceholders.SCOPE_NAME,
                                scopeDataDO.getScopeName());
                    }), null, false));
        }
    }

    private static Integer checkResourceExistence(String resourceId) throws DataAccessException, UMAClientException {

        NamedJdbcTemplate namedJdbcTemplate = JdbcUtils.getNewNamedTemplate();
        Integer id;
        id = namedJdbcTemplate.fetchSingleRecord(SQLQueries.CHECK_RESOURCE_ID_EXISTENCE, (resultSet, rowNumber) ->
                resultSet.getInt(1), namedPreparedStatement -> {
            namedPreparedStatement.setString(UMAConstants.SQLPlaceholders.RESOURCE_ID, resourceId);
        });
        if (id == null) {
            throw new UMAClientException(UMAConstants.ErrorMessages
                    .ERROR_NOT_FOUND_RESOURCE_ID, "Resource id : " + resourceId + " not found.");
        }
        return id;
    }

    /**
     * Check whether the resource belongs to the given user.
     *
     * @param resourceId resource ID of the resource.
     * @param userName   name of the user.
     * @param userDomain user store domain of the user.
     * @param clientId   client id representing the resource server.
     * @throws UMAServerException
     */
    public static boolean isResourceOwner(String resourceId, String userName, String userDomain,
            String clientId) throws UMAServerException {

        NamedJdbcTemplate namedJdbcTemplate = JdbcUtils.getNewNamedTemplate();
        Integer id;
        try {
            id = namedJdbcTemplate.fetchSingleRecord(SQLQueries.CHECK_RESOURCE_OWNER,
                    (resultSet, rowNumber) -> resultSet.getInt(1), namedPreparedStatement -> {
                        namedPreparedStatement.setString(UMAConstants.SQLPlaceholders.RESOURCE_ID, resourceId);
                        namedPreparedStatement.setString(UMAConstants.SQLPlaceholders.RESOURCE_OWNER_NAME, userName);
                        namedPreparedStatement.setString(UMAConstants.SQLPlaceholders.USER_DOMAIN, userDomain);
                        namedPreparedStatement.setString(UMAConstants.SQLPlaceholders.CLIENT_ID, clientId);
                    });
        } catch (DataAccessException e) {
            throw new UMAServerException(UMAConstants.ErrorMessages.ERROR_INTERNAL_SERVER_ERROR_FAILED_TO_GET_RESOURCE,
                    e);
        }
        if (id != null) {
            return true;
        }
        return false;
    }
}

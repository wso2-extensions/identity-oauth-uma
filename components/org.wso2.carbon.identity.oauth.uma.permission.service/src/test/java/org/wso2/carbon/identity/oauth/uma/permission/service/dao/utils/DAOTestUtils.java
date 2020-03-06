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

package org.wso2.carbon.identity.oauth.uma.permission.service.dao.utils;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang.StringUtils;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;

/**
 * DB Utils.
 */
public class DAOTestUtils {

    private static Map<String, BasicDataSource> dataSourceMap = new HashMap<>();
    private static final String STORE_RESOURCE_QUERY = "INSERT INTO IDN_UMA_RESOURCE (ID, RESOURCE_ID, RESOURCE_NAME, "
            + "TIME_CREATED, RESOURCE_OWNER_NAME, CLIENT_ID, TENANT_ID, USER_DOMAIN) VALUES (?,?,?,?,?,?,?,?)";
    private static final String STORE_RESOURCE_SCOPE_QUERY = "INSERT INTO IDN_UMA_RESOURCE_SCOPE (ID, " +
            "RESOURCE_IDENTITY, SCOPE_NAME) VALUES (?,?,?)";
    private static final String STORE_PT_QUERY = "INSERT INTO IDN_UMA_PERMISSION_TICKET " +
            "(ID, PT, TIME_CREATED, EXPIRY_TIME, TICKET_STATE, TENANT_ID) VALUES (?,?,?,?,?,?)";
    private static final String STORE_PT_RESOURCE_IDS_QUERY = "INSERT INTO IDN_UMA_PT_RESOURCE " +
            "(ID, PT_RESOURCE_ID, PT_ID) VALUES (?, ?, ?)";
    private static final String STORE_PT_RESOURCE_SCOPES_QUERY = "INSERT INTO IDN_UMA_PT_RESOURCE_SCOPE " +
            "(ID, PT_RESOURCE_ID, PT_SCOPE_ID) VALUES (?, ?, ?)";

    /**
     * Create H2 database.
     *
     * @param databaseName Database name.
     * @param scriptPath   File path for the database script.
     * @throws Exception   Exception.
     */
    public static void initiateH2Base(String databaseName, String scriptPath) throws Exception {

        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUsername("username");
        dataSource.setPassword("password");
        dataSource.setUrl("jdbc:h2:mem:test" + databaseName);
        try (Connection connection = dataSource.getConnection()) {
            connection.createStatement().executeUpdate("RUNSCRIPT FROM '" + scriptPath + "'");
        }
        dataSourceMap.put(databaseName, dataSource);
    }

    /**
     * Close H2 database connection.
     *
     * @param databaseName Database name.
     * @throws Exception   Exception.
     */
    public static void closeH2Base(String databaseName) throws Exception {

        BasicDataSource dataSource = dataSourceMap.get(databaseName);
        if (dataSource != null) {
            dataSource.close();
        }
    }

    /**
     * Obtain the database connection.
     *
     * @param database Database name.
     * @return database connection.
     * @throws SQLException SQLException.
     */
    public static Connection getConnection(String database) throws SQLException {

        if (dataSourceMap.get(database) != null) {
            return dataSourceMap.get(database).getConnection();
        }
        throw new RuntimeException("No datasource initiated for database: " + database);
    }

    /**
     * Spy the database connection for unit testing.
     *
     * @param connection Database connection.
     * @return spy connection object.
     * @throws SQLException SQLException.
     */
    public static Connection spyConnection(Connection connection) throws SQLException {

        Connection spy = spy(connection);
        doNothing().when(spy).close();
        return spy;
    }

    /**
     * Get file path for the database scripts.
     *
     * @param fileName Database script file name.
     * @return file path.
     */
    public static String getFilePath(String fileName) {

        if (StringUtils.isNotBlank(fileName)) {
            return Paths.get(System.getProperty("user.dir"), "src", "test", "resources", "dbScripts", fileName)
                    .toString();
        }
        throw new IllegalArgumentException("DB Script file name cannot be empty.");
    }

    /**
     * Get datasource initiated for database.
     *
     * @param datasourceName Datasource name.
     * @return Datasource.
     */
    public static BasicDataSource getDatasource(String datasourceName) {

        if (dataSourceMap.get(datasourceName) != null) {
            return dataSourceMap.get(datasourceName);
        }
        throw new RuntimeException("No datasource initiated for database: " + datasourceName);
    }

    /**
     * Store the permission ticket in database.
     *
     * @param databaseName Database name.
     * @param id           Permission ticket auto incremented id.
     * @param pt           Permission ticket identifier.
     * @param createdTime  Permission ticket creation time.
     * @param expiredTime  Permission ticket expiry time.
     * @param state        Current state of the permission ticket.
     * @param tenantId     Tenant ID.
     * @throws Exception   Exception.
     */
    public static void storePT(String databaseName, long id, String pt, Timestamp createdTime,
                               Timestamp expiredTime, String state, int tenantId) throws Exception {

        PreparedStatement preparedStatement = null;
        try (Connection connection = getConnection(databaseName)) {
            preparedStatement = connection.prepareStatement(STORE_PT_QUERY);
            preparedStatement.setLong(1, id);
            preparedStatement.setString(2, pt);
            preparedStatement.setTimestamp(3, createdTime);
            preparedStatement.setTimestamp(4, expiredTime);
            preparedStatement.setString(5, state);
            preparedStatement.setLong(6, tenantId);
            preparedStatement.execute();
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
    }

    /**
     * Store resources represented by the permission ticket.
     *
     * @param databaseName Database name.
     * @param id           Auto incremented id for the entry.
     * @param ptResourceId Resource ID.
     * @param ptId         Permission ticket ID.
     * @throws Exception   Exception.
     */
    public static void storePTResources(String databaseName, long id, long ptResourceId, long ptId) throws
            Exception {

        PreparedStatement preparedStatement = null;
        try (Connection connection = getConnection(databaseName)) {
            preparedStatement = connection.prepareStatement(STORE_PT_RESOURCE_IDS_QUERY);
            preparedStatement.setLong(1, id);
            preparedStatement.setLong(2, ptResourceId);
            preparedStatement.setLong(3, ptId);
            preparedStatement.execute();
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
    }

    /**
     * Store resource scopes of the resources represented by the permission ticket.
     *
     * @param databaseName Database name.
     * @param id           Auto incremented id for the entry.
     * @param ptResourceId Resource ID.
     * @param scopeId      Resource scope ID.
     * @throws Exception Exception.
     */
    public static void storePTResourceScopes(String databaseName, long id, long ptResourceId, long scopeId) throws
            Exception {

        PreparedStatement preparedStatement = null;
        try (Connection connection = getConnection(databaseName)) {
            preparedStatement = connection.prepareStatement(STORE_PT_RESOURCE_SCOPES_QUERY);
            preparedStatement.setLong(1, id);
            preparedStatement.setLong(2, ptResourceId);
            preparedStatement.setLong(3, scopeId);
            preparedStatement.execute();
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
    }

    /**
     * Store UMA protected resource.
     *
     * @param databaseName      Database name.
     * @param id                Auto incremented id for the entry.
     * @param resourceId        Resource identifier.
     * @param resourceName      Resource name.
     * @param timecreated       Resource registration time.
     * @param resourceOwnerName Resource owner name.
     * @param clientId          Client ID of the resource server.
     * @param tenantId          Tenant ID.
     * @param userDomain        Userstore domain.
     * @throws Exception Exception.
     */
    public static void storeResourceTable(String databaseName, long id, String resourceId, String resourceName,
                                          Timestamp timecreated, String resourceOwnerName, String clientId,
                                          long tenantId, String userDomain) throws Exception {

        PreparedStatement preparedStatement = null;
        try (Connection connection = getConnection(databaseName)) {
            preparedStatement = connection.prepareStatement(STORE_RESOURCE_QUERY);
            preparedStatement.setLong(1, id);
            preparedStatement.setString(2, resourceId);
            preparedStatement.setString(3, resourceName);
            preparedStatement.setTimestamp(4, timecreated);
            preparedStatement.setString(5, resourceOwnerName);
            preparedStatement.setString(6, clientId);
            preparedStatement.setLong(7, tenantId);
            preparedStatement.setString(8, userDomain);
            preparedStatement.execute();
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
    }

    /**
     * Store resource scopes for the protected resource.
     *
     * @param databaseName     Database name.
     * @param id               Auto incremented id for the entry.
     * @param resourceIdentity Resource identifier.
     * @param scopeName        Resource scope name.
     * @throws Exception Exception.
     */
    public static void storeResourceScopes(String databaseName, long id, long resourceIdentity, String scopeName)
            throws Exception {

        PreparedStatement preparedStatement = null;
        try (Connection connection = getConnection(databaseName)) {
            preparedStatement = connection.prepareStatement(STORE_RESOURCE_SCOPE_QUERY);
            preparedStatement.setLong(1, id);
            preparedStatement.setLong(2, resourceIdentity);
            preparedStatement.setString(3, scopeName);
            preparedStatement.execute();
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
    }
}

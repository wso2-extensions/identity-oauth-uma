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

package org.wso2.carbon.identity.oauth.uma.resource.service.dao.util;

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

public class DAOUtils {

    private static Map<String, BasicDataSource> dataSourceMap = new HashMap<>();
    public static final String STORE_RESOURCE_DETAILS =
            "INSERT INTO IDN_UMA_RESOURCE(RESOURCE_ID,RESOURCE_NAME,TIME_CREATED," +
                    "RESOURCE_OWNER_NAME,TENANT_ID,CLIENT_ID,USER_DOMAIN) VALUES (?,?,?,?,?,?,?)";

    public static final String STORE_RESOURCE_META_DETAILS =
            "INSERT INTO IDN_UMA_RESOURCE_META_DATA(RESOURCE_IDENTITY,PROPERTY_KEY,PROPERTY_VALUE)" +
                    "VALUES ((SELECT ID FROM IDN_UMA_RESOURCE WHERE ID = ?),?,?);";

    public static final String STORE_SCOPES =
            "INSERT INTO IDN_UMA_RESOURCE_SCOPE(RESOURCE_IDENTITY,SCOPE_NAME) VALUES ((SELECT ID FROM " +
                    "IDN_UMA_RESOURCE WHERE ID = ?),?)";

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

    public static void closeH2Base(String databaseName) throws Exception {

        BasicDataSource dataSource = dataSourceMap.get(databaseName);
        if (dataSource != null) {
            dataSource.close();
        }
    }

    public static Connection getConnection(String database) throws SQLException {

        if (dataSourceMap.get(database) != null) {
            return dataSourceMap.get(database).getConnection();
        }
        throw new RuntimeException("No datasource initiated for database: " + database);
    }

    public static String getFilePath(String fileName) {

        if (StringUtils.isNotBlank(fileName)) {
            return Paths.get(System.getProperty("user.dir"), "src", "test", "resources", "dbscripts", fileName)
                    .toString();
        }
        throw new IllegalArgumentException("DB Script file name cannot be empty.");
    }

    public static void createResourceTable(String databaseName, String resourceId, String resourceName,
                                           Timestamp timecreated, String resourceOwnerName, int tenantId,
                                           String consumerKey, String userDomain) throws Exception {

        PreparedStatement preparedStatement = null;
        try (Connection connection = getConnection(databaseName)) {
            preparedStatement = connection.prepareStatement(STORE_RESOURCE_DETAILS);
            preparedStatement.setString(1, resourceId);
            preparedStatement.setString(2, resourceName);
            preparedStatement.setTimestamp(3, timecreated);
            preparedStatement.setString(4, resourceOwnerName);
            preparedStatement.setInt(5, tenantId);
            preparedStatement.setString(6, consumerKey);
            preparedStatement.setString(7, userDomain);
            preparedStatement.execute();
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
    }

    public static void createResourceMetaDataTable(String databaseName, String propertyKey, String propertyValue,
                                                   Long resourceIdFK) throws Exception {

        PreparedStatement preparedStatement = null;
        try (Connection connection = getConnection(databaseName)) {
            preparedStatement = connection.prepareStatement(STORE_RESOURCE_META_DETAILS);
            preparedStatement.setString(2, propertyKey);
            preparedStatement.setString(3, propertyValue);
            preparedStatement.setLong(1, resourceIdFK);
            preparedStatement.execute();
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
    }

    public static void createResourceScopeTable(String database, Long resourceScopeIdFK, String scopeName)
            throws Exception {

        PreparedStatement preparedStatement = null;
        try (Connection connection = getConnection(database)) {
            preparedStatement = connection.prepareStatement(STORE_SCOPES);
            preparedStatement.setLong(1, resourceScopeIdFK);
            preparedStatement.setString(2, scopeName);
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
    }

    public static Connection spyConnection(Connection connection) throws SQLException {

        Connection spy = spy(connection);
        doNothing().when(spy).close();
        return spy;
    }
}

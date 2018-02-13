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

package org.wso2.carbon.identity.oauth.uma.service.dao.util;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang.StringUtils;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class DAOUtils {

    private static Map<String, BasicDataSource> dataSourceMap = new HashMap<>();
    public static final String STORE_RESOURCE_DETAILS =
            "INSERT INTO IDN_RESOURCE(RESOURCE_ID,RESOURCE_NAME,TIME_CREATED," +
                    "RESOUCE_OWNER_ID,TENANT_ID) VALUES (?,?,?,?,?)";

    public static final String STORE_RESOURCE_META_DETAILS =
            "INSERT INTO IDN_RESOURCE_META_DATA(FK_RESOURCE_ID_META_DATA,PROPERTY_KEY,PROPERTY_VALUE)" +
                    "VALUES ((SELECT ID FROM IDN_RESOURCE WHERE ID = ?),?,?);";

    public static final String STORE_SCOPES =
            "INSERT INTO IDN_RESOURCE_SCOPE(FK_RESOURCE_ID,SCOPE_NAME) VALUES ((SELECT ID FROM IDN_RESOURCE WHERE " +
                    "ID = ?),?)";

    protected void initiateH2Base(String databaseName, String scriptPath) throws Exception {

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
    protected void closeH2Base(String databaseName) throws Exception {

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

    protected void createResourceTable(String databaseName, String resourceId, String resourceName,
                                       Timestamp timecreated, String resourceOwnerId, long tenantId) throws Exception {

        PreparedStatement preparedStatement = null;
        try (Connection connection = getConnection(databaseName)) {
            preparedStatement = connection.prepareStatement(STORE_RESOURCE_DETAILS);
            preparedStatement.setString(1, resourceId);
            preparedStatement.setString(2, resourceName);
            preparedStatement.setTimestamp(3, timecreated);
            preparedStatement.setString(4, resourceOwnerId);
            preparedStatement.setLong(5, tenantId);
            preparedStatement.execute();
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
    }

    protected void createResourceMetaDataTable(String databaseName, String propertyKey, String propertyValue,
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

    protected void createResourceScopeTable(String database, Long resourceScopeIdFK, String scopeName)
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
}

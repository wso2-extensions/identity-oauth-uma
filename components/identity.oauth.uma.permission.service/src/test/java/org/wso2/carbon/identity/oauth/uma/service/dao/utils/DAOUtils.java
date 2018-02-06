/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.identity.oauth.uma.service.dao.utils;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang.StringUtils;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * DB Utils.
 */
public class DAOUtils {

    private static Map<String, BasicDataSource> dataSourceMap = new HashMap<>();
    private static final String STORE_RESOURCE_QUERY = "INSERT INTO IDN_RESOURCE (ID, RESOURCE_ID, RESOURCE_NAME, " +
            "TIME_CREATED, RESOURCE_OWNER_ID, TENANT_ID) VALUES (?,?,?,?,?,?)";
    private static final String STORE_RESOURCE_SCOPE_QUERY = "INSERT INTO IDN_RESOURCE_SCOPE (ID, RESOURCE_IDENTITY, " +
            "SCOPE_NAME) VALUES (?,?,?)";
    private static final String STORE_PT_QUERY = "INSERT INTO IDN_PERMISSION_TICKET " +
            "(ID, PT, TIME_CREATED, VALIDITY_PERIOD, TICKET_STATE, TENANT_ID) VALUES (?,?,?,?,?,?)";
    private static final String STORE_PT_RESOURCE_IDS_QUERY = "INSERT INTO IDN_PT_RESOURCE " +
            "(ID, PT_RESOURCE_ID, PT_ID) VALUES " +
            "(?, ?, ?)";
    private static final String STORE_PT_RESOURCE_SCOPES_QUERY = "INSERT INTO IDN_PT_RESOURCE_SCOPE " +
            "(ID, PT_RESOURCE_ID, PT_SCOPE_ID) VALUES (?, ?, ?)";

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
            return Paths.get(System.getProperty("user.dir"), "src", "test", "resources", "dbScripts", fileName)
                    .toString();
        }
        throw new IllegalArgumentException("DB Script file name cannot be empty.");
    }

    public static BasicDataSource getDatasource(String datasourceName) {
        if (dataSourceMap.get(datasourceName) != null) {
            return dataSourceMap.get(datasourceName);
        }
        throw new RuntimeException("No datasource initiated for database: " + datasourceName);
    }

    protected void createPTTable(String databaseName, long id, String pt, Timestamp timecreated, long period,
                                 String state, long tenantid) throws Exception {
        PreparedStatement preparedStatement = null;
        try (Connection connection = getConnection(databaseName)) {
            preparedStatement = connection.prepareStatement(STORE_PT_QUERY);
            preparedStatement.setLong(1, id);
            preparedStatement.setString(2, pt);
            preparedStatement.setTimestamp(3, timecreated);
            preparedStatement.setLong(4, period);
            preparedStatement.setString(5, state);
            preparedStatement.setLong(6, tenantid);
            preparedStatement.execute();
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
    }

    protected void createPTResourceTable(String databaseName, long id, long ptResourceId, long ptId) throws Exception {
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

    protected void createPTResourceScopeTable(String databaseName, long id, long ptResourceId, long scopeId) throws
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

    protected void createResourceTable(String databaseName, long id, String resourceId, String resourceName,
                                       Timestamp timecreated, String resourceOwnerId, long tenantId) throws Exception {
        PreparedStatement preparedStatement = null;
        try (Connection connection = getConnection(databaseName)) {
            preparedStatement = connection.prepareStatement(STORE_RESOURCE_QUERY);
            preparedStatement.setLong(1, id);
            preparedStatement.setString(2, resourceId);
            preparedStatement.setString(3, resourceName);
            preparedStatement.setTimestamp(4, timecreated);
            preparedStatement.setString(5, resourceOwnerId);
            preparedStatement.setLong(6, tenantId);
            preparedStatement.execute();
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
    }

    protected void createResourceScopeTable(String databaseName, long id, long resourceIdentity, String scopeName)
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

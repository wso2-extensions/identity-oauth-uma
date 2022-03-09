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

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.IObjectFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;
import org.wso2.carbon.database.utils.jdbc.NamedJdbcTemplate;
import org.wso2.carbon.identity.oauth.uma.common.JdbcUtils;
import org.wso2.carbon.identity.oauth.uma.common.exception.UMAException;
import org.wso2.carbon.identity.oauth.uma.permission.service.TestConstants;
import org.wso2.carbon.identity.oauth.uma.permission.service.dao.utils.DAOTestUtils;
import org.wso2.carbon.identity.oauth.uma.permission.service.model.PermissionTicketModel;
import org.wso2.carbon.identity.oauth.uma.permission.service.model.Resource;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.sql.DataSource;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Unit tests for PermissionTicketDAO.
 */
@PrepareForTest(JdbcUtils.class)
public class PermissionTicketDAOTest extends PowerMockTestCase {

    private static final String DB_NAME = "UMA_DB";
    private Timestamp createdTime = new Timestamp(System.currentTimeMillis());
    private Timestamp expiredTime = new Timestamp(System.currentTimeMillis() + 300000);

    @BeforeClass
    public void setUp() throws Exception {

        DAOTestUtils.initiateH2Base(DB_NAME, DAOTestUtils.getFilePath("permission.sql"));
        DAOTestUtils.storeResourceTable(DB_NAME, 1, "1", "photo01", createdTime,
                TestConstants.RESOURCE_OWNER_NAME, TestConstants.CLIENT_ID, TestConstants.TENANT_ID,
                TestConstants.USER_DOMAIN);
        DAOTestUtils.storeResourceScopes(DB_NAME, 1, 1, "scope01");
        DAOTestUtils.storePT(DB_NAME, "12345", createdTime, expiredTime, TestConstants.TICKET_STATE,
                TestConstants.TENANT_ID);
        DAOTestUtils.storePTResources(DB_NAME, 1, 1);
        DAOTestUtils.storePTResourceScopes(DB_NAME, 1, 1);
    }

    @AfterClass
    public void tearDown() throws Exception {

        DAOTestUtils.closeH2Base(DB_NAME);
    }

    @ObjectFactory
    public IObjectFactory getObjectFactory() {

        return new org.powermock.modules.testng.PowerMockObjectFactory();
    }

    @Test
    public void testPersistPermissionTicket() throws Exception {

        DataSource dataSource = mock(DataSource.class);
        mockStatic(JdbcUtils.class);
        when(JdbcUtils.getNewNamedTemplate()).thenReturn(new NamedJdbcTemplate(dataSource));
        try (Connection connection = DAOTestUtils.getConnection(DB_NAME)) {
            Connection spy = DAOTestUtils.spyConnection(connection);
            when(dataSource.getConnection()).thenReturn(spy);
            List<Resource> list = new ArrayList<>();
            list.add(getResource());
            PermissionTicketDAO.persistPermissionTicket(list, getPermissionTicketDO(),
                    TestConstants.RESOURCE_OWNER_NAME, TestConstants.CLIENT_ID, TestConstants.USER_DOMAIN);
        }

    }

    /**
     * Test persisting a permission with an invalid resource id.
     */
    @Test(expectedExceptions = UMAException.class)
    public void testPersistInvalidResourceId() throws Exception {

        DataSource dataSource = mock(DataSource.class);
        mockStatic(JdbcUtils.class);
        when(JdbcUtils.getNewNamedTemplate()).thenReturn(new NamedJdbcTemplate(dataSource));
        try (Connection connection = DAOTestUtils.getConnection(DB_NAME)) {
            Connection spy = DAOTestUtils.spyConnection(connection);
            when(dataSource.getConnection()).thenReturn(spy);
            List<Resource> list = new ArrayList<>();
            list.add(getResourceWithInvalidResourceId());
            PermissionTicketDAO.persistPermissionTicket(list, getPermissionTicketDO(),
                    TestConstants.RESOURCE_OWNER_NAME, TestConstants.CLIENT_ID, TestConstants.USER_DOMAIN);
        }
    }

    /**
     * Test persisting a permission with an invalid resource scope.
     */
    @Test(expectedExceptions = UMAException.class)
    public void testPersistInvalidResourceScope() throws Exception {

        DataSource dataSource = mock(DataSource.class);
        mockStatic(JdbcUtils.class);
        when(JdbcUtils.getNewNamedTemplate()).thenReturn(new NamedJdbcTemplate(dataSource));
        try (Connection connection = DAOTestUtils.getConnection(DB_NAME)) {
            Connection spy = DAOTestUtils.spyConnection(connection);
            when(dataSource.getConnection()).thenReturn(spy);
            List<Resource> list = new ArrayList<>();
            list.add(getResourceWithInvalidResourceScope());
            PermissionTicketDAO.persistPermissionTicket(list, getPermissionTicketDO(),
                    TestConstants.RESOURCE_OWNER_NAME, TestConstants.CLIENT_ID, TestConstants.USER_DOMAIN);
        }
    }

    private PermissionTicketModel getPermissionTicketDO() {

        PermissionTicketModel permissionTicketModel = new PermissionTicketModel();
        permissionTicketModel.setTicket(UUID.randomUUID().toString());
        permissionTicketModel.setStatus(TestConstants.TICKET_STATE);
        permissionTicketModel.setCreatedTime(createdTime);
        permissionTicketModel.setExpiryTime(expiredTime);
        permissionTicketModel.setTenantId(TestConstants.TENANT_ID);
        return permissionTicketModel;
    }

    private Resource getResource() {

        Resource resource = new Resource();
        resource.setResourceId("1");
        List<String> resourceScopeList = new ArrayList<>();
        resourceScopeList.add("scope01");
        resource.setResourceScopes(resourceScopeList);
        return resource;
    }

    private Resource getResourceWithInvalidResourceId() {

        Resource resource = new Resource();
        // Invalid resource ID.
        resource.setResourceId("10");
        List<String> resourceScopeList = new ArrayList<>();
        resourceScopeList.add("scope01");
        resource.setResourceScopes(resourceScopeList);
        return resource;
    }

    private Resource getResourceWithInvalidResourceScope() {

        Resource resource = new Resource();
        resource.setResourceId("1");
        List<String> resourceScopeList = new ArrayList<>();
        // Invalid resource scope.
        resourceScopeList.add("scope02");
        resource.setResourceScopes(resourceScopeList);
        return resource;
    }
}

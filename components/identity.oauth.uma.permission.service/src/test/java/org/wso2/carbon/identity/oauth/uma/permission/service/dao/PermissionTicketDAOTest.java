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
import org.testng.IObjectFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.core.util.IdentityDatabaseUtil;
import org.wso2.carbon.identity.oauth.uma.permission.service.dao.utils.DAOTestUtils;
import org.wso2.carbon.identity.oauth.uma.permission.service.exception.UMAException;
import org.wso2.carbon.identity.oauth.uma.permission.service.model.PermissionTicketDO;
import org.wso2.carbon.identity.oauth.uma.permission.service.model.Resource;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Unit tests for PermissionTicketDAO.
 */
@PrepareForTest(IdentityDatabaseUtil.class)
public class PermissionTicketDAOTest extends DAOTestUtils {

    private static final String DB_NAME = "UMA_DB";

    @BeforeClass
    public void setUp() throws Exception {

        initiateH2Base(DB_NAME, getFilePath("permission.sql"));
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        createResourceTable(DB_NAME, 1, "1", "photo01", timestamp, "owner1",
                "1234", "carbon.super");
        createResourceScopeTable(DB_NAME, 1, 1, "scope01");
        createPTTable(DB_NAME, 1, "12345", timestamp, 3600000, "ACTIVE", "carbon.super");
        createPTResourceTable(DB_NAME, 1, 1, 1);
        createPTResourceScopeTable(DB_NAME, 1, 1, 1);
    }

    @AfterClass
    public void tearDown() throws Exception {

        closeH2Base(DB_NAME);
    }

    @ObjectFactory
    public IObjectFactory getObjectFactory() {

        return new org.powermock.modules.testng.PowerMockObjectFactory();
    }

    @Test
    public void testPersist() throws Exception {

        mockStatic(IdentityDatabaseUtil.class);
        try (Connection connection = DAOTestUtils.getConnection(DB_NAME)) {
            when(IdentityDatabaseUtil.getDBConnection()).thenReturn(connection);
            List<Resource> list = new ArrayList<>();
            list.add(getResource());
            PermissionTicketDAO.persistPTandRequestedPermissions(list, getPermissionTicketDO());
        }
    }

    /**
     * Test persisting a permission with an invalid resource id.
     */
    @Test(expectedExceptions = UMAException.class)
    public void testPersistInvalidResourceId() throws Exception {

        mockStatic(IdentityDatabaseUtil.class);
        try (Connection connection = DAOTestUtils.getConnection(DB_NAME)) {
            when(IdentityDatabaseUtil.getDBConnection()).thenReturn(connection);
            List<Resource> list = new ArrayList<>();
            list.add(getResourceWithInvalidResourceId());
            PermissionTicketDAO.persistPTandRequestedPermissions(list, getPermissionTicketDO());
        }
    }

    /**
     * Test persisting a permission with an invalid resource scope.
     */
    @Test(expectedExceptions = UMAException.class)
    public void testPersistInvalidResourceScope() throws Exception {

        mockStatic(IdentityDatabaseUtil.class);
        try (Connection connection = DAOTestUtils.getConnection(DB_NAME)) {
            when(IdentityDatabaseUtil.getDBConnection()).thenReturn(connection);
            List<Resource> list = new ArrayList<>();
            list.add(getResourceWithInvalidResourceScope());
            PermissionTicketDAO.persistPTandRequestedPermissions(list, getPermissionTicketDO());
        }
    }

    /**
     * Test persisting an empty resource or empty permission ticket
     */
    @Test(expectedExceptions = UMAException.class)
    public void testPersistEmptyPermission() throws Exception {

        mockStatic(IdentityDatabaseUtil.class);
        try (Connection connection = DAOTestUtils.getConnection(DB_NAME)) {
            when(IdentityDatabaseUtil.getDBConnection()).thenReturn(connection);
            List<Resource> list = new ArrayList<>();
            PermissionTicketDAO.persistPTandRequestedPermissions(list, new PermissionTicketDO());
        }
    }

    private PermissionTicketDO getPermissionTicketDO() {

        PermissionTicketDO permissionTicketDO = new PermissionTicketDO();
        permissionTicketDO.setTicket(UUID.randomUUID().toString());
        permissionTicketDO.setStatus("ACTIVE");
        permissionTicketDO.setCreatedTime(Calendar.getInstance(TimeZone.getTimeZone("UTC")));
        permissionTicketDO.setValidityPeriod(3600000);
        permissionTicketDO.setTenantDomain("carbon.super");
        return permissionTicketDO;
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

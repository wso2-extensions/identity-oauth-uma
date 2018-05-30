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
import org.wso2.carbon.identity.oauth.uma.resource.service.dao.util.DAOUtils;
import org.wso2.carbon.identity.oauth.uma.resource.service.model.Resource;

import java.sql.Connection;
import java.sql.Timestamp;
import javax.sql.DataSource;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;


@PrepareForTest(JdbcUtils.class)
public class ResourceDAOTest extends PowerMockTestCase {

    private static final String DB_NAME = "regdb";
    private int tenantId = -1234;
    private String resourceOwnerName = "admin";
    private String clientId = "1234";
    private String resourceId = "1";
    private String userDomain = "primary";

    @BeforeClass
    public void setUp() throws Exception {

        DAOUtils.initiateH2Base(DB_NAME, DAOUtils.getFilePath("resource.sql"));
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        DAOUtils.createResourceTable(DB_NAME, resourceId, "PhotoAlbum", timestamp, resourceOwnerName,
                tenantId, clientId, userDomain);
        DAOUtils.createResourceMetaDataTable(DB_NAME, "icon_uri",
                "http://www.example.com/icons/sharesocial.png", (long) 1);
        DAOUtils.createResourceScopeTable(DB_NAME, (long) 1, "view");
    }

    @AfterClass
    public void tearDown() throws Exception {

        DAOUtils.closeH2Base(DB_NAME);
    }

    @ObjectFactory
    public IObjectFactory getObjectFactory() {

        return new org.powermock.modules.testng.PowerMockObjectFactory();
    }

    @Test
    public void testRegisterResource() throws Exception {

        DataSource dataSource = mock(DataSource.class);
        mockStatic(JdbcUtils.class);
        when(JdbcUtils.getNewNamedTemplate()).thenReturn(new NamedJdbcTemplate(dataSource));
        try (Connection connection = DAOUtils.getConnection(DB_NAME)) {
            Connection spy = DAOUtils.spyConnection(connection);
            when(dataSource.getConnection()).thenReturn(spy);
            ResourceDAO.registerResource(new Resource(), resourceOwnerName, tenantId, clientId, userDomain);
        }

    }

    @Test(priority = 1)
    public void testRetrieveResource() throws Exception {

        DataSource dataSource = mock(DataSource.class);
        mockStatic(JdbcUtils.class);
        when(JdbcUtils.getNewNamedTemplate()).thenReturn(new NamedJdbcTemplate(dataSource));
        try (Connection connection = DAOUtils.getConnection(DB_NAME)) {
            Connection spy = DAOUtils.spyConnection(connection);
            when(dataSource.getConnection()).thenReturn(spy);
            ResourceDAO.retrieveResource(resourceId);
        }
    }

    /**
     * Test retrieving with a invalid resource id.
     */
    @Test(expectedExceptions = UMAException.class)
    public void testRetrieveResourceWithInvalidResourceId() throws Exception {

        DataSource dataSource = mock(DataSource.class);
        mockStatic(JdbcUtils.class);
        when(JdbcUtils.getNewNamedTemplate()).thenReturn(new NamedJdbcTemplate(dataSource));
        try (Connection connection = DAOUtils.getConnection(DB_NAME)) {
            Connection spy = DAOUtils.spyConnection(connection);
            when(dataSource.getConnection()).thenReturn(spy);
            ResourceDAO.retrieveResource("1234");
        }
    }

    @Test
    public void testRetrieveResourceIDs() throws Exception {

        DataSource dataSource = mock(DataSource.class);
        mockStatic(JdbcUtils.class);
        when(JdbcUtils.getNewNamedTemplate()).thenReturn(new NamedJdbcTemplate(dataSource));
        try (Connection connection = DAOUtils.getConnection(DB_NAME)) {
            Connection spy = DAOUtils.spyConnection(connection);
            when(dataSource.getConnection()).thenReturn(spy);
            ResourceDAO.retrieveResourceIDs(resourceOwnerName, userDomain, clientId);
        }
    }

    @Test(priority = 3)
    public void testDeleteResource() throws Exception {

        DataSource dataSource = mock(DataSource.class);
        mockStatic(JdbcUtils.class);
        when(JdbcUtils.getNewNamedTemplate()).thenReturn(new NamedJdbcTemplate(dataSource));
        try (Connection connection = DAOUtils.getConnection(DB_NAME)) {
            Connection spy = DAOUtils.spyConnection(connection);
            when(dataSource.getConnection()).thenReturn(spy);
            ResourceDAO.deleteResource(resourceId);
        }
    }

    /**
     * Test deleting with a invalid resource id.
     */
    @Test(expectedExceptions = UMAException.class)
    public void testDeleteResourceWithInvalidResourceId() throws Exception {

        DataSource dataSource = mock(DataSource.class);
        mockStatic(JdbcUtils.class);
        when(JdbcUtils.getNewNamedTemplate()).thenReturn(new NamedJdbcTemplate(dataSource));
        try (Connection connection = DAOUtils.getConnection(DB_NAME)) {
            Connection spy = DAOUtils.spyConnection(connection);
            when(dataSource.getConnection()).thenReturn(spy);
            ResourceDAO.deleteResource("1234");
        }
    }

    @Test(priority = 2)
    public void testUpdateResource() throws Exception {

        DataSource dataSource = mock(DataSource.class);
        mockStatic(JdbcUtils.class);
        when(JdbcUtils.getNewNamedTemplate()).thenReturn(new NamedJdbcTemplate(dataSource));
        try (Connection connection = DAOUtils.getConnection(DB_NAME)) {
            Connection spy = DAOUtils.spyConnection(connection);
            when(dataSource.getConnection()).thenReturn(spy);
            ResourceDAO.updateResource(resourceId, new Resource());
        }
    }

    /**
     * Test updating with a invalid resource id.
     */
    @Test(expectedExceptions = UMAException.class)
    public void testUpdateResourceWithInvalidResourceId() throws Exception {

        DataSource dataSource = mock(DataSource.class);
        mockStatic(JdbcUtils.class);
        when(JdbcUtils.getNewNamedTemplate()).thenReturn(new NamedJdbcTemplate(dataSource));
        try (Connection connection = DAOUtils.getConnection(DB_NAME)) {
            Connection spy = DAOUtils.spyConnection(connection);
            when(dataSource.getConnection()).thenReturn(spy);
            ResourceDAO.updateResource("1234", new Resource());
        }
    }

}

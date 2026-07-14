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

package org.wso2.carbon.identity.oauth.uma.resource.service.impl;

import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.oauth.uma.resource.service.dao.ResourceDAO;
import org.wso2.carbon.identity.oauth.uma.resource.service.model.Resource;

import static org.mockito.Mockito.mockStatic;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

public class ResourceServiceImplTest {

    private static final String RESOURCE_ID = "123454-62552-31456";
    private static final String RESOURCE_OWNER_NAME = "admin";
    private static final int TENANT_ID = -1234;
    private static final String CLIENT_ID = "1234";
    private static final String USERSTORE_DOMAIN = "primary";
    private static Resource resource = new Resource();

    private ResourceServiceImpl resourceService;

    @BeforeMethod
    public void setUp() throws Exception {

        MockitoAnnotations.openMocks(this);
        resourceService = new ResourceServiceImpl();
    }

    @Test
    public void testRegisterResource() throws Exception {

        try (MockedStatic<ResourceDAO> mockedResourceDAO = mockStatic(ResourceDAO.class)) {
            assertNull(resourceService.registerResource(resource, RESOURCE_OWNER_NAME, TENANT_ID, CLIENT_ID,
                    USERSTORE_DOMAIN));
        }
    }

    @Test
    public void testGetResourceIds() throws Exception {

        try (MockedStatic<ResourceDAO> mockedResourceDAO = mockStatic(ResourceDAO.class)) {
            assertNotNull(resourceService.getResourceIds(RESOURCE_OWNER_NAME, USERSTORE_DOMAIN, CLIENT_ID));
        }
    }

    @Test
    public void testGetResourceById() throws Exception {

        try (MockedStatic<ResourceDAO> mockedResourceDAO = mockStatic(ResourceDAO.class)) {
            assertNull(resourceService.getResourceById(RESOURCE_ID));
        }
    }

    @Test
    public void testUpdateResource() throws Exception {

        try (MockedStatic<ResourceDAO> mockedResourceDAO = mockStatic(ResourceDAO.class)) {
            assertNotNull(resourceService.updateResource(RESOURCE_ID, resource));
        }
    }

    @Test
    public void testDeleteResource() throws Exception {

        try (MockedStatic<ResourceDAO> mockedResourceDAO = mockStatic(ResourceDAO.class)) {
            assertNotNull(resourceService.deleteResource(""));
        }
    }
}

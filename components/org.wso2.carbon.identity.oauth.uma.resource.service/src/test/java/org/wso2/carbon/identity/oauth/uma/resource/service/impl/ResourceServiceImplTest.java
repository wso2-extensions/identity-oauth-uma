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

import org.mockito.InjectMocks;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.testng.IObjectFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.oauth.uma.resource.service.dao.ResourceDAO;
import org.wso2.carbon.identity.oauth.uma.resource.service.model.Resource;

import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

@PrepareForTest({ResourceDAO.class})
public class ResourceServiceImplTest {

    private static final String RESOURCE_ID = "123454-62552-31456";
    private static Resource resource = new Resource();

    @InjectMocks
    private ResourceServiceImpl resourceService;

    @BeforeMethod
    public void setUp() throws Exception {

        resourceService = new ResourceServiceImpl();
    }

    @ObjectFactory
    public IObjectFactory getObjectFactory() {

        return new org.powermock.modules.testng.PowerMockObjectFactory();
    }

    @Test
    public void testRegisterResource() throws Exception {

        mockStatic(ResourceDAO.class);
        assertNull(resourceService.registerResource(resource, "admin", -1234,
                "1234", "primary"));
    }

    @Test
    public void testGetResourceIds() throws Exception {

        mockStatic(ResourceDAO.class);
        assertNotNull(resourceService.getResourceIds("admin", "primary",
                "1234"));
    }

    @Test
    public void testGetResourceById() throws Exception {

        mockStatic(ResourceDAO.class);
        assertNull(resourceService.getResourceById(RESOURCE_ID));
    }

    @Test
    public void testUpdateResource() throws Exception {

        mockStatic(ResourceDAO.class);
        assertNotNull(resourceService.updateResource(RESOURCE_ID, resource));
    }

    @Test
    public void testDeleteResource() throws Exception {

        mockStatic(ResourceDAO.class);
        assertNotNull(resourceService.deleteResource(""));
    }
}

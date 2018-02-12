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

package org.wso2.carbon.identity.oauth.uma.permission.service.internal;

import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.osgi.framework.BundleContext;
import org.testng.IObjectFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.oauth.uma.permission.service.impl.PermissionServiceImpl;

import java.util.Dictionary;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.doAnswer;
import static org.testng.Assert.assertEquals;

public class PermissionServiceComponentTest {

    @Mock
    private BundleContext bundleContext;

    @BeforeClass
    public void setUp() throws Exception {

        initMocks(this);
    }

    @AfterMethod
    public void tearDown() throws Exception {

    }

    @ObjectFactory
    public IObjectFactory getObjectFactory() {

        return new org.powermock.modules.testng.PowerMockObjectFactory();
    }

    @Test
    public void testActivate() throws Exception {

        final String[] serviceName = new String[1];
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {

                PermissionServiceImpl permissionService = (PermissionServiceImpl) invocation.getArguments()[1];
                serviceName[0] = permissionService.getClass().getName();
                return null;
            }
        }).when(bundleContext).registerService(anyString(), any(PermissionServiceComponent.class),
                any(Dictionary.class));

        PermissionServiceComponent permissionServiceComponent = new PermissionServiceComponent();
        permissionServiceComponent.activate(bundleContext);
        assertEquals(PermissionServiceImpl.class.getName(), serviceName[0], "error");
    }
}

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

package org.wso2.carbon.identity.oauth.uma.endpoint;

import org.mockito.Mock;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.IObjectFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.context.internal.OSGiDataHolder;
import org.wso2.carbon.identity.oauth.uma.endpoint.dto.ResourceModelDTO;
import org.wso2.carbon.identity.oauth.uma.service.PermissionService;
import org.wso2.carbon.identity.oauth.uma.service.model.PermissionTicketDO;

import javax.ws.rs.core.Response;

import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@PrepareForTest({BundleContext.class, ServiceTracker.class, PrivilegedCarbonContext.class, PermissionService.class})
public class PermissionApiServiceImplTest extends PowerMockTestCase {

    @Mock
    BundleContext mockBundleContext;

    @Mock
    ServiceTracker mockServiceTracker;

    @Mock
    private PermissionService mockPermissionService;

    @Mock
    private PermissionTicketDO mockPermissionTicketDO;

    @ObjectFactory
    public IObjectFactory getObjectFactory() {

        return new org.powermock.modules.testng.PowerMockObjectFactory();
    }

    @BeforeMethod
    public void setUp() throws Exception {
        //Get OSGIservice by starting the tenant flow.
        whenNew(ServiceTracker.class).withAnyArguments().thenReturn(mockServiceTracker);
        TestUtil.startTenantFlow("carbon.super");
        Object[] services = new Object[1];
        services[0] = mockPermissionService;
        when(mockServiceTracker.getServices()).thenReturn(services);
        OSGiDataHolder.getInstance().setBundleContext(mockBundleContext);
    }

    @AfterMethod
    public void tearDown() throws Exception {

    }

    @Test
    public void testRegisterPermission() throws Exception {

        PermissionTicketDO permissionTicketDO = new PermissionTicketDO();
        when(mockPermissionService.issuePermissionTicket(anyList())).thenReturn(permissionTicketDO);
        when(mockPermissionTicketDO.getTicket()).thenReturn(anyString());
        PermissionApiServiceImpl permissionApiService = new PermissionApiServiceImpl();
        ResourceModelDTO resourceModelDTO = new ResourceModelDTO();
        Assert.assertEquals(permissionApiService.requestPermission(resourceModelDTO).getStatus(),
                Response.Status.CREATED.getStatusCode());
    }

}

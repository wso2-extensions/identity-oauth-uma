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

package org.wso2.carbon.identity.oauth.uma.permission.endpoint;

import org.mockito.Mock;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.IObjectFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.context.internal.OSGiDataHolder;
import org.wso2.carbon.identity.oauth.uma.permission.endpoint.dto.ResourceModelDTO;
import org.wso2.carbon.identity.oauth.uma.permission.endpoint.exception.PermissionEndpointException;
import org.wso2.carbon.identity.oauth.uma.permission.service.PermissionService;
import org.wso2.carbon.identity.oauth.uma.permission.service.UMAConstants;
import org.wso2.carbon.identity.oauth.uma.permission.service.exception.PermissionDAOException;
import org.wso2.carbon.identity.oauth.uma.permission.service.exception.UMAResourceException;
import org.wso2.carbon.identity.oauth.uma.permission.service.model.PermissionTicketDO;

import javax.ws.rs.core.Response;

import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@PrepareForTest({BundleContext.class, ServiceTracker.class, PrivilegedCarbonContext.class, PermissionService.class})
public class PermissionApiServiceImplTest extends PowerMockTestCase {

    private PermissionApiServiceImpl permissionApiService = new PermissionApiServiceImpl();

    @Mock
    private BundleContext mockBundleContext;

    @Mock
    private ServiceTracker mockServiceTracker;

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
        ResourceModelDTO resourceModelDTO = new ResourceModelDTO();
        assertEquals(permissionApiService.requestPermission(resourceModelDTO).getStatus(),
                Response.Status.CREATED.getStatusCode());
    }

    @Test
    public void testRegisterPermissionDAOException() throws Exception {

        doThrow(new PermissionDAOException("Server")).when(mockPermissionService).issuePermissionTicket(anyList());
        ResourceModelDTO resourceModelDTO = new ResourceModelDTO();
        try {
            permissionApiService.requestPermission(resourceModelDTO);
        } catch (PermissionEndpointException e) {
            assertEquals(e.getResponse().getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        }
    }

    @Test
    public void testRegisterUMAResourceException() throws Exception {

        UMAResourceException umaResourceException = new UMAResourceException(UMAConstants
                .ErrorMessages.ERROR_BAD_REQUEST_INVALID_RESOURCE_ID);
        doThrow(umaResourceException).when(mockPermissionService).issuePermissionTicket(anyList());
        ResourceModelDTO resourceModelDTO = new ResourceModelDTO();
        try {
            permissionApiService.requestPermission(resourceModelDTO);
        } catch (PermissionEndpointException e) {
            assertTrue(PermissionEndpointConstants.RESPONSE_DATA_MAP.containsKey(
                    umaResourceException.getCode()));
        }
    }

}

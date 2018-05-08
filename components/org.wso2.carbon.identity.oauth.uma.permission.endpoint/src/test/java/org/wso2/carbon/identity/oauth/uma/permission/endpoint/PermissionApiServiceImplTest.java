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

import org.apache.cxf.jaxrs.ext.MessageContext;
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
import org.wso2.carbon.identity.application.common.model.User;
import org.wso2.carbon.identity.auth.service.AuthenticationContext;
import org.wso2.carbon.identity.oauth.uma.common.HandleErrorResponseConstants;
import org.wso2.carbon.identity.oauth.uma.common.UMAConstants;
import org.wso2.carbon.identity.oauth.uma.common.exception.UMAClientException;
import org.wso2.carbon.identity.oauth.uma.common.exception.UMAServerException;
import org.wso2.carbon.identity.oauth.uma.permission.endpoint.dto.ResourceModelDTO;
import org.wso2.carbon.identity.oauth.uma.permission.endpoint.exception.PermissionEndpointException;
import org.wso2.carbon.identity.oauth.uma.permission.service.PermissionService;
import org.wso2.carbon.identity.oauth.uma.permission.service.model.PermissionTicketModel;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.wso2.carbon.identity.oauth.uma.permission.endpoint.PermissionApiServiceImpl.OAUTH2_ALLOWED_SCOPES;
import static org.wso2.carbon.identity.oauth.uma.permission.endpoint.PermissionApiServiceImpl.PAT_SCOPE;

@PrepareForTest({BundleContext.class, ServiceTracker.class, PrivilegedCarbonContext.class, PermissionService.class})
public class PermissionApiServiceImplTest extends PowerMockTestCase {

    private PermissionApiServiceImpl permissionApiService;
    private ResourceModelDTO resourceModelDTO;

    @Mock
    private BundleContext mockBundleContext;

    @Mock
    private ServiceTracker mockServiceTracker;

    @Mock
    private PermissionService mockPermissionService;

    @Mock
    private PermissionTicketModel mockPermissionTicketModel;

    @Mock
    private MessageContext mockMessageContext;

    @Mock
    private AuthenticationContext mockAuthenticationContext;

    @Mock
    private HttpServletRequest mockHTTPServletRequest;

    @ObjectFactory
    public IObjectFactory getObjectFactory() {

        return new org.powermock.modules.testng.PowerMockObjectFactory();
    }

    @BeforeMethod
    public void setUp() throws Exception {

        permissionApiService = new PermissionApiServiceImpl();
        resourceModelDTO = new ResourceModelDTO();
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

        when(mockMessageContext.getHttpServletRequest()).thenReturn(mockHTTPServletRequest);
        when(mockHTTPServletRequest.getAttribute(anyString())).thenReturn(mockAuthenticationContext);
        String[] tokenScopes = new String[]{PAT_SCOPE};
        when(mockAuthenticationContext.getParameter(anyString())).thenReturn(tokenScopes);
        when(mockAuthenticationContext.getUser()).thenReturn(new User());
        PermissionTicketModel permissionTicketModel = new PermissionTicketModel();
        when(mockPermissionService.issuePermissionTicket(anyList(), anyInt(), anyString())).
                thenReturn(permissionTicketModel);
        when(mockPermissionTicketModel.getTicket()).thenReturn("ticket");
        assertEquals(permissionApiService.requestPermission(resourceModelDTO, mockMessageContext).getStatus(),
                Response.Status.CREATED.getStatusCode());
    }

    @Test
    public void testRegisterPermissionDAOException() throws Exception {

        when(mockMessageContext.getHttpServletRequest()).thenReturn(mockHTTPServletRequest);
        when(mockHTTPServletRequest.getAttribute(anyString())).thenReturn(mockAuthenticationContext);
        String[] tokenScopes = new String[]{PAT_SCOPE};
        when(mockAuthenticationContext.getParameter(anyString())).thenReturn(tokenScopes);
        when(mockAuthenticationContext.getUser()).thenReturn(new User());
        doThrow(new UMAServerException("Server")).when(mockPermissionService).issuePermissionTicket(anyList(),
                anyInt(), anyString());
        try {
            permissionApiService.requestPermission(resourceModelDTO, mockMessageContext);
        } catch (PermissionEndpointException e) {
            assertEquals(e.getResponse().getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        }
    }

    @Test
    public void testRegisterUMAResourceException() throws Exception {

        when(mockMessageContext.getHttpServletRequest()).thenReturn(mockHTTPServletRequest);
        when(mockHTTPServletRequest.getAttribute(anyString())).thenReturn(mockAuthenticationContext);
        String[] tokenScopes = new String[]{PAT_SCOPE};
        when(mockAuthenticationContext.getParameter(anyString())).thenReturn(tokenScopes);
        when(mockAuthenticationContext.getUser()).thenReturn(new User());
        UMAClientException umaClientException = new UMAClientException(UMAConstants
                .ErrorMessages.ERROR_BAD_REQUEST_INVALID_RESOURCE_ID);
        doThrow(umaClientException).when(mockPermissionService).issuePermissionTicket(anyList(), anyInt(), anyString());
        try {
            permissionApiService.requestPermission(resourceModelDTO, mockMessageContext);
        } catch (PermissionEndpointException e) {
            assertTrue(HandleErrorResponseConstants.RESPONSE_DATA_MAP.containsKey(
                    umaClientException.getCode()));
        }
    }

    @Test
    public void testRegisterPermissionInvalidPATScope() throws Exception {

        when(mockMessageContext.getHttpServletRequest()).thenReturn(mockHTTPServletRequest);
        when(mockHTTPServletRequest.getAttribute(anyString())).thenReturn(mockAuthenticationContext);
        String[] tokenScopes = new String[]{"uma_protection_fake"};
        when(mockAuthenticationContext.getParameter(OAUTH2_ALLOWED_SCOPES)).thenReturn(tokenScopes);
        assertEquals(permissionApiService.requestPermission(resourceModelDTO, mockMessageContext).getStatus(),
                Response.Status.UNAUTHORIZED.getStatusCode());

    }

    @Test
    public void testRegisterPermissionEmptyRequestBody() throws Exception {

        when(mockMessageContext.getHttpServletRequest()).thenReturn(mockHTTPServletRequest);
        when(mockHTTPServletRequest.getAttribute(anyString())).thenReturn(mockAuthenticationContext);
        String[] tokenScopes = new String[]{PAT_SCOPE};
        when(mockAuthenticationContext.getParameter(anyString())).thenReturn(tokenScopes);
        ResourceModelDTO requestedPermission = null;
        assertEquals(permissionApiService.requestPermission(requestedPermission, mockMessageContext).getStatus(),
                Response.Status.BAD_REQUEST.getStatusCode());

    }

}

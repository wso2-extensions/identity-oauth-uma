/*
 *  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.identity.oauth.uma.resource.endpoint.impl;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.mockito.Mock;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.IObjectFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.context.internal.OSGiDataHolder;
import org.wso2.carbon.identity.application.common.model.User;
import org.wso2.carbon.identity.auth.service.AuthenticationContext;
import org.wso2.carbon.identity.oauth.uma.resource.endpoint.TestUtil;
import org.wso2.carbon.identity.oauth.uma.resource.service.ResourceService;
import org.wso2.carbon.identity.oauth.uma.resource.service.model.Resource;
import org.wso2.carbon.identity.oauth.uma.resource.service.model.ScopeDataDO;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.testng.Assert.assertEquals;

@PrepareForTest({BundleContext.class, ServiceTracker.class, PrivilegedCarbonContext.class, ResourceService.class})
public class ResourceRegistrationApiServiceImplTest extends PowerMockTestCase {

    private ResourceRegistrationApiServiceImpl resourcesApiService = null;
    private Resource resource;
    private final String patScope = "uma_protection";

    @Mock
    private ResourceService resourceService;

    @Mock
    private BundleContext mockBundleContext;

    @Mock
    private ServiceTracker mockServiceTracker;

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

        resourcesApiService = new ResourceRegistrationApiServiceImpl();
        resource = new Resource();

        resourcesApiService = new ResourceRegistrationApiServiceImpl();
        resource = new Resource();
        //Get OSGIservice by starting the tenant flow.
        whenNew(ServiceTracker.class).withAnyArguments().thenReturn(mockServiceTracker);
        TestUtil.startTenantFlow("carbon.super");
        Object[] services = new Object[1];
        services[0] = resourceService;
        when(mockServiceTracker.getServices()).thenReturn(services);
        OSGiDataHolder.getInstance().setBundleContext(mockBundleContext);

        resource.setName("photo_albem");
        List<ScopeDataDO> scopeDataDO = new ArrayList<>();
        ScopeDataDO scopeDataDO1 = new ScopeDataDO();
        scopeDataDO1.setResourceId("67378");
        scopeDataDO.add(scopeDataDO1);
        resource.setScopeDataDOArray(scopeDataDO);
        resource.setIconUri("http://www.example.com/icons/sky.png");
        resource.setDescription("Collection of digital photographs");
        resource.setType("http://www.example.com/rsrcs/photoalbum");
    }

    @Test
    public void testDeleteResource() throws Exception {

        when(mockMessageContext.getHttpServletRequest()).thenReturn(mockHTTPServletRequest);
        when(mockHTTPServletRequest.getAttribute(anyString())).thenReturn(mockAuthenticationContext);
        String[] tokenScopes = new String[]{patScope};
        when(mockAuthenticationContext.getParameter(anyString())).thenReturn(tokenScopes);
        when(resourceService.deleteResource("232e7415-3bcb-4ef9-9527-ac4dacc6aa83")).thenReturn(true);
        assertEquals(resourcesApiService.deleteResource("232e7415-3bcb-4ef9-9527-ac4dacc6aa83",
                mockMessageContext).getStatus(), Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    public void testGetResourceIds() throws Exception {

        when(mockMessageContext.getHttpServletRequest()).thenReturn(mockHTTPServletRequest);
        when(mockHTTPServletRequest.getAttribute(anyString())).thenReturn(mockAuthenticationContext);
        when(mockAuthenticationContext.getUser()).thenReturn(new User());
        List<String> resourceIds = new ArrayList<>();
        when(resourceService.getResourceIds(anyString(), anyString())).thenReturn(resourceIds);
        assertEquals(resourcesApiService.getResourceIds(mockMessageContext).getStatus(),
                Response.Status.UNAUTHORIZED.getStatusCode());
    }

    @Test
    public void testGetResource() throws Exception {

        when(mockMessageContext.getHttpServletRequest()).thenReturn(mockHTTPServletRequest);
        when(mockHTTPServletRequest.getAttribute(anyString())).thenReturn(mockAuthenticationContext);
        String[] tokenScopes = new String[]{patScope};
        when(mockAuthenticationContext.getParameter(anyString())).thenReturn(tokenScopes);
        when(resourceService.getResourceById("232e7415-3bcb-4ef9-9527-ac4dacc6aa83")).thenReturn(resource);
        assertEquals(resourcesApiService.getResource("232e7415-3bcb-4ef9-9527-ac4dacc6aa83",
                mockMessageContext).getStatus(), Response.Status.OK.getStatusCode());
    }
}

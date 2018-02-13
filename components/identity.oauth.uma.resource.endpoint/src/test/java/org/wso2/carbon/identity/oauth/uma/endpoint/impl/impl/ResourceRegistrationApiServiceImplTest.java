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

package org.wso2.carbon.identity.oauth.uma.endpoint.impl.impl;

import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.testng.IObjectFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.oauth.uma.endpoint.dto.CreateResourceDTO;
import org.wso2.carbon.identity.oauth.uma.endpoint.dto.ResourceDetailsDTO;
import org.wso2.carbon.identity.oauth.uma.endpoint.impl.exceptions.ResourceEndpointException;
import org.wso2.carbon.identity.oauth.uma.endpoint.impl.util.ResourceUtils;
import org.wso2.carbon.identity.oauth.uma.service.ResourceService;
import org.wso2.carbon.identity.oauth.uma.service.exceptions.UMAServiceException;
import org.wso2.carbon.identity.oauth.uma.service.model.Resource;
import org.wso2.carbon.identity.oauth.uma.service.model.ScopeDataDO;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.testng.Assert.assertEquals;


@PowerMockIgnore("javax.*")
@PrepareForTest({ResourceUtils.class})
public class ResourceRegistrationApiServiceImplTest {

    private ResourceRegistrationApiServiceImpl resourcesApiService = null;
    private Resource resource;

    @Mock
    private ResourceService resourceService;

    @ObjectFactory
    public IObjectFactory getObjectFactory() {

        return new org.powermock.modules.testng.PowerMockObjectFactory();
    }

    @BeforeMethod
    public void setUp() throws Exception {

        resourcesApiService = new ResourceRegistrationApiServiceImpl();
        resource = new Resource();
        resource.setName("photo_albem");
        List<ScopeDataDO> scopeDataDO = new ArrayList<>();
        ScopeDataDO scopeDataDO1 = new ScopeDataDO();
        scopeDataDO1.setResourceId("67378");
        scopeDataDO.add(scopeDataDO1);
        resource.setScopeDataDOArr(scopeDataDO);
        resource.setIconUri("http://www.example.com/icons/sky.png");
        resource.setDescription("Collection of digital photographs");
        resource.setType("http://www.example.com/rsrcs/photoalbum");
    }

    @Test
    public void testRegisterResource() throws Exception {

        ResourceDetailsDTO resourceDetailsDTO = new ResourceDetailsDTO();
        CreateResourceDTO resourceDTO = new CreateResourceDTO();
        resourceDetailsDTO.setName("photo_albem");
        List<String> scopes = new ArrayList<>();
        scopes.add("scope1");
        resourceDetailsDTO.setResource_scopes(scopes);
        resourceDetailsDTO.setDescription("Collection of digital photographs");
        resourceDetailsDTO.setIcon_uri("http://www.example.com/icons/sky.png");
        resourceDetailsDTO.setType("http://www.example.com/rsrcs/photoalbum");
        try {
            resourcesApiService.registerResource(resourceDetailsDTO);
        } catch (ResourceEndpointException e) {
            assertEquals(e.getResponse().getStatus(), Response.Status.OK.getStatusCode());
        }
    }

    @Test
    public void testGetResource() throws Exception {

        when(resourceService.getResourceById("98765543-09888")).thenThrow
                (new UMAServiceException("This is a server exception"));

        try {
            resourcesApiService.getResource("98765543-09888");
        } catch (ResourceEndpointException e) {
            assertEquals(e.getResponse().getStatus(), Response.Status.NOT_FOUND.getStatusCode());
        }

    }

    @Test
    public void testGetResourceIds() throws Exception {

        List<String> resourceOwnerIds = new ArrayList<>();
/*
        when(resourceService.getResourceIds("123")).thenThrow(new UMAServiceException("This is a server exception"));

        try {
            resourcesApiService.getResourceIds("123");
        }catch (ResourceEndpointException e){
            assertEquals(e.getResponse().getStatus(), Response.Status.NOT_FOUND.getStatusCode());
        }*/
    }

    @Test
    public void testUpdateResource() throws Exception {

        ResourceDetailsDTO updateRequestDTO = new ResourceDetailsDTO();
        doThrow(new UMAServiceException("server")).when(resourceService).updateResource(any(String.class),
                any(Resource.class));
        try {
            resourcesApiService.updateResource("98765543-09888", updateRequestDTO);
        } catch (ResourceEndpointException e) {
            assertEquals(e.getResponse().getStatus(), Response.Status.NOT_FOUND.getStatusCode());
        }
    }

    @Test
    public void testDeleteResource() throws Exception {

        doThrow(new UMAServiceException("server")).when(resourceService).deleteResource(any(String.class));
        try {
            resourcesApiService.deleteResource("98765543-09888");
        } catch (ResourceEndpointException e) {
            assertEquals(e.getResponse().getStatus(), Response.Status.NOT_FOUND.getStatusCode());
        }

    }
}

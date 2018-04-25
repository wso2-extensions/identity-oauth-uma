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

package org.wso2.carbon.identity.oauth.uma.resource.endpoint.util;

import org.testng.annotations.Test;
import org.wso2.carbon.identity.oauth.uma.resource.endpoint.dto.CreateResourceDTO;
import org.wso2.carbon.identity.oauth.uma.resource.endpoint.dto.ListReadResourceDTO;
import org.wso2.carbon.identity.oauth.uma.resource.endpoint.dto.ReadResourceDTO;
import org.wso2.carbon.identity.oauth.uma.resource.endpoint.dto.ResourceDetailsDTO;
import org.wso2.carbon.identity.oauth.uma.resource.endpoint.dto.UpdateResourceDTO;
import org.wso2.carbon.identity.oauth.uma.resource.service.model.Resource;
import org.wso2.carbon.identity.oauth.uma.resource.service.model.ScopeDataDO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;

public class ResourceUtilsTest {

    @Test
    public void testGetResource() throws Exception {

        ResourceDetailsDTO resourceDetailsDTO = new ResourceDetailsDTO();
        resourceDetailsDTO.setName("photo_albem");
        List<String> scopes = new ArrayList<>();
        scopes.add("scope1");
        resourceDetailsDTO.setResource_Scopes(scopes);
        resourceDetailsDTO.setDescription("Collection of digital photographs");
        resourceDetailsDTO.setIcon_Uri("http://www.example.com/icons/sky.png");
        resourceDetailsDTO.setType("http://www.example.com/rsrcs/photoalbum");
        Resource resource = ResourceUtils.getResource(resourceDetailsDTO);
        assertEquals(resource.getScopes(), resourceDetailsDTO.getResource_Scopes(), "Actual scopes are not " +
                "match for expected scopes");
        assertEquals(resource.getIconUri(), resourceDetailsDTO.getIcon_Uri(), "Actual IconUri  match " +
                "for expected IconUri");
        assertEquals(resource.getType(), resourceDetailsDTO.getType(), "Actual type  match for expected type");
        assertEquals(resource.getDescription(), resourceDetailsDTO.getDescription(), "Actual description" +
                " match for expected " +
                "description");
        assertEquals(resource.getName(), resourceDetailsDTO.getName(), "Actual name  match for expected name");
    }

    @Test
    public void testReadResponse() throws Exception {

        Resource resource = new Resource();
        resource.setName("photo_albem");
        List<ScopeDataDO> scopeDataDO = new ArrayList<>();
        ScopeDataDO scopeDataDO1 = new ScopeDataDO();
        scopeDataDO1.setResourceId("67378");
        scopeDataDO.add(scopeDataDO1);
        resource.setScopeDataDOArray(scopeDataDO);
        resource.setIconUri("http://www.example.com/icons/sky.png");
        resource.setDescription("Collection of digital photographs");
        resource.setType("http://www.example.com/rsrcs/photoalbum");

        ReadResourceDTO readResourceDTO = ResourceUtils.readResponse(resource);
        assertNotEquals(readResourceDTO.getResource_scope(), resource.getScopeDataDOArray(), "Actual scopes " +
                "are not match for expected scopes");
        assertEquals(readResourceDTO.getIcon_uri(), resource.getIconUri(), "Actual IconUri  match for" +
                " expected IconUri");
        assertEquals(readResourceDTO.getType(), resource.getType(), "Actual type  match for expected type");
        assertEquals(readResourceDTO.getDescription(), resource.getDescription(), "Actual description " +
                "match for expected description");
        assertEquals(readResourceDTO.getName(), resource.getName(), "Actual name  match for expected name");
    }

    @Test
    public void testCreateResponse() throws Exception {

        Resource resource = new Resource();
        String resourceId = UUID.randomUUID().toString();
        resource.setResourceId(resourceId);

        CreateResourceDTO createResourceDTO = ResourceUtils.createResponse(resource);
        assertEquals(createResourceDTO.getResourceId(), resource.getResourceId(), "Actual resourceId match" +
                " with expected resoourceId");

    }

    @Test
    public void testUpdateResponse() throws Exception {

        Resource resource = new Resource();
        UpdateResourceDTO updateResourceDTO = ResourceUtils.updateResponse(resource);
        assertNotNull(updateResourceDTO, "Updtate resource object cannot be null.");
    }

    @Test
    public void testListResourceId() throws Exception {

        Resource resource = new Resource();
        ListReadResourceDTO listReadResourceDTO = ResourceUtils.listResourceId(resource);
        assertNotNull(listReadResourceDTO, "List resource object cannot be null.");

    }
}

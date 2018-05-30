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

import org.apache.commons.lang3.StringUtils;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.oauth.uma.resource.endpoint.dto.CreateResourceDTO;
import org.wso2.carbon.identity.oauth.uma.resource.endpoint.dto.ListReadResourceDTO;
import org.wso2.carbon.identity.oauth.uma.resource.endpoint.dto.ReadResourceDTO;
import org.wso2.carbon.identity.oauth.uma.resource.endpoint.dto.ResourceDetailsDTO;
import org.wso2.carbon.identity.oauth.uma.resource.endpoint.dto.UpdateResourceDTO;
import org.wso2.carbon.identity.oauth.uma.resource.service.ResourceService;
import org.wso2.carbon.identity.oauth.uma.resource.service.model.Resource;
import org.wso2.carbon.identity.oauth.uma.resource.service.model.ScopeDataDO;

/**
 * This class holds the util methods used by ResourceRegistrationApiServiceImpl.
 */
public class ResourceUtils {

    public static ResourceService getResourceService() {

        return (ResourceService) PrivilegedCarbonContext.getThreadLocalCarbonContext()
                .getOSGiService(ResourceService.class, null);
    }

    /**
     * Returns a resourceRegistration object
     *
     * @param resourceDetailsDTO specifies the details carried out by the ResourceDetailsDTO
     * @return A generic resourceregistration with the specified details
     */
    public static Resource getResource(ResourceDetailsDTO resourceDetailsDTO) {

        Resource resourceRegistration = new Resource();
        resourceRegistration.setName(resourceDetailsDTO.getName());
        resourceRegistration.setScopes(resourceDetailsDTO.getResource_Scopes());

        for (String scope : resourceRegistration.getScopes()) {
            resourceRegistration.getScopeDataDOArray().add(new ScopeDataDO(resourceRegistration.getResourceId()
                    , scope));
        }
        if (StringUtils.isNotEmpty(resourceDetailsDTO.getType())) {
            resourceRegistration.setType(resourceDetailsDTO.getType());
        }
        if (StringUtils.isNotEmpty(resourceDetailsDTO.getDescription())) {
            resourceRegistration.setDescription(resourceDetailsDTO.getDescription());
        }
        if (StringUtils.isNotEmpty(resourceDetailsDTO.getIcon_Uri())) {
            resourceRegistration.setIconUri(resourceDetailsDTO.getIcon_Uri());
        }
        return resourceRegistration;
    }

    /**
     * Returns a ReadResourceDTO object
     *
     * @param resourceRegistration specifies the details carried out by the Resource Model class
     * @return A generic readresourceDTO with the specified details
     */
    public static ReadResourceDTO readResponse(Resource resourceRegistration) {

        ReadResourceDTO readResourceDTO = new ReadResourceDTO();
        readResourceDTO.setResourceId(resourceRegistration.getResourceId());
        readResourceDTO.setName(resourceRegistration.getName());
        readResourceDTO.setType(resourceRegistration.getType());
        readResourceDTO.setDescription(resourceRegistration.getDescription());
        readResourceDTO.setIcon_uri(resourceRegistration.getIconUri());
        readResourceDTO.setResource_scope(resourceRegistration.getScopes());
        return readResourceDTO;
    }

    /**
     * Returns a CreateResourceDTO object
     *
     * @param resourceRegistration specifies the details carried out by the Resource Model class
     * @return A generic createResourceDTO with the specified details
     */
    public static CreateResourceDTO createResponse(Resource resourceRegistration) {

        CreateResourceDTO createResourceDTO = new CreateResourceDTO();

        createResourceDTO.setResourceId(resourceRegistration.getResourceId());
        return createResourceDTO;
    }

    /**
     * Returns a UpdateResourceDTO object
     *
     * @param resourceRegistration specifies the details carried out by the Resource Model class
     * @return A generic updateResourceDTO with the specified details
     */
    public static UpdateResourceDTO updateResponse(Resource resourceRegistration) {

        UpdateResourceDTO updateResourceDTO = new UpdateResourceDTO(resourceRegistration.getResourceId());
    //    updateResourceDTO.setResourceId(resourceRegistration.getResourceId());
        return updateResourceDTO;
    }

    public static ListReadResourceDTO listResourceId(Resource resourceRegistration) {

        ListReadResourceDTO listReadResourceDTO = new ListReadResourceDTO();
        return listReadResourceDTO;
    }
}

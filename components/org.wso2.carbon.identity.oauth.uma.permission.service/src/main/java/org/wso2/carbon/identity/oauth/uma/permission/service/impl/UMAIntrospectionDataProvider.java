/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.identity.oauth.uma.permission.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.core.handler.AbstractIdentityHandler;
import org.wso2.carbon.identity.oauth.uma.common.exception.UMAException;
import org.wso2.carbon.identity.oauth.uma.permission.service.PermissionService;
import org.wso2.carbon.identity.oauth.uma.permission.service.model.Resource;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.oauth2.IntrospectionDataProvider;
import org.wso2.carbon.identity.oauth2.dto.OAuth2IntrospectionResponseDTO;
import org.wso2.carbon.identity.oauth2.dto.OAuth2TokenValidationRequestDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Introspection data provider for the tokens issued with UMA grant.
 */
public class UMAIntrospectionDataProvider extends AbstractIdentityHandler implements IntrospectionDataProvider {

    private final PermissionService permissionService;
    private static final String RESOURCE_ID = "resource_id";
    private static final String RESOURCE_SCOPE = "resource_scopes";
    private static final String PERMISSION = "permission";
    private static Log log = LogFactory.getLog(UMAIntrospectionDataProvider.class);


    public UMAIntrospectionDataProvider(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Override
    public Map<String, Object> getIntrospectionData(OAuth2TokenValidationRequestDTO oAuth2TokenValidationRequestDTO,
                                                    OAuth2IntrospectionResponseDTO oAuth2IntrospectionResponseDTO)
            throws IdentityOAuth2Exception {

        Map<String, Object> introspectionData = new HashMap<>();

        if (isEnabled()) {
            try {
                List<Resource> resources = permissionService.validateAccessToken(oAuth2TokenValidationRequestDTO
                                                                                         .getAccessToken()
                                                                                         .getIdentifier());
                if (resources != null && !resources.isEmpty()) {
                    if (log.isDebugEnabled()) {
                        log.debug(String.format("Resources found for the token issued to Client ID: %s and User: %s",
                                                oAuth2IntrospectionResponseDTO.getClientId(),
                                                oAuth2IntrospectionResponseDTO.getUsername()));
                    }
                    List<Map<String, Object>> permissions = new ArrayList<>();
                    for (Resource resource : resources) {
                        Map<String, Object> data = new HashMap<>();
                        data.put(RESOURCE_ID, resource.getResourceId());
                        data.put(RESOURCE_SCOPE, resource.getResourceScopes());
                        permissions.add(data);
                    }
                    introspectionData.put(PERMISSION, permissions);
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug(String.format("No resources found for the token issued to Client ID: %s and User: %s",
                                                oAuth2IntrospectionResponseDTO.getClientId(),
                                                oAuth2IntrospectionResponseDTO.getUsername()));
                    }
                }
            } catch (UMAException e) {
                throw new IdentityOAuth2Exception("Error occurred while retrieving resources.", e);
            }
        }
        return introspectionData;
    }
}

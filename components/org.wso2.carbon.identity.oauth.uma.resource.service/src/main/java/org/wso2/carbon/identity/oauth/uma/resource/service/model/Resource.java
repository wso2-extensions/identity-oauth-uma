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

package org.wso2.carbon.identity.oauth.uma.resource.service.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Results holder for resource management Code validation query.
 */
public class Resource {

    private String resourceId;

    private String name;

    private String type;

    private String iconUri;

    private String description;

    private List<String> resourceScopes = new ArrayList<>();

    private List<ScopeDataDO> scopeDataDoArray = new ArrayList<>();

    public String getResourceId() {

        return resourceId;
    }

    public void setResourceId(String resourceId) {

        this.resourceId = resourceId;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public List<String> getScopes() {

        return resourceScopes;
    }

    public void setScopes(List<String> scopes) {

        this.resourceScopes = scopes;
    }

    public List<ScopeDataDO> getScopeDataDOArray() {

        return scopeDataDoArray;
    }

    public void setScopeDataDOArray(List<ScopeDataDO> scopeDataDoArray) {

        this.scopeDataDoArray = scopeDataDoArray;
    }

    public String getType() {

        return type;
    }

    public void setType(String type) {

        this.type = type;
    }

    public String getIconUri() {

        return iconUri;
    }

    public void setIconUri(String iconUri) {

        this.iconUri = iconUri;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

}

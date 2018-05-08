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

/**
 * Data holder for store scope details.
 */
public class ScopeDataDO {

    private String resourceId;
    private String scopeName;
    private String scopeDescription;
    private int tenantid;

    public ScopeDataDO() {

    }

    public ScopeDataDO(String resourceId, String name) {

        this.resourceId = resourceId;
        this.scopeName = name;
    }

    public String getResourceId() {

        return resourceId;
    }

    public void setResourceId(String resourceId) {

        this.resourceId = resourceId;
    }

    public String getScopeName() {

        return scopeName;
    }

    public void setScopeName(String scopeName) {

        this.scopeName = scopeName;
    }

    public String getScopeDescription() {

        return scopeDescription;
    }

    public void setScopeDescription(String scopeDescription) {

        this.scopeDescription = scopeDescription;
    }

    public int getTenantid() {

        return tenantid;
    }

    public void setTenantid(int tenantid) {

        this.tenantid = tenantid;
    }
}

package org.wso2.carbon.identity.oauth.uma.service.model;

/**
 * Created by isuri on 1/23/18.
 */

public class ScopeDataDO {

    private int id;
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

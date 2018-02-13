package org.wso2.carbon.identity.oauth.uma.endpoint.dto;


import io.swagger.annotations.*;
import com.fasterxml.jackson.annotation.*;

import java.util.List;


@ApiModel(description = "")
public class ReadResourceDTO {

    private String resourceId;
    private String name;
    private String type;
    private String icon_uri;
    private String description;
    private List<String> resource_scope;


    /**
     * Returning resource Id after successfull registration.
     **/
    @ApiModelProperty(value = " Returning resource Id after successfull registration.")
    @JsonProperty("resourceId")
    public String getResourceId() {

        return resourceId;
    }

    public void setResourceId(String resourceId) {

        this.resourceId = resourceId;
    }


    /**
     * Returning name after successfull registration.
     **/
    @ApiModelProperty(value = " Returning name after successfull registration.")
    @JsonProperty("name")
    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }


    /**
     * Returning resource scopes after successfull registration.
     **/
    /*@ApiModelProperty(value = " Returning resource scopes after successfull registration.")
    @JsonProperty("scope")
    public String getScope() {

        return scope;
    }

    public void setScope(String scope) {

        this.scope = scope;
    }

*/

    /**
     * Returning type after successfull registration.
     **/
    @ApiModelProperty(value = " Returning type after successfull registration.")
    @JsonProperty("type")
    public String getType() {

        return type;
    }

    public void setType(String type) {

        this.type = type;
    }


    /**
     * Returning icon_uri after successfull registration.
     **/
    @ApiModelProperty(value = " Returning icon_uri after successfull registration.")
    @JsonProperty("icon_uri")
    public String getIcon_uri() {

        return icon_uri;
    }

    public void setIcon_uri(String icon_uri) {

        this.icon_uri = icon_uri;
    }


    /**
     * Returning description after successfull registration.
     **/
    @ApiModelProperty(value = " Returning description after successfull registration.")
    @JsonProperty("description")
    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public List<String> getResource_scope() {

        return resource_scope;
    }

    public void setResource_scope(List<String> resource_scope) {

        this.resource_scope = resource_scope;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class ReadResourceDTO {\n");

        sb.append("  resourceId: ").append(resourceId).append("\n");
        sb.append("  name: ").append(name).append("\n");
        sb.append("  scope: ").append(resource_scope).append("\n");
        sb.append("  type: ").append(type).append("\n");
        sb.append("  icon_uri: ").append(icon_uri).append("\n");
        sb.append("  description: ").append(description).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}

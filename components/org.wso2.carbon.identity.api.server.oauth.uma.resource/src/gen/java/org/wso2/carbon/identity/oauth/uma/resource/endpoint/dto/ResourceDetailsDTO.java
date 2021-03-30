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

package org.wso2.carbon.identity.oauth.uma.resource.endpoint.dto;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.*;
import com.fasterxml.jackson.annotation.*;

import javax.validation.constraints.NotNull;

@ApiModel(description = "")
public class ResourceDetailsDTO {


    @NotNull
    private List<String> resource_scopes = new ArrayList<String>();

    @NotNull
    private String icon_uri;

    @NotNull
    private String name;

    @NotNull
    private String type;

    @NotNull
    private String description;


    /**
     * An array of strings indicating the available scopes for this resource.\n
     **/
    @ApiModelProperty(required = true, value = "An array of strings indicating the available scopes for this resource.\n")
    @JsonProperty("resource_scopes")
    public List<String> getResource_Scopes() {
        return resource_scopes;
    }

    public void setResource_Scopes(List<String> resourceScopes) {
        this.resource_scopes = resourceScopes;
    }


    /**
     * A URI for a graphic icon representing the resource.  \n
     **/
    @ApiModelProperty(required = true, value = "A URI for a graphic icon representing the resource.  \n")
    @JsonProperty("icon_uri")
    public String getIcon_Uri() {
        return icon_uri;
    }

    public void setIcon_Uri(String iconUri) {
        this.icon_uri = iconUri;
    }


    /**
     * A human-readable string describing a resource of one or more resources.The authorization server MAY use the name in any user interface it presents to the resource owner.\n
     **/
    @ApiModelProperty(required = true, value = "A human-readable string describing a resource of one or more resources.The authorization server MAY use the name in any user interface it presents to the resource owner.\n")
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    /**
     * A string uniquely identifying the semantics of the resource.\n
     **/
    @ApiModelProperty(required = true, value = "A string uniquely identifying the semantics of the resource.\n")
    @JsonProperty("type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    /**
     * A human-readable string describing the resource at length. The authorization server MAY use this description in any user interface it presents to a resource owner, for example, for resource protection monitoring or policy setting.\n
     **/
    @ApiModelProperty(required = true, value = "A human-readable string describing the resource at length. The authorization server MAY use this description in any user interface it presents to a resource owner, for example, for resource protection monitoring or policy setting.\n")
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class ResourceDetailsDTO {\n");
        sb.append("  resource_scopes: ").append(resource_scopes).append("\n");
        sb.append("  icon_uri: ").append(icon_uri).append("\n");
        sb.append("  name: ").append(name).append("\n");
        sb.append("  type: ").append(type).append("\n");
        sb.append("  description: ").append(description).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}

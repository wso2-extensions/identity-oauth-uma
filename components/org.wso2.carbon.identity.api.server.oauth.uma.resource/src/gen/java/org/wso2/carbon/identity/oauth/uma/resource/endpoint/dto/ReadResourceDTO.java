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


import io.swagger.annotations.*;
import com.fasterxml.jackson.annotation.*;

import java.util.List;


@ApiModel(description = "")
public class ReadResourceDTO {

    private String _id;
    private String name;
    private String type;
    private String icon_uri;
    private String description;
    private List<String> resource_scopes;


    /**
     * Returning resource Id after successful registration.
     **/
    @ApiModelProperty(value = " Returning resource Id after successful registration.")
    @JsonProperty("_id")
    public String getResourceId() {

        return _id;
    }

    public void setResourceId(String resourceId) {

        this._id = resourceId;
    }


    /**
     * Returning name after successful registration.
     **/
    @ApiModelProperty(value = " Returning name after successful registration.")
    @JsonProperty("name")
    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }


    /**
     * Returning type after successful registration.
     **/
    @ApiModelProperty(value = " Returning type after successful registration.")
    @JsonProperty("type")
    public String getType() {

        return type;
    }

    public void setType(String type) {

        this.type = type;
    }

    /**
     * Returning icon_uri after successful registration.
     **/
    @ApiModelProperty(value = " Returning icon_uri after successful registration.")
    @JsonProperty("icon_uri")
    public String getIcon_uri() {

        return icon_uri;
    }

    public void setIcon_uri(String icon_uri) {

        this.icon_uri = icon_uri;
    }


    /**
     * Returning description after successful registration.
     **/
    @ApiModelProperty(value = " Returning description after successful registration.")
    @JsonProperty("description")
    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public List<String> getResource_scopes() {

        return resource_scopes;
    }

    public void setResource_scopes(List<String> resource_scopes) {

        this.resource_scopes = resource_scopes;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class ReadResourceDTO {\n");

        sb.append("  _id: ").append(_id).append("\n");
        sb.append("  name: ").append(name).append("\n");
        sb.append("  scope: ").append(resource_scopes).append("\n");
        sb.append("  type: ").append(type).append("\n");
        sb.append("  icon_uri: ").append(icon_uri).append("\n");
        sb.append("  description: ").append(description).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}

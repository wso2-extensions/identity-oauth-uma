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

@ApiModel(description = "")
public class ListReadResourceDTO {

    private List<String> resourceId = new ArrayList<String>();

    /**
     * Returning list of resource Id after successfull registration.
     **/
    @ApiModelProperty(value = " Returning list of resource Id after successfull registration.")
    @JsonProperty("resourceId")
    public List<String> getResourceId() {

        return resourceId;
    }

    public void setResourceId(List<String> resourceId) {
        this.resourceId = resourceId;
    }


    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class ListReadResourceDTO {\n");
        sb.append("  resourceId: ").append(resourceId).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}

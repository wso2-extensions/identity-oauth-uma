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


@ApiModel(description = "")
public class CreateResourceDTO {

    private String resourceId;
    private String policy_uri;

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
     * Policy_URI used for user access
     **/
    @ApiModelProperty(value = " Policy_URI used for user access")
    @JsonProperty("policy_uri")
    public String getPolicy_uri() {

        return policy_uri ;
    }

    public void setPolicy_uri(String policy_uri) {

        this.policy_uri = policy_uri;
    }


    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class CreateResourceDTO {\n");

        sb.append("  resourceId: ").append(resourceId).append("\n");
        sb.append("  policy_uri: ").append(policy_uri).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}

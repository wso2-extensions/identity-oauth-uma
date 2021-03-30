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

    private String _id;

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




    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class CreateResourceDTO {\n");

        sb.append("  _id: ").append(_id).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}

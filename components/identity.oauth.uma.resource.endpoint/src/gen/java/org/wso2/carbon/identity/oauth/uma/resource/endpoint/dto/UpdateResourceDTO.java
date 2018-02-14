package org.wso2.carbon.identity.oauth.uma.resource.endpoint.dto;


import io.swagger.annotations.*;
import com.fasterxml.jackson.annotation.*;

import javax.validation.constraints.NotNull;


@ApiModel(description = "")
public class UpdateResourceDTO {

    private String resourceId;


    /**
     * Returning resource description after successfull updating.
     **/
    @ApiModelProperty(value = " Returning resource description after successfull updating.")
    @JsonProperty("resourceId")
    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class UpdateResourceDTO {\n");

        sb.append("  resourceId: ").append(resourceId).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}

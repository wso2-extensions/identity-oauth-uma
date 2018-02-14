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

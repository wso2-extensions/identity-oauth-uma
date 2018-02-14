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

        return policy_uri;
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

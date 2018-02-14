package org.wso2.carbon.identity.oauth.uma.resource.endpoint.dto;


import io.swagger.annotations.*;
import com.fasterxml.jackson.annotation.*;

@ApiModel(description = "")
public class ErrorDTO {

    private String code;
    private String description;

    /**
     **/
    @ApiModelProperty(value = "")
    @JsonProperty("code")
    public String getCode() {
        return code;
    }

    public String setCode(String code) {
        this.code = code;
        return code;
    }

    /**
     **/

    @ApiModelProperty(value = "")
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    public String setDescription(String description) {
        this.description = description;
        return description;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ErrorDTO {\n");
        sb.append("  code: ").append(code).append("\n");
        sb.append("  description: ").append(description).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}
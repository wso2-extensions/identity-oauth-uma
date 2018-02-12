package org.wso2.carbon.identity.oauth.uma.permission.endpoint.dto;


import io.swagger.annotations.*;
import com.fasterxml.jackson.annotation.*;

import javax.validation.constraints.NotNull;





@ApiModel(description = "")
public class ErrorResponseDTO  {
  
  
  @NotNull
  private String error = null;
  
  
  private String errorDescription = null;

  
  /**
   * A single error code.
   **/
  @ApiModelProperty(required = true, value = "A single error code.")
  @JsonProperty("error")
  public String getError() {
    return error;
  }
  public void setError(String error) {
    this.error = error;
  }

  
  /**
   * Additional information about the error.
   **/
  @ApiModelProperty(value = "Additional information about the error.")
  @JsonProperty("errorDescription")
  public String getErrorDescription() {
    return errorDescription;
  }
  public void setErrorDescription(String errorDescription) {
    this.errorDescription = errorDescription;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class ErrorResponseDTO {\n");
    
    sb.append("  error: ").append(error).append("\n");
    sb.append("  errorDescription: ").append(errorDescription).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}

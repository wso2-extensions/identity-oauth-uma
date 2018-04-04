package org.wso2.carbon.identity.oauth.uma.permission.endpoint.dto;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.*;
import com.fasterxml.jackson.annotation.*;

import javax.validation.constraints.NotNull;





@ApiModel(description = "")
public class ResourceModelInnerDTO  {
  
  
  @NotNull
  private List<String> resource_scopes = new ArrayList<String>();
  
  @NotNull
  private String resource_id = null;

  
  /**
   * An array referencing zero or more identifiers of scopes to which the resource server is requesting access for this resource on behalf of the client. Each scope identifier MUST correspond to a scope that was previously registered by this resource server for the referenced resource.\n
   **/
  @ApiModelProperty(required = true, value = "An array referencing zero or more identifiers of scopes to which the resource server is requesting access for this resource on behalf of the client. Each scope identifier MUST correspond to a scope that was previously registered by this resource server for the referenced resource.\n")
  @JsonProperty("resource_scopes")
  public List<String> getResourceScopes() {
    return resource_scopes;
  }
  public void setResourceScopes(List<String> resource_scopes) {
    this.resource_scopes = resource_scopes;
  }

  
  /**
   * The identifier for a resource to which the resource server is requesting permission on behalf of the client. The identifier MUST correspond to a resource that was previously registered.\n
   **/
  @ApiModelProperty(required = true, value = "The identifier for a resource to which the resource server is requesting permission on behalf of the client. The identifier MUST correspond to a resource that was previously registered.\n")
  @JsonProperty("resource_id")
  public String getResourceId() {
    return resource_id;
  }
  public void setResourceId(String resource_id) {
    this.resource_id = resource_id;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class ResourceModelInnerDTO {\n");
    
    sb.append("  resource_scopes: ").append(resource_scopes).append("\n");
    sb.append("  resource_id: ").append(resource_id).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}

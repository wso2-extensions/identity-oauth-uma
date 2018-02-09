package org.wso2.carbon.identity.oauth.uma.permission.endpoint.dto;

import java.util.ArrayList;

import io.swagger.annotations.*;


@ApiModel(description = "")
public class ResourceModelDTO extends ArrayList<ResourceModelInnerDTO> {
  

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class ResourceModelDTO {\n");
    sb.append("  " + super.toString()).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}

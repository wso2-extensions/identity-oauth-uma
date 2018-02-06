package org.wso2.carbon.identity.oauth.uma.endpoint.dto;


import io.swagger.annotations.*;
import com.fasterxml.jackson.annotation.*;

import javax.validation.constraints.NotNull;





@ApiModel(description = "")
public class PermissionTicketResponseDTO  {
  
  
  @NotNull
  private String ticket = null;

  
  /**
   * The created permission ticket.
   **/
  @ApiModelProperty(required = true, value = "The created permission ticket.")
  @JsonProperty("ticket")
  public String getTicket() {
    return ticket;
  }
  public void setTicket(String ticket) {
    this.ticket = ticket;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class PermissionTicketResponseDTO {\n");
    
    sb.append("  ticket: ").append(ticket).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}

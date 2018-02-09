package org.wso2.carbon.identity.oauth.uma.permission.endpoint.factories;

import org.wso2.carbon.identity.oauth.uma.permission.endpoint.PermissionApiService;
import org.wso2.carbon.identity.oauth.uma.permission.endpoint.PermissionApiServiceImpl;

public class PermissionApiServiceFactory {

   private final static PermissionApiService service = new PermissionApiServiceImpl();

   public static PermissionApiService getPermissionApi()
   {
      return service;
   }
}

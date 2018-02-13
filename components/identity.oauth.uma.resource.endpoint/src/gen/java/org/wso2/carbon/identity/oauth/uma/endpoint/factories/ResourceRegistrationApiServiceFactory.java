package org.wso2.carbon.identity.oauth.uma.endpoint.factories;

import org.wso2.carbon.identity.oauth.uma.endpoint.ResourceRegistrationApiService;
import org.wso2.carbon.identity.oauth.uma.endpoint.impl.impl.ResourceRegistrationApiServiceImpl;

public class ResourceRegistrationApiServiceFactory {

   private final static ResourceRegistrationApiService service = new ResourceRegistrationApiServiceImpl();

   public static ResourceRegistrationApiService getResourceRegistrationApi()
   {
      return service;
   }
}

package org.wso2.carbon.identity.oauth.uma.resource.endpoint.factories;

import org.wso2.carbon.identity.oauth.uma.resource.endpoint.ResourceRegistrationApiService;
import org.wso2.carbon.identity.oauth.uma.resource.endpoint.impl.ResourceRegistrationApiServiceImpl;

public class ResourceRegistrationApiServiceFactory {

   private final static ResourceRegistrationApiService service = new ResourceRegistrationApiServiceImpl();

   public static ResourceRegistrationApiService getResourceRegistrationApi()
   {
      return service;
   }
}

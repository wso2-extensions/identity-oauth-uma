# User Managed Access with WSO2 Identity Server

WSO2 Identity Server (WSO2 IS) supports the UMA 2.0 protocol, which allows a resource owner to easily share resources with other requesting parties. To use UMA with WSO2 Identity Server, first you need to configure the authenticator with WSO2 Identity Server.

## Deploying UMA Artifacts

You can build the authenticator from the source code by following the steps given below.

   1. Stop WSO2 Identity Server if it is already running.
   2. To build the artifacts, navigate to the repository directory and execute the following command:
      ```
      mvn clean install
      ```
   3. Add the `.jar` files, which are created in the following directories to the `<IS_HOME>/repository/components/dropins` directory.
      ```
      components/org.wso2.carbon.identity.oauth.uma.common/target
      components/org.wso2.carbon.identity.oauth.uma.grant/target
      components/org.wso2.carbon.identity.oauth.uma.permission.service/target
      components/org.wso2.carbon.identity.oauth.uma.resource.service/target
      components/org.wso2.carbon.identity.oauth.uma.xacml.extension/target
      ```
   4. Add the `.war` files, which are created in the following directories to the `<IS_HOME>/repository/deployment/server/webapps` directory.
      ```
      components/org.wso2.carbon.identity.oauth.uma.permission.endpoint/target
      components/org.wso2.carbon.identity.oauth.uma.resource.endpoint/target 
      ```
   5. Run the corresponding db script from the [folder](../features/org.wso2.carbon.identity.oauth.uma.server.feature/resources/dbscripts).
   6. Start/ Restart WSO2 Identity Server.

## Enabling UMA in WSO2 Identity Server

1. Stop WSO2 Identity Server if it is already running.
2. Add the below configuration to the `<IS-Home>/repository/conf/deployment.toml` file.
    ```
    [[oauth.custom_grant_type]]
    name = "urn:ietf:params:oauth:grant-type:uma-ticket"
    grant_handler = "org.wso2.carbon.identity.oauth.uma.grant.UMA2GrantHandler"
    grant_validator = "org.wso2.carbon.identity.oauth.uma.grant.GrantValidator"

    [[event_listener]]
    id = "uma_introspection_data_provider"
    type = "org.wso2.carbon.identity.core.handler.AbstractIdentityHandler"
    name = "org.wso2.carbon.identity.oauth.uma.permission.service.impl.UMAIntrospectionDataProvider"
    order = "161"
    enable = false

    [[resource.access_control]]
    context = "(.*)/api/identity/oauth2/uma/resourceregistration/v1.0/(.*)"
    secure = "true"
    http_method = "all"

    [[resource.access_control]]
    context = "(.*)/api/identity/oauth2/uma/permission/v1.0/(.*)"
    secure = "true"
    http_method = "all"
    ```
3. Start/ Restart WSO2 Identity Server.



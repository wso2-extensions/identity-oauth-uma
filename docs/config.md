# User Managed Access with WSO2 Identity Server

WSO2 Identity Server (WSO2 IS) supports the UMA 2.0 protocol, which allows a resource owner to easily share resources with other requesting parties. To use UMA with WSO2 Identity Server, first you need to configure the authenticator with WSO2 Identity Server.

## Deploying UMA Artifacts

You can either download the UMA artifacts or build the authenticator from the source code by following the steps given below.

### Download UMA artifacts
1. Stop WSO2 Identity Server if it is already running.
2. Download the UMA connector and other required artifacts from the [WSO2 store](https://store.wso2.com/store/assets/isconnector/list).
3. Add the following `.jar` files to the `<IS_HOME>/repository/components/dropins` directory.
    ```
    org.wso2.carbon.identity.oauth.uma.common-x.x.x.jar
    org.wso2.carbon.identity.oauth.uma.grant-x.x.x.jar
    org.wso2.carbon.identity.oauth.uma.permission.service-x.x.x.jar
    org.wso2.carbon.identity.oauth.uma.resource.service-x.x.x.jar
    org.wso2.carbon.identity.oauth.uma.xacml.extension-x.x.x.jar
    ```
4. Add the following `.war` files to the `<IS_HOME>/repository/deployment/server/webapps` directory.
    ```
    api#identity#oauth2#uma#resourceregistration#v_.war
    api#identity#oauth2#uma#permission#v_.war
    ```
5. Run the corresponding db script from the [folder](../features/org.wso2.carbon.identity.oauth.uma.server.feature/resources/dbscripts).
6. Start/ Restart WSO2 Identity Server.

### Build from the source code
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
    ```toml
    [[oauth.custom_grant_type]]
    name = "urn:ietf:params:oauth:grant-type:uma-ticket"
    grant_handler = "org.wso2.carbon.identity.oauth.uma.grant.UMA2GrantHandler"
    grant_validator = "org.wso2.carbon.identity.oauth.uma.grant.GrantValidator"

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



# User Managed Access with WSO2 Identity Server

WSO2 Identity Server (WSO2 IS) supports the UMA 2.0 protocol, which allows a resource owner to easily share resources with other requesting parties. To use UMA with WSO2 Identity Server, first you need to configure the authenticator with WSO2 Identity Server.

### Deploying UMA Artifacts

You can either download the UMA artifacts or build the authenticator from the source code.

1. To download the Github artifacts:
   1. Stop WSO2 Identity Server if it is already running.
   2. Download the UMA connector and other required artifacts from the [WSO2 store](https://store.wso2.com/store/assets/isconnector/list).   
   3. Place the following `.jar` files into the `<IS_HOME>/repository/components/dropins` directory.
       ```
       org.wso2.carbon.identity.oauth.uma.common-x.x.x.jar
       org.wso2.carbon.identity.oauth.uma.grant-x.x.x.jar
       org.wso2.carbon.identity.oauth.uma.permission.service-x.x.x.jar
       org.wso2.carbon.identity.oauth.uma.resource.service-x.x.x.jar
       org.wso2.carbon.identity.oauth.uma.xacml.extension-x.x.x.jar
       ```
   4. Place the following `.war` files into the `<IS_HOME>/repository/deployment/server/webapps` directory.
       ```
       api#identity#oauth2#uma#resourceregistration#v_.war
       api#identity#oauth2#uma#permission#v_.war
       ```
   5. Run the corresponding db script from the [folder](../features/org.wso2.carbon.identity.oauth.uma.server.feature/resources/dbscripts)
   6. Start/ Restart the WSO2 Identity Server.

2. To build from the source code:
   1. Stop WSO2 Identity Server if it is already running.
   2. To build the artifacts, navigate to the repository directory and execute the following command in a command prompt:
      ```
      mvn clean install
      ```
   3. Place the `.jar` files are created at following directories into the `<IS_HOME>/repository/components/dropins` directory.
      ```
      components/org.wso2.carbon.identity.oauth.uma.common/target
      components/org.wso2.carbon.identity.oauth.uma.grant/target
      components/org.wso2.carbon.identity.oauth.uma.permission.service/target
      components/org.wso2.carbon.identity.oauth.uma.resource.service/target
      components/org.wso2.carbon.identity.oauth.uma.xacml.extension/target
      ```
   4. Place the `.war` files are created at following directories into the `<IS_HOME>/repository/deployment/server/webapps` directory.
      ```
      components/org.wso2.carbon.identity.oauth.uma.permission.endpoint/target
      components/org.wso2.carbon.identity.oauth.uma.resource.endpoint/target 
      ```
   5. Run the corresponding db script from the [folder](../features/org.wso2.carbon.identity.oauth.uma.server.feature/resources/dbscripts)
   6. Start/ Restart the WSO2 Identity Server.

### Enabling UMA in the WSO2 Identity Server

1. Stop WSO2 Identity Server if it is already running.
2. Add the below configuration in the `<IS-Home>/repository/conf/deployment.toml` file.
    ```
   [oauth.grant_type.uma_ticket]
    enable=true
   ```
3. Start/ Restart the WSO2 Identity Server.



package org.wso2.carbon.identity.uma.grant.connector;

import org.wso2.carbon.identity.oauth.uma.permission.service.model.Resource;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;

import java.util.List;

/**
 *
 * interface to implement for extensions.
 */
public interface PolicyEvaluator {

    boolean isAuthorized(String userName, List<Resource> resource) throws IdentityOAuth2Exception;
}

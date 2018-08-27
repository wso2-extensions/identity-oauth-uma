/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.oauth.uma.grant.connector;

import org.wso2.carbon.identity.oauth.uma.permission.service.model.Resource;
import org.wso2.carbon.identity.oauth.uma.xacml.service.handler.XACMLUMAHandler;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;

import java.util.List;

/**
 * Default implementation for the policy evaluator.
 */
public class DefaultPolicyEvaluator implements PolicyEvaluator {

    @Override
    public boolean isAuthorized(String userName, List<Resource> resources) throws IdentityOAuth2Exception {

        XACMLUMAHandler xacmlumaHandler = new XACMLUMAHandler();
        return xacmlumaHandler.isAuthorized(userName, resources);
    }
}

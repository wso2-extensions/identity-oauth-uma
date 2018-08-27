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

package org.wso2.carbon.identity.oauth.uma.grant.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.wso2.carbon.identity.oauth.uma.grant.connector.DefaultPolicyEvaluator;
import org.wso2.carbon.identity.oauth.uma.grant.connector.PolicyEvaluator;

import java.util.ArrayList;
import java.util.List;

/**
 * Service component for UMA grant.
 **/
@Component(name = "org.wso2.carbon.identity.oauth.uma.grant",
        immediate = true)
public class UMA2GrantServiceComponent {

    private static final Log log = LogFactory.getLog(UMA2GrantServiceComponent.class);
    private static List<PolicyEvaluator> policyEvaluators = new ArrayList<>();

    @Activate
    protected void activate(BundleContext bundleContext) {

        try {
            bundleContext.registerService(PolicyEvaluator.class, new DefaultPolicyEvaluator(), null);
        } catch (Throwable throwable) {
            log.error("Error occurred while activating the UMA Grant Component.", throwable);
        }

        log.info("UMA Grant component activated successfully.");
    }

    @Reference(
            name = "policy.evaluator",
            service = PolicyEvaluator.class,
            cardinality = ReferenceCardinality.MULTIPLE,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetPolicyEvaluator"
    )
    protected void setPolicyEvaluator(PolicyEvaluator policyEvaluator) {

        policyEvaluators.add(policyEvaluator);
        log.info("Policy evaluator registered successfully: " + policyEvaluator.getClass().getSimpleName());
    }

    protected void unsetPolicyEvaluator(PolicyEvaluator policyEvaluator) {

        policyEvaluators.remove(policyEvaluator);
        log.info("Policy evaluator un-registered successfully: " + policyEvaluator.getClass().getSimpleName());
    }

    public static List<PolicyEvaluator> getPolicyEvaluators() {
        return policyEvaluators;
    }
}

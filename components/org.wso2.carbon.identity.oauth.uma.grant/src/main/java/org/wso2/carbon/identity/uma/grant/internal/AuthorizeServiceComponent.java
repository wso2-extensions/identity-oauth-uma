package org.wso2.carbon.identity.uma.grant.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.wso2.carbon.identity.uma.grant.connector.PolicyEvaluator;

/**
 * Service component for UMA grant
 **/
@Component(name = "org.wso2.carbon.identity.oauth.uma.authorization",
        immediate = true)
public class AuthorizeServiceComponent {

    private static final Log log = LogFactory.getLog(AuthorizeServiceComponent.class);

    public static PolicyEvaluator policyEvaluator;

    public static PolicyEvaluator getPolicyEvaluator () {
        return policyEvaluator;
    }

    @Reference(
            name = "policy.evaluator",
            service = org.wso2.carbon.identity.uma.grant.connector.PolicyEvaluator.class,
            cardinality = ReferenceCardinality.MULTIPLE,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetPolicyEvaluator"
    )
    protected void setPolicyEvaluator(PolicyEvaluator policyEvaluator) {

        if (policyEvaluator != null) {
            if (log.isDebugEnabled()) {
                log.debug("Set the policy evaluator successfully.");
            }
        }
        this.policyEvaluator = policyEvaluator;
    }

    protected void unsetPolicyEvaluator(PolicyEvaluator policyEvaluator) {

        if (log.isDebugEnabled()) {
            log.debug("Unset the policy evaluator successfully.");
        }
    }
}

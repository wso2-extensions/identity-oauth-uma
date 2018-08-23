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

package org.wso2.carbon.identity.oauth.uma.grant;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.core.util.KeyStoreManager;
import org.wso2.carbon.identity.application.authentication.framework.model.AuthenticatedUser;
import org.wso2.carbon.identity.base.IdentityException;
import org.wso2.carbon.identity.core.util.IdentityTenantUtil;
import org.wso2.carbon.identity.oauth.uma.common.exception.UMAClientException;
import org.wso2.carbon.identity.oauth.uma.common.exception.UMAServerException;
import org.wso2.carbon.identity.oauth.uma.grant.connector.PolicyEvaluator;
import org.wso2.carbon.identity.oauth.uma.grant.internal.UMA2GrantServiceComponent;
import org.wso2.carbon.identity.oauth.uma.permission.service.dao.PermissionTicketDAO;
import org.wso2.carbon.identity.oauth.uma.permission.service.model.Resource;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.oauth2.ResponseHeader;
import org.wso2.carbon.identity.oauth2.model.RequestParameter;
import org.wso2.carbon.identity.oauth2.token.OAuthTokenReqMessageContext;
import org.wso2.carbon.identity.oauth2.token.handlers.grant.AbstractAuthorizationGrantHandler;
import org.wso2.carbon.identity.oauth2.util.OAuth2Util;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

import java.security.Key;
import java.security.interfaces.RSAPrivateKey;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Grant type for User Managed Access 2.0.
 */
public class UMA2GrantHandler extends AbstractAuthorizationGrantHandler {

    private static Log log = LogFactory.getLog(UMA2GrantHandler.class);
    private Map<Integer, Key> privateKeys = new ConcurrentHashMap<>();

    @Override
    public boolean validateGrant(OAuthTokenReqMessageContext tokReqMsgCtx) throws IdentityOAuth2Exception {

        String grantType = null;
        String permissionTicket = null;
        String idToken = null;

        // Extract request parameters.
        RequestParameter[] parameters = tokReqMsgCtx.getOauth2AccessTokenReqDTO().getRequestParameters();

        for (RequestParameter parameter : parameters) {

            // Extract grant type.
            if (UMAGrantConstants.GRANT_PARAM.equals(parameter.getKey())) {
                if (parameter.getValue() != null) {
                    grantType = parameter.getValue()[0];
                }
            }

            // Extract permission ticket.
            if (UMAGrantConstants.PERMISSION_TICKET.equals(parameter.getKey())) {
                if (parameter.getValue() != null) {
                    permissionTicket = parameter.getValue()[0];
                }
            }

            // Extract ID token.
            if (UMAGrantConstants.CLAIM_TOKEN.equals(parameter.getKey())) {
                if (parameter.getValue() != null) {
                    idToken = parameter.getValue()[0];
                }
            }
        }

        if (StringUtils.isEmpty(grantType) || !StringUtils.equals(UMAGrantConstants.UMA_GRANT_TYPE, grantType)) {
            return false;
        }

        if (StringUtils.isEmpty(permissionTicket)) {
            throw new IdentityOAuth2Exception("Empty permission ticket.");
        }

        if (StringUtils.isEmpty(idToken)) {
            throw new IdentityOAuth2Exception("Empty id-token.");
        }

        String subject = getSubjectFromIDToken(idToken, tokReqMsgCtx.getOauth2AccessTokenReqDTO().getTenantDomain());

        // Validate the permission ticket against the subject.
        if (validatePermissionTicket(permissionTicket, subject)) {
            AuthenticatedUser authenticatedUser = new AuthenticatedUser();
            authenticatedUser.setUserName(subject);
            tokReqMsgCtx.setAuthorizedUser(authenticatedUser);
            tokReqMsgCtx.setScope(tokReqMsgCtx.getOauth2AccessTokenReqDTO().getScope());
            return true;
        } else {
            ResponseHeader responseHeader = new ResponseHeader();
            responseHeader.setKey(UMAGrantConstants.ERROR_RESPONSE_HEADER);
            responseHeader.setValue("Failed validation for the permission ticket for the given user.");
            tokReqMsgCtx.addProperty(UMAGrantConstants.RESPONSE_HEADERS, new ResponseHeader[]{responseHeader});
            return false;
        }
    }

    /**
     * Validate the permission ticket against the subject.
     * @param permissionTicket Permission ticket.
     * @param subject Subject identifier.
     * @return True if validation is success.
     * @throws IdentityOAuth2Exception
     */
    private boolean validatePermissionTicket(String permissionTicket, String subject) throws IdentityOAuth2Exception {

        PermissionTicketDAO permissionTicketDAO = new PermissionTicketDAO();
        List<Resource> resources;

        try {
            if (PermissionTicketDAO.isPermissionTicketExpired(permissionTicket)) {
                return false;
            }
            resources = permissionTicketDAO.validatePermissionTicket(permissionTicket);
            for (PolicyEvaluator policyEvaluator : UMA2GrantServiceComponent.getPolicyEvaluators()) {
                if (!policyEvaluator.isAuthorized(subject, resources)) {
                    return false;
                }
            }
            return true;
        } catch (UMAClientException e) {
            if (log.isDebugEnabled()) {
                log.debug("Invalid permission ticket.", e);
            }
            return false;
        } catch (UMAServerException e) {
            log.error("Server error occurred while validating permission ticket.", e);
            return false;
        }
    }

    private String getSubjectFromIDToken(String idToken, String tenantDomain) throws IdentityOAuth2Exception {

        JWTClaimsSet claimsSet = null;

        if (StringUtils.isEmpty(tenantDomain)) {
            tenantDomain = MultitenantConstants.SUPER_TENANT_DOMAIN_NAME;
        }

        // Check whether the assertion is encrypted.
        EncryptedJWT encryptedJWT = getEncryptedJWT(idToken);
        if (encryptedJWT != null) {
            RSAPrivateKey rsaPrivateKey = getPrivateKey(tenantDomain);
            RSADecrypter decrypter = new RSADecrypter(rsaPrivateKey);
            try {
                encryptedJWT.decrypt(decrypter);
            } catch (JOSEException e) {
                throw new IdentityOAuth2Exception("Error while decrypting the encrypted JWT.", e);
            }
            try {
                // If the assertion is a nested JWT.
                String payload;
                if (encryptedJWT.getPayload() != null) {
                    payload = encryptedJWT.getPayload().toString();
                } else {
                    throw new IdentityOAuth2Exception("Empty payload in the encrypted JWT.");
                }

                // Check whether the encrypted JWT is signed.
                if (isEncryptedJWTSigned(payload)) {
                    SignedJWT signedJWT = SignedJWT.parse(payload);
                    claimsSet = signedJWT.getJWTClaimsSet();
                    if (log.isDebugEnabled()) {
                        log.debug("The encrypted JWT is signed. Obtained the claim set of the encrypted JWT.");
                    }
                } else {
                    try {
                        // If encrypted JWT is not signed.
                        claimsSet = encryptedJWT.getJWTClaimsSet();
                        if (log.isDebugEnabled()) {
                            log.debug("The encrypted JWT is not signed. Obtained the claim set of the encrypted JWT.");
                        }
                    } catch (ParseException ex) {
                        throw new IdentityOAuth2Exception("Error when trying to retrieve claimsSet from the " +
                                "encrypted JWT.", ex);
                    }
                }
            } catch (ParseException e) {
                throw new IdentityOAuth2Exception("Unexpected number of Base64URL parts of the nested JWT payload. " +
                        "Expected number of parts must be three.", e);
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("The assertion is not encrypted.");
            }

            // The assertion is not an encrypted one.
            SignedJWT signedJWT = getSignedJWT(idToken);
            try {
                claimsSet = signedJWT.getJWTClaimsSet();
            } catch (ParseException e) {
                throw new IdentityOAuth2Exception("Error while retrieving claims set from the signed JWT.", e);
            }
        }

        return claimsSet.getSubject();
    }

    private EncryptedJWT getEncryptedJWT(String idToken) {

        try {
            return EncryptedJWT.parse(idToken);
        } catch (ParseException e) {
            if (log.isDebugEnabled()) {
                log.debug("Error while parsing the assertion. The assertion is not encrypted.");
            }
            return null;
        }
    }

    private SignedJWT getSignedJWT(String idToken) throws IdentityOAuth2Exception {

        try {
            return SignedJWT.parse(idToken);
        } catch (ParseException e) {
            String errorMessage = "Error while parsing the JWT.";
            throw new IdentityOAuth2Exception(errorMessage, e);
        }
    }

    private RSAPrivateKey getPrivateKey(String tenantDomain) throws IdentityOAuth2Exception {

        Key privateKey;
        int tenantId = OAuth2Util.getTenantId(tenantDomain);

        if (!(privateKeys.containsKey(tenantId))) {

            try {
                IdentityTenantUtil.initializeRegistry(tenantId, tenantDomain);
            } catch (IdentityException e) {
                throw new IdentityOAuth2Exception("Error occurred while loading registry for tenant " +
                        tenantDomain, e);
            }

            // Get tenant's key store manager.
            KeyStoreManager tenantKSM = KeyStoreManager.getInstance(tenantId);

            if (!MultitenantConstants.SUPER_TENANT_DOMAIN_NAME.equals(tenantDomain)) {

                // Derive key store name.
                String ksName = tenantDomain.trim().replace(".", "-");
                String jksName = ksName + ".jks";

                // Obtain private key.
                privateKey = tenantKSM.getPrivateKey(jksName, tenantDomain);
            } else {
                try {
                    privateKey = tenantKSM.getDefaultPrivateKey();
                } catch (Exception e) {

                    // Intentionally catch Exception as an Exception is thrown from the above layer.
                    throw new IdentityOAuth2Exception("Error while obtaining private key for super tenant", e);
                }
            }

            // PrivateKey will not be null always
            privateKeys.put(tenantId, privateKey);
        } else {

            // PrivateKey will not be null because containsKey() true says given key is exist and ConcurrentHashMap
            // does not allow to store null values.
            privateKey = privateKeys.get(tenantId);
        }

        return (RSAPrivateKey) privateKey;
    }

    private boolean isEncryptedJWTSigned(String payload) {

        if (StringUtils.isNotEmpty(payload)) {
            String[] parts = payload.split(".");
            return parts.length == 3 && StringUtils.isNotEmpty(parts[2]);
        }
        return false;
    }
}


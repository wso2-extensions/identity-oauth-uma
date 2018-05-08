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

package org.wso2.carbon.identity.oauth.uma.common;

import org.wso2.carbon.identity.core.util.IdentityUtil;

/**
 * This class holds the constants used by UMA Resource Registration Endpoint and Permission Endpoint.
 */
public class UMAConstants {

    public static final String UMA_PERMISSION_ENDPOINT_CONFIG_PATH = "uma.properties";
    public static final String REGISTERED_RESOURCE_PATH = "/api/identity/oauth2/uma/resourceregistration/v1.0/resource";
    public static final String RESOURCE_PATH =
            IdentityUtil.getServerURL(REGISTERED_RESOURCE_PATH, true, true);

    /**
     * error descriptions
     */
    public enum ErrorMessages {

        ERROR_CODE_FAIL_TO_GET_RESOURCE("60001", "Server error occurred while retrieving resource."),
        ERROR_CODE_NOT_FOUND_RESOURCE_ID("60002", "Resource id is not found."),
        ERROR_CODE_INVALID_RESOURCE_ID("60003", "Invalid resource id."),
        ERROR_CODE_RESOURCE_NAME_DUPLICATE("60004", "Conflict occurred when persisting resource name."),
        INTERNAL_SERVER_ERROR_FAILED_TO_PERSIST_REQUESTED_RESOURCES("60005", "Error occurred while persisting" +
                " requested permissions."),
        ERROR_BAD_REQUEST_INVALID_RESOURCE_ID("60005", "Permission request failed with bad resource ID."),
        ERROR_BAD_REQUEST_INVALID_RESOURCE_SCOPE("60006", "Permission request failed with bad resource scope."),
        ERROR_INTERNAL_SERVER_ERROR_FAILED_TO_PERSIST_PT("60007", "Server error occurred while persisting PT."),
        ERROR_INTERNAL_SERVER_ERROR_FAILED_TO_PERSIST_REQUESTED_PERMISSIONS("60008", "Server error occurred while " +
                "persisting requested permissions."),
        ERROR_UNEXPECTED("60009", "Unexpected error.");

        private final String code;
        private final String message;

        ErrorMessages(String code, String message) {

            this.code = code;
            this.message = message;
        }

        public String getCode() {

            return this.code;
        }

        public String getMessage() {

            return this.message;
        }

        @Override
        public String toString() {

            return code + " - " + message;
        }
    }

    /**
     * SQL Placeholders
     */
    public static final class SQLPlaceholders {

        public static final String PERMISSION_TICKET = "permission_ticket";
        public static final String TIME_CREATED = "time_created";
        public static final String VALIDITY_PERIOD = "validity_period";
        public static final String STATE = "state";
        public static final String TENANT_ID = "tenant_id";
        public static final String RESOURCE_ID = "resource_id";
        public static final String RESOURCE_SCOPE = "resource_scope";
        public static final String ID = "id";
        public static final String RESOURCE_OWNER_NAME = "resource_owner_name";
    }
}

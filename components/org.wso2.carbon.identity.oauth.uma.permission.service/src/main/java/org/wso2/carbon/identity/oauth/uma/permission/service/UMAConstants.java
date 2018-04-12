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

package org.wso2.carbon.identity.oauth.uma.permission.service;

/**
 * This class holds the constants used by Permission Endpoint.
 */
public class UMAConstants {

    public static final String UMA_PERMISSION_ENDPOINT_CONFIG_PATH = "uma.properties";

    /**
     * error descriptions
     */
    public enum ErrorMessages {
        ERROR_BAD_REQUEST_INVALID_RESOURCE_ID("6001", "Permission request failed with bad resource ID."),
        ERROR_BAD_REQUEST_INVALID_RESOURCE_SCOPE("6002", "Permission request failed with bad resource scope."),
        ERROR_INTERNAL_SERVER_ERROR_FAILED_TO_PERSIST_PT("6003", "Server error occurred while persisting PT."),
        ERROR_INTERNAL_SERVER_ERROR_FAILED_TO_PERSIST_REQUESTED_PERMISSIONS("6004", "Server error occurred while " +
                "persisting requested permissions."),
        ERROR_UNEXPECTED("6005", "Unexpected error.");

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

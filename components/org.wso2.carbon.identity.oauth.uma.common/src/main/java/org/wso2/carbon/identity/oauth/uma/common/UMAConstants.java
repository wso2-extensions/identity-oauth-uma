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

/**
 * This class holds the constants used by UMA Resource Registration Endpoint and Permission Endpoint.
 */
public class UMAConstants {

    public static final String REGISTERED_RESOURCE_PATH = "/api/identity/oauth2/uma/resourceregistration/v1.0/resource";

    /**
     * error descriptions
     */
    public enum ErrorMessages {

        ERROR_NOT_FOUND_RESOURCE_ID("60001", "Resource id is not found."),
        ERROR_BAD_REQUEST_RESOURCE_ID_MISSING("60002", "Resource id is missing in the request."),
        ERROR_CONFLICT_RESOURCE_NAME_DUPLICATE("60003", "Conflict occurred when persisting resource name."),
        ERROR_BAD_REQUEST_INVALID_RESOURCE_ID("60004", "Permission request failed with bad resource ID."),
        ERROR_BAD_REQUEST_INVALID_RESOURCE_SCOPE("60005", "Permission request failed with bad resource scope."),
        ERROR_INTERNAL_SERVER_ERROR_FAILED_TO_PERSIST_PT("60006", "Server error occurred while persisting PT."),
        ERROR_INTERNAL_SERVER_ERROR_FAILED_TO_CHECK_RESOURCE_ID_EXISTENCE("60007", "Server error occurred while " +
                "checking whether resource ids are existing."),
        ERROR_INTERNAL_SERVER_ERROR_FAILED_TO_GET_RESOURCE("60008", "Server error occurred while retrieving" +
                " resource description."),
        ERROR_INTERNAL_SERVER_ERROR_FAILED_TO_PERSIST_RESOURCE("60009", "Server error occurred while registering " +
                "resource."),
        ERROR_INTERNAL_SERVER_ERROR_FAILED_TO_DELETE_RESOURCE("60010", "Server error occurred while deleting " +
                "resource."),
        ERROR_INTERNAL_SERVER_ERROR_FAILED_TO_UPDATE_RESOURCE("60011", "Server error occurred while updating resource" +
                "description."),
        ERROR_INTERNAL_SERVER_ERROR_FAILED_TO_LIST_RESOURCES("60012", "Server error occurred while listing resources."),
        ERROR_INTERNAL_SERVER_ERROR_FAILED_TO_CHECK_RESOURCE_SCOPE_EXISTENCE("60013", "Server error occurred while " +
                "checking whether resource scopes are existing."),
        ERROR_INTERNAL_SERVER_ERROR_FAILED_TO_UPDATE_PERMISSION_TICKET_STATE("60014", "Server error occurred while " +
                "updating permission ticket state."),
        ERROR_INTERNAL_SERVER_ERROR_FAILED_TO_CHECK_PERMISSION_TICKET_STATE("60015", "Server error occurred while " +
                "checking whether the permission ticket has expired."),
        ERROR_INTERNAL_SERVER_ERROR_FAILED_TO_PERSIST_REQUESTED_PERMISSIONS("60016", "Server error occurred while " +
                "persisting requested permissions."),
        ERROR_BAD_REQUEST_INVALID_PERMISSION_TICKET("60017", "Invalid permission ticket."),
        ERROR_UNEXPECTED("60019", "Unexpected error.");

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
        public static final String EXPIRY_TIME = "expiry_time";
        public static final String STATE = "state";
        public static final String TENANT_ID = "tenant_id";
        public static final String RESOURCE_ID = "resource_id";
        public static final String RESOURCE_SCOPE = "resource_scope";
        public static final String ID = "id";
        public static final String RESOURCE_OWNER_NAME = "resource_owner_name";
        public static final String RESOURCE_NAME = "resource_name";
        public static final String CLIENT_ID = "client_id";
        public static final String USER_DOMAIN = "user_domain";
        public static final String PROPERTY_KEY = "property_key";
        public static final String PROPERTY_VALUE = "property_value";
        public static final String SCOPE_NAME = "scope_name";
    }

    /**
     * Permission ticket states.
     */
    public static class PermissionTicketStates {

        public static final String PERMISSION_TICKET_STATE_ACTIVE = "ACTIVE";
        public static final String PERMISSION_TICKET_STATE_REVOKED = "REVOKED";
        public static final String PERMISSION_TICKET_STATE_EXPIRED = "EXPIRED";
    }
}

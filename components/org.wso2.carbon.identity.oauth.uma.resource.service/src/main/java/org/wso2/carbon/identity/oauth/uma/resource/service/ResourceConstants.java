/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.wso2.carbon.identity.oauth.uma.resource.service;

/**
 * This class holds the constants used by ResourceServiceImpl.
 */
public class ResourceConstants {

    /**
     * Error codes and messages.
     */
    public enum ErrorMessages {

        ERROR_CODE_FAIL_TO_GET_RESOURCE("60001", "Error occurred while retrieving Resource."),
        ERROR_CODE_NOT_FOUND_RESOURCE_ID("60002", "Resource id is not found."),
        ERROR_CODE_INVALID_RESOURCE_ID("60003", "Invalid resourceId is found."),
        ERROR_CODE_UNEXPECTED("60005", "Unexpected error");

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
}

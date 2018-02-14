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

package org.wso2.carbon.identity.oauth.uma.resource.service.exceptions;

import org.wso2.carbon.identity.oauth.uma.resource.service.ResourceConstants;

/**
 * This class express the exceptions handled in endpoint layer when client enter wrong data.
 */
public class UMAClientException extends UMAException {

    public UMAClientException() {

    }

    public UMAClientException(
            ResourceConstants.ErrorMessages errorCodeFailToGetResource) {

        super(errorCodeFailToGetResource.getCode(), errorCodeFailToGetResource.getMessage());
        this.setErrorDescription(errorCodeFailToGetResource.getMessage());
    }

    public UMAClientException(ResourceConstants.ErrorMessages message, Throwable throwable) {

        super(String.valueOf(message), throwable);
    }

    public UMAClientException(int stausCode, String errorMessage) {

        super(errorMessage);
        this.setStatusCode(stausCode);
        this.setErrorDescription(errorMessage);
    }

    public UMAClientException(int statusCode, String errorcode, String errorMessage) {

        super(errorMessage);
        this.setErrorCode(errorcode);
        this.setErrorDescription(errorMessage);
        this.setStatusCode(statusCode);
    }

    public UMAClientException(String errorcode, String errorMessage, Throwable throwable) {

        super(errorMessage, throwable);
        this.setErrorCode(errorcode);
        this.setErrorDescription(errorMessage);

    }
}

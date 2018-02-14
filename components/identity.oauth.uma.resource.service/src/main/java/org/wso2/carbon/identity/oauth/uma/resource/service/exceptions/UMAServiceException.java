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

import org.apache.commons.lang.StringUtils;
import org.wso2.carbon.identity.oauth.uma.resource.service.ResourceConstants;

import java.sql.SQLException;

/**
 * This class express the exceptions handle in impl layer where server failure occur.
 */
public class UMAServiceException extends UMAException {

    public UMAServiceException() {

        super();
    }


    public UMAServiceException(String message) {

        super(message);
    }

    public UMAServiceException(Throwable throwable) {

        super(throwable);
    }

    public UMAServiceException(
            ResourceConstants.ErrorMessages errorCodeFailToGetResource,
            String message, SQLException e) {

        super(errorCodeFailToGetResource.getCode(), message);
    }

    public UMAServiceException(ResourceConstants.ErrorMessages message1, Throwable throwable) {

        super(message1.getCode(), message1.getMessage(), throwable);
    }

    public UMAServiceException(String errorcode, String message) {

        super(errorcode, message);
    }

    public UMAServiceException(String errorCode, Throwable throwable, String message) {

        super(errorCode, message, throwable);
    }

    public UMAServiceException(String message, Throwable e) {

        super(message, e);
    }

    public String getErrorDescription() {

        String errorDescription = this.getMessage();
        if (StringUtils.isEmpty(errorDescription)) {
            errorDescription = ResourceConstants.ErrorMessages.ERROR_CODE_UNEXPECTED.getMessage();
        }
        return errorDescription;
    }

    private String getDefaultErrorCode() {

        String errorCode = super.getErrorCode();
        if (StringUtils.isEmpty(errorCode)) {
            errorCode = ResourceConstants.ErrorMessages.ERROR_CODE_UNEXPECTED.getCode();
        }
        return errorCode;
    }
}

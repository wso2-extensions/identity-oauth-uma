/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.identity.oauth.uma.endpoint.exception;

import org.wso2.carbon.identity.oauth.uma.endpoint.dto.ErrorResponseDTO;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * Custom exception for permission endpoint.
 */
public class PermissionEndpointException extends WebApplicationException {

    public PermissionEndpointException(Response.Status status, ErrorResponseDTO errorResponseDTO) {
        super(Response.status(status)
                .entity(errorResponseDTO)
                .build());
    }

    public PermissionEndpointException(Response.Status status) {
        super(Response.status(status)
                .build());
    }
}

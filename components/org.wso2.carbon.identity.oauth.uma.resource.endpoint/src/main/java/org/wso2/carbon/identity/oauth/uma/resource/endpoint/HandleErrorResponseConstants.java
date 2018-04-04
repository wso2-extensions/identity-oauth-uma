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

package org.wso2.carbon.identity.oauth.uma.resource.endpoint;

import java.util.HashMap;
import java.util.Map;

/**
 * HandleErrorResponseConstants class is used to handle exceptions in the endpoint layer.
 * */
public class HandleErrorResponseConstants {

    private String code;
    private Map<String, String[]> map;

    public HandleErrorResponseConstants() {
        this.code = null;
        this.map = initializeMapping();
    }

    private Map<String, String[]> initializeMapping() {

        Map<String, String[]> map = new HashMap<>();
        map.put("60001", new String[]{"400", "invalid_request"});
        map.put("60002", new String[]{"404", "not_found"});
        return map;
    }

    public String getCode() {

        return code;
    }

    public Map<String, String[]> getResponseMap() {

        return map;
    }
}

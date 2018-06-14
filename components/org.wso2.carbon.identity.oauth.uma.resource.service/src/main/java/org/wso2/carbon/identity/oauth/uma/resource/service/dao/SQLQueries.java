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

package org.wso2.carbon.identity.oauth.uma.resource.service.dao;

import org.wso2.carbon.identity.oauth.uma.common.UMAConstants;

/**
 * SQL queries related to resource registration.
 */
public class SQLQueries {

    public static final String STORE_RESOURCE = "INSERT INTO IDN_UMA_RESOURCE (RESOURCE_ID, RESOURCE_NAME, " +
            "TIME_CREATED, RESOURCE_OWNER_NAME, CLIENT_ID, TENANT_ID, USER_DOMAIN) VALUES (:"
            + UMAConstants.SQLPlaceholders.RESOURCE_ID + ";,:" + UMAConstants.SQLPlaceholders.RESOURCE_NAME + ";,:"
            + UMAConstants.SQLPlaceholders.TIME_CREATED + ";,:" + UMAConstants.SQLPlaceholders.RESOURCE_OWNER_NAME +
            ";,:" + UMAConstants.SQLPlaceholders.CLIENT_ID + ";,:" + UMAConstants.SQLPlaceholders.TENANT_ID + ";,:" +
            UMAConstants.SQLPlaceholders.USER_DOMAIN + ";)";

    public static final String STORE_RESOURCE_META_DETAILS = "INSERT INTO IDN_UMA_RESOURCE_META_DATA (" +
            "RESOURCE_IDENTITY, PROPERTY_KEY, PROPERTY_VALUE) VALUES (:" + UMAConstants.SQLPlaceholders.ID + ";,:"
            + UMAConstants.SQLPlaceholders.PROPERTY_KEY + ";,:" + UMAConstants.SQLPlaceholders.PROPERTY_VALUE +
            ";)";

    public static final String STORE_RESOURCE_SCOPES = "INSERT INTO IDN_UMA_RESOURCE_SCOPE (RESOURCE_IDENTITY, " +
            "SCOPE_NAME) VALUES (:" + UMAConstants.SQLPlaceholders.ID + ";,:" + UMAConstants.SQLPlaceholders.SCOPE_NAME
            + ";)";

    public static final String GET_RESOURCE_NAME = "SELECT RESOURCE_NAME FROM IDN_UMA_RESOURCE WHERE RESOURCE_ID = :" +
            UMAConstants.SQLPlaceholders.RESOURCE_ID + ";";

    public static final String GET_RESOURCE_META_DATA = "SELECT PROPERTY_KEY, PROPERTY_VALUE FROM " +
            "IDN_UMA_RESOURCE_META_DATA WHERE RESOURCE_IDENTITY = :" + UMAConstants.SQLPlaceholders.ID + ";";

    public static final String GET_RESOURCE_SCOPES = "SELECT SCOPE_NAME FROM IDN_UMA_RESOURCE_SCOPE WHERE " +
            "RESOURCE_IDENTITY = :" + UMAConstants.SQLPlaceholders.ID + ";";

    public static final String DELETE_RESOURCE = "DELETE FROM IDN_UMA_RESOURCE WHERE ID = :"
            + UMAConstants.SQLPlaceholders.ID + ";";

    public static final String DELETE_RESOURCE_META_DETAILS = "DELETE FROM IDN_UMA_RESOURCE_META_DATA WHERE " +
            "RESOURCE_IDENTITY = :" + UMAConstants.SQLPlaceholders.ID + ";";

    public static final String GET_ALL_RESOURCES = "SELECT RESOURCE_ID FROM IDN_UMA_RESOURCE WHERE " +
            "RESOURCE_OWNER_NAME = :" + UMAConstants.SQLPlaceholders.RESOURCE_OWNER_NAME + "; AND USER_DOMAIN = :" +
            UMAConstants.SQLPlaceholders.USER_DOMAIN + "; AND CLIENT_ID = :" + UMAConstants.SQLPlaceholders.CLIENT_ID
            + ";";

    public static final String UPDATE_RESOURCE = "UPDATE IDN_UMA_RESOURCE SET RESOURCE_NAME = :"
            + UMAConstants.SQLPlaceholders.RESOURCE_NAME + "; WHERE ID = :"
            + UMAConstants.SQLPlaceholders.ID + ";";

    public static final String DELETE_RESOURCE_SCOPES = "DELETE FROM IDN_UMA_RESOURCE_SCOPE WHERE RESOURCE_IDENTITY = :"
            + UMAConstants.SQLPlaceholders.ID + ";";

    public static final String CHECK_RESOURCE_NAME_EXISTENCE = "SELECT RESOURCE_NAME FROM IDN_UMA_RESOURCE WHERE " +
            "RESOURCE_OWNER_NAME = :" + UMAConstants.SQLPlaceholders.RESOURCE_OWNER_NAME + "; AND USER_DOMAIN = :" +
            UMAConstants.SQLPlaceholders.USER_DOMAIN + "; AND RESOURCE_NAME = :" +
            UMAConstants.SQLPlaceholders.RESOURCE_NAME + ";";

    public static final String CHECK_RESOURCE_ID_EXISTENCE = "SELECT ID FROM IDN_UMA_RESOURCE WHERE RESOURCE_ID = :"
            + UMAConstants.SQLPlaceholders.RESOURCE_ID + ";";

}

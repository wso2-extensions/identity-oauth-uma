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

/**
 * SQL queries related to dao file.
 */
public class SQLQueries {


    public static final String STORE_RESOURCE_DETAILS =
            "INSERT INTO IDN_RESOURCE(RESOURCE_ID,RESOURCE_NAME,TIME_CREATED," +
                    "RESOURCE_OWNER_NAME,TENANT_ID,CLIENT_ID) VALUES (?,?,?,?,?,?)";

    public static final String STORE_RESOURCE_META_DETAILS =
            "INSERT INTO IDN_RESOURCE_META_DATA(RESOURCE_IDENTITY,PROPERTY_KEY,PROPERTY_VALUE)" +
                    "VALUES ((SELECT ID FROM IDN_RESOURCE WHERE ID = ?),?,?);";

    public static final String STORE_SCOPES =
            "INSERT INTO IDN_RESOURCE_SCOPE(RESOURCE_IDENTITY,SCOPE_NAME) VALUES ((SELECT ID FROM IDN_RESOURCE WHERE " +
                    "ID = ?),?)";

    public static final String RETRIEVE_RESOURCES_BY_ID =
            "SELECT A.RESOURCE_ID,A.RESOURCE_NAME,B.PROPERTY_KEY,B.PROPERTY_VALUE,C.SCOPE_NAME FROM " +
                    "IDN_RESOURCE AS A,IDN_RESOURCE_META_DATA AS B,IDN_RESOURCE_SCOPE AS C WHERE " +
                    "C.RESOURCE_IDENTITY = B.RESOURCE_IDENTITY and B.RESOURCE_IDENTITY=A.ID AND" +
                    " RESOURCE_ID=?";

    public static final String DELETE_RESOURCE_DETAILS = "DELETE FROM IDN_RESOURCE WHERE RESOURCE_ID = ?;";

    public static final String DELETE_RESOURCE_META_DETAILS = "DELETE FROM IDN_RESOURCE_META_DATA WHERE " +
            "IDN_RESOURCE_META_DATA.RESOURCE_IDENTITY = (SELECT ID FROM IDN_RESOURCE WHERE RESOURCE_ID = ? );";

    public static final String DELETE_RESOURCE_SCOPES =
            "DELETE FROM IDN_RESOURCE_SCOPE WHERE IDN_RESOURCE_SCOPE.RESOURCE_IDENTITY =" +
                    " (SELECT ID FROM IDN_RESOURCE WHERE RESOURCE_ID = ? )";

    public static final String GET_ALL_RESOURCES = "SELECT RESOURCE_ID FROM IDN_RESOURCE WHERE RESOURCE_OWNER_NAME = ? "
            + "AND CLIENT_ID = ?;";

    public static final String UPDATE_RESOURCE = "UPDATE IDN_RESOURCE SET RESOURCE_NAME = ? WHERE RESOURCE_ID = ?;";

    public static final String UPDATE_METADATA = "UPDATE IDN_RESOURCE_META_DATA SET PROPERTY_VALUE = ? WHERE " +
            "IDN_RESOURCE_META_DATA.RESOURCE_IDENTITY = (SELECT ID FROM IDN_RESOURCE WHERE " +
            "RESOURCE_ID = ?) AND PROPERTY_KEY = ?;";

    public static final String DELETE_SCOPES = "DELETE FROM IDN_RESOURCE_SCOPE WHERE RESOURCE_IDENTITY " +
            "=(SELECT ID FROM IDN_RESOURCE WHERE RESOURCE_ID = ?);";

    public static final String UPDATE_SCOPES = "INSERT INTO IDN_RESOURCE_SCOPE(RESOURCE_IDENTITY,SCOPE_NAME) " +
            "VALUES ((SELECT ID FROM IDN_RESOURCE WHERE RESOURCE_ID = ?),?)";

    public static final String CHECK_EXISTANCE_OF_RESOURCE_NAME = "SELECT RESOURCE_NAME FROM IDN_RESOURCE WHERE " +
            "RESOURCE_OWNER_NAME = ? AND RESOURCE_NAME = ?";


}

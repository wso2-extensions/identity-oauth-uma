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
                    "RESOURCE_OWNER_NAME,TENANT_DOMAIN,CONSUMER_KEY) VALUES (?,?,?,?,?,?)";

    public static final String STORE_RESOURCE_META_DETAILS =
            "INSERT INTO IDN_RESOURCE_META_DATA(FK_RESOURCE_ID_META_DATA,PROPERTY_KEY,PROPERTY_VALUE)" +
                    "VALUES ((SELECT ID FROM IDN_RESOURCE WHERE ID = ?),?,?);";

    public static final String STORE_SCOPES =
            "INSERT INTO IDN_RESOURCE_SCOPE(FK_RESOURCE_ID,SCOPE_NAME) VALUES ((SELECT ID FROM IDN_RESOURCE WHERE " +
                    "ID = ?),?)";

    public static final String RETRIEVE_RESOURCES_BY_ID =
            "SELECT A.RESOURCE_ID,A.RESOURCE_NAME,B.PROPERTY_KEY,B.PROPERTY_VALUE,C.SCOPE_NAME FROM " +
                    "IDN_RESOURCE AS A,IDN_RESOURCE_META_DATA AS B,IDN_RESOURCE_SCOPE AS C WHERE " +
                    "C.FK_RESOURCE_ID = B.FK_RESOURCE_ID_META_DATA and B.FK_RESOURCE_ID_META_DATA=A.ID AND" +
                    " RESOURCE_ID=?";

    public static final String DELETE_RESOURCE_DETAILS = "DELETE FROM IDN_RESOURCE WHERE RESOURCE_ID = ?;";

    public static final String DELETE_RESOURCE_META_DETAILS = "DELETE FROM IDN_RESOURCE_META_DATA WHERE " +
            "IDN_RESOURCE_META_DATA.FK_RESOURCE_ID_META_DATA = (SELECT ID FROM IDN_RESOURCE WHERE RESOURCE_ID = ? );";

    public static final String DELETE_RESOURCE_SCOPES =
            "DELETE FROM IDN_RESOURCE_SCOPE WHERE IDN_RESOURCE_SCOPE.FK_RESOURCE_ID=" +
                    " (SELECT ID FROM IDN_RESOURCE WHERE RESOURCE_ID = ? )";

    public static final String GET_ALL_RESOURCES = "SELECT RESOURCE_ID FROM IDN_RESOURCE WHERE RESOURCE_OWNER_NAME = ? "
            + "AND CONSUMER_KEY = ?;";

    public static final String UPDATE_RESOURCE = "UPDATE IDN_RESOURCE SET RESOURCE_NAME = ? WHERE RESOURCE_ID = ?;";

    public static final String UPDATE_METADATA = "UPDATE IDN_RESOURCE_META_DATA SET PROPERTY_VALUE = ? WHERE " +
            "IDN_RESOURCE_META_DATA.FK_RESOURCE_ID_META_DATA = (SELECT ID FROM IDN_RESOURCE WHERE " +
            "RESOURCE_ID = ?) AND PROPERTY_KEY = ?;";

    public static final String DELETESCOPES = "DELETE FROM IDN_RESOURCE_SCOPE WHERE FK_RESOURCE_ID =(SELECT ID FROM " +
            "IDN_RESOURCE WHERE RESOURCE_ID = ?);";

    public static final String UPDATESCOPES = "INSERT INTO IDN_RESOURCE_SCOPE(FK_RESOURCE_ID,SCOPE_NAME) " +
            "VALUES ((SELECT ID FROM IDN_RESOURCE WHERE RESOURCE_ID = ?),?)";


}

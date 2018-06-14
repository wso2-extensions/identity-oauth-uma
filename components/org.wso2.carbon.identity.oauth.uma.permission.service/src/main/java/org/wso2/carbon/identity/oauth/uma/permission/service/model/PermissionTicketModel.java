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

package org.wso2.carbon.identity.oauth.uma.permission.service.model;

import java.sql.Timestamp;

/**
 * PermissionTicketModel holds all permission ticket related parameters.
 */
public class PermissionTicketModel {

    private String ticket;
    private String status;
    private Timestamp createdTime;
    private int tenantId;
    private Timestamp expiryTime;

    public String getTicket() {

        return ticket;
    }

    public void setTicket(String ticket) {

        this.ticket = ticket;
    }

    public String getStatus() {

        return status;
    }

    public void setStatus(String status) {

        this.status = status;
    }

    public Timestamp getCreatedTime() {

        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {

        this.createdTime = createdTime;
    }

    public int getTenantId() {

        return tenantId;
    }

    public void setTenantId(int tenantId) {

        this.tenantId = tenantId;
    }

    public Timestamp getExpiryTime() {

        return expiryTime;
    }

    public void setExpiryTime(Timestamp expiryTime) {

        this.expiryTime = expiryTime;
    }
}

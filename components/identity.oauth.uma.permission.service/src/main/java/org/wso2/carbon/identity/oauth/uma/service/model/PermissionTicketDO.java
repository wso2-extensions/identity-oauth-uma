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

package org.wso2.carbon.identity.oauth.uma.service.model;

import java.util.Calendar;

/**
 * PermissionTicketDO holds all permission ticket related parameters.
 */
public class PermissionTicketDO {

    private String ticket;
    private String status;
    private Calendar createdTime;
    private long validityPeriod;
    private String tenantId;

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

    public long getValidityPeriod() {

        return validityPeriod;
    }

    public void setValidityPeriod(long validityPeriod) {

        this.validityPeriod = validityPeriod;
    }

    public Calendar getCreatedTime() {

        return createdTime;
    }

    public void setCreatedTime(Calendar createdTime) {

        this.createdTime = createdTime;
    }

    public String getTenantId() {

        return tenantId;
    }

    public void setTenantId(String tenantId) {

        this.tenantId = tenantId;
    }
}

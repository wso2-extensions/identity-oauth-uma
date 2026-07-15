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

package org.wso2.carbon.identity.oauth.uma.permission.service.impl;

import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.oauth.config.OAuthServerConfiguration;
import org.wso2.carbon.identity.oauth.uma.permission.service.TestConstants;
import org.wso2.carbon.identity.oauth.uma.permission.service.dao.PermissionTicketDAO;
import org.wso2.carbon.identity.oauth.uma.permission.service.model.Resource;

import java.util.ArrayList;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertNotNull;

public class PermissionServiceImplTest {

    private PermissionServiceImpl permissionService;

    @Mock
    private OAuthServerConfiguration oAuthServerConfiguration;

    @BeforeMethod
    public void setUp() throws Exception {

        MockitoAnnotations.openMocks(this);
        permissionService = new PermissionServiceImpl();
    }

    @Test
    public void testIssuePermissionTicket() throws Exception {

        try (MockedStatic<OAuthServerConfiguration> mockedOAuthConfig = mockStatic(OAuthServerConfiguration.class);
             MockedStatic<PermissionTicketDAO> mockedPermissionTicketDAO = mockStatic(PermissionTicketDAO.class)) {
            mockedOAuthConfig.when(OAuthServerConfiguration::getInstance).thenReturn(oAuthServerConfiguration);
            when(oAuthServerConfiguration.getAuthorizationCodeValidityPeriodInSeconds()).thenReturn(300L);
            assertNotNull(permissionService.issuePermissionTicket(new ArrayList<Resource>(), TestConstants.TENANT_ID,
                    TestConstants.RESOURCE_OWNER_NAME, TestConstants.CLIENT_ID, TestConstants.USER_DOMAIN),
                    "Expected a not null object");
        }
    }

}

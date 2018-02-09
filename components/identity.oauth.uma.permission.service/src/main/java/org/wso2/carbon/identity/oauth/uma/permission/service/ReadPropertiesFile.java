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

package org.wso2.carbon.identity.oauth.uma.permission.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.oauth.uma.permission.service.model.PermissionTicketDO;
import org.wso2.carbon.utils.CarbonUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * ReadPropertiesFile is used to read configuration values from a properties file which is
 *required for a permission ticket generation.
 */
//TODO: Move this configuration to identity.xml
public class ReadPropertiesFile {

    private static Log log = LogFactory.getLog(ReadPropertiesFile.class);

    /**
     * @param permissionTicketDO Configuration values for permission ticket
     */
    public static void readFileConfigValues(PermissionTicketDO permissionTicketDO) {

        String configDirPath = CarbonUtils.getCarbonConfigDirPath();
        String confPath = Paths.get(configDirPath, "uma", UMAConstants.UMA_PERMISSION_ENDPOINT_CONFIG_PATH)
                .toString();
        File configfile = new File(confPath);
        if (!configfile.exists()) {
            log.warn("File is not present at: " + confPath);
            permissionTicketDO.setValidityPeriod(3600000);
        }

        Properties prop = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream(confPath);
            prop.load(input);
            long validityTimePeriod = Long.parseLong(prop.getProperty("validityperiod"));
            permissionTicketDO.setValidityPeriod(validityTimePeriod);
        } catch (IOException e) {
            log.error("Configuration values for permission ticket not found in the properties file. ", e);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    log.error(e);
                }
            }
        }
    }
}

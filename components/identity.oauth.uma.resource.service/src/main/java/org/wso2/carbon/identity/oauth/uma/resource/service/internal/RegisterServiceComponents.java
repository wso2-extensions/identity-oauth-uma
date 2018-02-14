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

package org.wso2.carbon.identity.oauth.uma.resource.service.internal;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.wso2.carbon.identity.oauth.uma.resource.service.ResourceService;
import org.wso2.carbon.identity.oauth.uma.resource.service.impl.ResourceServiceImpl;

/**
* Service component for UMA resource endpoint.
*/
@Component(name = "org.wso2.carbon.identity.oauth.uma.resource.service.internal.ResourceRegistrationerviceComponent",
        immediate = true)
public class RegisterServiceComponents {

    @Activate
    protected void activate(BundleContext bundleContext) {

        bundleContext.registerService(ResourceService.class.getName(), new ResourceServiceImpl(), null);
    }
}

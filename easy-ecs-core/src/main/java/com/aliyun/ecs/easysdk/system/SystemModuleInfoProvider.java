/*
 * Copyright (c) 2021-present, Alibaba Cloud All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aliyun.ecs.easysdk.system;

import com.aliyun.ecs.easysdk.system.config.ConfigKeys;
import com.aliyun.ecs.easysdk.system.config.ConfigurationService;
import com.aliyun.ecs.easysdk.container.EasyContainer;
import com.aliyun.ecs.easysdk.container.meta.ModuleInfo;
import com.aliyun.ecs.easysdk.container.meta.ModuleInfoBuilder;
import com.aliyun.ecs.easysdk.container.spi.ModuleInfoProvider;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemModuleInfoProvider implements ModuleInfoProvider {

    private static final Logger logger = LoggerFactory.getLogger(SystemModuleInfoProvider.class);

    @Override
    public ModuleInfo getModuleInfo() {
        ModuleInfoBuilder systemModuleInfoBuilder = new ModuleInfoBuilder("system");
        systemModuleInfoBuilder.addBeanInfo(ConfigurationService.class, ConfigurationService.getInstance(), "init", null);
        registerIAcsClient(systemModuleInfoBuilder);
        return systemModuleInfoBuilder.build();
    }

    private void registerIAcsClient(ModuleInfoBuilder moduleInfoBuilder) {
        IAcsClient iAcsClient = EasyContainer.DEFAULT_INSTANCE.getBean(IAcsClient.class);
        if (iAcsClient == null) {
            String accessKeyId = ConfigurationService.getInstance().getStringProperty(ConfigKeys.ALIYUN_EASY_ECS_SDK_ACCESS_KEY_ID);
            String accessSecret = ConfigurationService.getInstance().getStringProperty(ConfigKeys.ALIYUN_EASY_ECS_SDK_ACCESS_SECRET);
            if (StringUtils.isEmpty(accessKeyId) || StringUtils.isEmpty(accessSecret)) {
                throw new EasyEcsSDKInitializationException("Either IAcsClient instance or accessKey/secret should be configured");
            }
            String defaultRegionId = ConfigurationService.getInstance().getStringProperty(ConfigKeys.ALIYUN_EASY_ECS_SDK_DEFAULT_REGION_ID, "cn-beijing");
            IClientProfile profile = DefaultProfile.getProfile(defaultRegionId,
                    accessKeyId,
                    accessSecret);
            iAcsClient = new DefaultAcsClient(profile);
        }
        moduleInfoBuilder.addBeanInfo(IAcsClient.class, iAcsClient);
    }
}

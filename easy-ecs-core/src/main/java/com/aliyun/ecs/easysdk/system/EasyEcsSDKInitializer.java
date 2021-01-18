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

import com.aliyun.ecs.easysdk.container.EasyContainer;
import com.aliyun.ecs.easysdk.container.meta.ModuleInfo;
import com.aliyun.ecs.easysdk.container.spi.ModuleInfoProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class EasyEcsSDKInitializer {

    private static final Logger logger = LoggerFactory.getLogger(EasyEcsSDKInitializer.class);

    private static final EasyEcsSDKInitializer INSTANCE = new EasyEcsSDKInitializer();

    public static EasyEcsSDKInitializer getInstance() {
        return INSTANCE;
    }

    public void init() {
        //检索所有 ModuleInfo 配置文件，载入元数据信息
        List<ModuleInfo> moduleInfos = loadModuleInfosFromClassLoader();
        for(ModuleInfo moduleInfo : moduleInfos) {
            EasyContainer.DEFAULT_INSTANCE.registerModule(moduleInfo);
        }
        //初始化核心的 Module，注册相关 Bean 的信息
        EasyContainer.DEFAULT_INSTANCE.init();
    }

    private List<ModuleInfo> loadModuleInfosFromClassLoader() {
        List<ModuleInfo> moduleInfos = new ArrayList<>();
        ServiceLoader<ModuleInfoProvider> serviceLoaders = ServiceLoader.load(ModuleInfoProvider.class);
        for (ModuleInfoProvider moduleInfoProvider : serviceLoaders) {
            logger.info("module {} is loaded", moduleInfoProvider.getClass().getName());
            ModuleInfo moduleInfo = moduleInfoProvider.getModuleInfo();
            if(moduleInfo == null) {
                logger.error("null module info {} is returned", moduleInfoProvider.getClass().getName());
                continue;
            }
            moduleInfos.add(moduleInfo);
        }
        return moduleInfos;
    }

}

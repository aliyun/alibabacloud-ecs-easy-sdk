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
package com.aliyun.ecs.easysdk.preemptiveinstance;

import java.util.Map;

import com.aliyun.ecs.easysdk.container.meta.ModuleInfo;
import com.aliyun.ecs.easysdk.container.meta.ModuleInfoBuilder;
import com.aliyun.ecs.easysdk.container.spi.ModuleInfoProvider;
import com.aliyun.ecs.easysdk.preemptiveinstance.impl.CacheServiceImpl;
import com.aliyun.ecs.easysdk.preemptiveinstance.impl.PreemptiveInstanceBaseServiceCachedImpl;
import com.aliyun.ecs.easysdk.preemptiveinstance.impl.PreemptiveInstanceBaseServiceImpl;
import com.aliyun.ecs.easysdk.preemptiveinstance.impl.PreemptiveInstanceRecommendationServiceImpl;
import com.google.common.collect.Maps;

import static com.aliyun.ecs.easysdk.preemptiveinstance.constants.ModuleConstants.MODULE_NAME;

public class PreemptiveInstanceRecommendationModuleInfoProvider implements ModuleInfoProvider {

    @Override
    public ModuleInfo getModuleInfo() {
        ModuleInfoBuilder moduleInfoBuilder = new ModuleInfoBuilder(MODULE_NAME);
        Map<String, String> properties = Maps.newHashMap();
        properties.put("cached", "false");
        moduleInfoBuilder.addBeanInfo(PreemptiveInstanceBaseService.class, PreemptiveInstanceBaseServiceImpl.class,
            "init", properties);

        Map<String, String> properties2 = Maps.newHashMap();
        properties2.put("cached", "true");

        moduleInfoBuilder.addBeanInfo(PreemptiveInstanceBaseService.class,
            PreemptiveInstanceBaseServiceCachedImpl.class,
            "init", properties2);

        moduleInfoBuilder.addBeanInfo(PreemptiveInstanceRecommendationService.class,
            PreemptiveInstanceRecommendationServiceImpl.class, "init", null);
        moduleInfoBuilder.addBeanInfo(CacheService.class, CacheServiceImpl.class, "init");
        return moduleInfoBuilder.build();
    }
}

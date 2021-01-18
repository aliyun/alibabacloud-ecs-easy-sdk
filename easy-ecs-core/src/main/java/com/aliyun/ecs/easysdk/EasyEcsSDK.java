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
package com.aliyun.ecs.easysdk;

import com.aliyun.ecs.easysdk.container.EasyContainer;
import com.aliyun.ecs.easysdk.system.config.ConfigurationService;
import com.aliyun.ecs.easysdk.system.EasyEcsSDKInitializer;

import java.util.Map;

public class EasyEcsSDK {

    public static <T> T getService(Class<T> clazz) {
        return EasyContainer.DEFAULT_INSTANCE.getBean(clazz);
    }

    public static <T> T getService(Class<T> clazz, Map<String, String> properties) {
        return EasyContainer.DEFAULT_INSTANCE.getBean(clazz, properties);
    }

    public static <T> void setService(Class<T> clazz, T service) {
        setService(clazz, service, null);
    }

    public static <T> void setService(Class<T> clazz, T service, Map<String, String> properties) {
        EasyContainer.DEFAULT_INSTANCE.registerBean(clazz, service, properties);
    }

    public static void init() {
        EasyEcsSDKInitializer.getInstance().init();
    }

    public static String getProperty(String key) {
        return ConfigurationService.getInstance().getStringProperty(key);
    }

    public static void setProperty(String key, String value) {
        ConfigurationService.getInstance().setProperty(key, value);
    }
}

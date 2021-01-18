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
package com.aliyun.ecs.easysdk.container.meta;

import java.util.Arrays;
import java.util.Map;

public class BeanInfo {

    private Class<?>[] types;

    private Class<?> implType;

    private Object instance;

    private String initMethod;

    private Map<String, String> properties;

    public Class<?>[] getTypes() {
        return types;
    }

    public void setTypes(Class<?>[] types) {
        this.types = types;
    }

    public Class<?> getImplType() {
        return implType;
    }

    public void setImplType(Class<?> implType) {
        this.implType = implType;
    }

    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public String getInitMethod() {
        return initMethod;
    }

    public void setInitMethod(String initMethod) {
        this.initMethod = initMethod;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        return "BeanInfo{" +
                "types=" + Arrays.toString(types) +
                ", implType=" + implType +
                ", instance=" + instance +
                ", initMethod='" + initMethod +
                ", properties=" + properties +
                '}';
    }
}

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ModuleInfoBuilder {

    private String moduleName;

    private List<BeanInfo> beanInfos = new ArrayList<BeanInfo>();

    private Map<String, String> moduleProperties;

    public ModuleInfoBuilder(String moduleName) {
        this.moduleName = moduleName;
    }

    public ModuleInfo build() {
        ModuleInfo moduleInfo = new ModuleInfo();
        moduleInfo.setName(moduleName);
        moduleInfo.setBeanInfos(Collections.unmodifiableList(beanInfos));
        moduleInfo.setProperties(moduleProperties);
        return moduleInfo;
    }

    public void addBeanInfo(Class<?> type, Class<?> implType, String initMethod, Map<String, String> properties) {
        BeanInfo beanInfo = new BeanInfo();
        beanInfo.setTypes(new Class<?>[]{type});
        beanInfo.setImplType(implType);
        beanInfo.setProperties(properties);
        beanInfo.setInitMethod(initMethod);
        beanInfos.add(beanInfo);
    }

    public void addBeanInfo(Class<?> type, Class<?> implType, String initMethod) {
        addBeanInfo(type, implType, initMethod, null);
    }

    public void addBeanInfo(Class<?> type, Class<?> implType) {
        addBeanInfo(type, implType, null, null);
    }

    public void addBeanInfo(Class<?>[] types, Class<?> implType, String initMethod, Map<String, String> properties) {
        BeanInfo beanInfo = new BeanInfo();
        beanInfo.setTypes(types);
        beanInfo.setImplType(implType);
        beanInfo.setProperties(properties);
        beanInfo.setInitMethod(initMethod);
        beanInfos.add(beanInfo);
    }

    public void addBeanInfo(Class<?>[] types, Class<?> implType) {
        addBeanInfo(types, implType, null, null);
    }

    public void addBeanInfo(Class<?> type, Object instance, String initMethod, Map<String, String> properties) {
        BeanInfo beanInfo = new BeanInfo();
        beanInfo.setTypes(new Class<?>[]{type});
        beanInfo.setInstance(instance);
        beanInfo.setProperties(properties);
        beanInfo.setInitMethod(initMethod);
        beanInfos.add(beanInfo);
    }

    public void addBeanInfo(Class<?> type, Object instance) {
        addBeanInfo(new Class<?>[]{type}, instance, null, null);
    }

    public void addBeanInfo(Class<?>[] types, Object instance, String initMethod, Map<String, String> properties) {
        BeanInfo beanInfo = new BeanInfo();
        beanInfo.setTypes(types);
        beanInfo.setInstance(instance);
        beanInfo.setProperties(properties);
        beanInfo.setInitMethod(initMethod);
        beanInfos.add(beanInfo);
    }

    public void addBeanInfo(Class<?>[] types, Object instance) {
        addBeanInfo(types, instance, null, null);
    }

    public void setModuleProperties(Map<String, String> moduleProperties) {
        this.moduleProperties = moduleProperties;
    }
}

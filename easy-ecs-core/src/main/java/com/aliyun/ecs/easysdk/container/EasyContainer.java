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
package com.aliyun.ecs.easysdk.container;

import com.aliyun.ecs.easysdk.container.meta.ModuleInfo;
import com.aliyun.ecs.easysdk.container.runtime.*;
import com.aliyun.ecs.easysdk.container.runtime.impl.DefaultBeanRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class EasyContainer {

    public static final EasyContainer DEFAULT_INSTANCE = new EasyContainer();

    private static final Logger logger = LoggerFactory.getLogger(EasyContainer.class);

    private volatile boolean initialized;

    private Set<ModuleInfo> moduleInfos = new LinkedHashSet<ModuleInfo>();

    private BeanInjecterManager beanInjectManagaer = new BeanInjecterManager(this);

    private BeanLoaderManager beanLoaderManager = new BeanLoaderManager(this);

    private BeanMetadataManager beanMetadataManager = new BeanMetadataManager();

    private BeanRegistry beanRegistry = new DefaultBeanRegistry();

    public void registerModule(ModuleInfo moduleInfo) {
        if (initialized) {
            throw new EasyContainerException("MouduleInfo can only registered before the initialization");
        }
        this.moduleInfos.add(moduleInfo);
    }

    public void inject(Object instance) {
        try {
            beanInjectManagaer.inject(instance);
        } catch (Exception e) {
            throw new EasyContainerException("fail to execute the injection for bean " + instance.getClass().getName(), e);
        }
    }

    public <T> T getBean(Class<T> type, Map<String, String> filterProperties) {
        return beanRegistry.getBean(type, filterProperties);
    }

    public <T> T[] getBeans(Class<T> type, Map<String, String> filterProperties) {
        return beanRegistry.getBeans(type, filterProperties);
    }

    public <T> T getBean(Class<T> type) {
        return getBean(type, null);
    }

    public <T> T[] getBeans(Class<T> type) {
        return getBeans(type, null);
    }

    public BeanRegistration registerBean(Class<?> type, Object instance, Map<String, String> properties) {
        return beanRegistry.registerBean(new Class<?>[]{type}, instance, properties);
    }

    public BeanRegistration registerBean(Class<?>[] types, Object instance, Map<String, String> properties) {
        return beanRegistry.registerBean(types, instance, properties);
    }

    public void init() {
        if (initialized) {
            return;
        }
        try {
            BeanMetadataManager.BeanRuntimeInfo[] beanRuntimeInfos = this.beanMetadataManager.buildBeanRuntimeInfos(this.moduleInfos);
            this.beanLoaderManager.load(beanRuntimeInfos);
            this.initialized = true;
            return;
        } catch (Exception e) {
            logger.error("Fail to init the pluto container", e);
        }
        throw new EasyContainerException("Fail to initialize the EasyContainer");
    }

    public void setBeanInjectManagaer(BeanInjecterManager beanInjectManagaer) {
        this.beanInjectManagaer = beanInjectManagaer;
    }

    public void setBeanLoaderManager(BeanLoaderManager beanLoaderManager) {
        this.beanLoaderManager = beanLoaderManager;
    }

    public BeanInjecterManager getBeanInjectManagaer() {
        return this.beanInjectManagaer;
    }

    public BeanLoaderManager getBeanLoaderManager() {
        return this.beanLoaderManager;
    }

    public BeanMetadataManager getBeanMetadataManager() {
        return this.beanMetadataManager;
    }

    public void setBeanMetadataManager(BeanMetadataManager beanMetadataManager) {
        this.beanMetadataManager = beanMetadataManager;
    }

    public Set<ModuleInfo> getModuleInfos() {
        return Collections.unmodifiableSet(moduleInfos);
    }
}

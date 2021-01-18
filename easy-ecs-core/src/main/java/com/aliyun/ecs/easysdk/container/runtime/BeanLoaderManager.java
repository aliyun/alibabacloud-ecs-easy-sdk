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
package com.aliyun.ecs.easysdk.container.runtime;

import com.aliyun.ecs.easysdk.container.EasyContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class BeanLoaderManager {

    private static final Logger logger = LoggerFactory.getLogger(BeanLoaderManager.class);

    private EasyContainer easyContainer;

    public BeanLoaderManager(EasyContainer easyContainer) {
        this.easyContainer = easyContainer;
    }

    public void load(BeanMetadataManager.BeanRuntimeInfo[] beanRuntimeInfos) {
        if (beanRuntimeInfos == null || beanRuntimeInfos.length == 0) {
            return;
        }
        for (BeanMetadataManager.BeanRuntimeInfo beanRuntimeInfo : beanRuntimeInfos) {
            Object instance = null;
            //载入 Bean 对象
            if (beanRuntimeInfo.beanInfo.getInstance() == null) {
                try {
                    Field instanceField = beanRuntimeInfo.beanInfo.getImplType().getDeclaredField("INSTANCE");
                    instance = instanceField.get(null);
                } catch (Exception e) {
                    logger.info("No instance field detect for bean " + beanRuntimeInfo.name);
                }
                Constructor<?> constructor = null;
                Object[] arguments = null;
                try {
                    constructor = beanRuntimeInfo.beanInfo.getImplType().getConstructor();
                } catch (NoSuchMethodException e) {
                    throw new EasyContainerException("No support consturctor method found", e);
                }
                try {
                    instance = constructor.newInstance(arguments);
                } catch (Exception e) {
                    throw new EasyContainerException("fail to create bean instance", e);
                }
            } else {
                instance = beanRuntimeInfo.beanInfo.getInstance();
            }
            //Annotation 注入
            easyContainer.getBeanInjectManagaer().inject(instance, beanRuntimeInfo.injectFields);
            //调用初始化方法
            if (beanRuntimeInfo.initMethod != null) {
                try {
                    beanRuntimeInfo.initMethod.setAccessible(true);
                    if (beanRuntimeInfo.initMethod.getParameterTypes().length == 0) {
                        beanRuntimeInfo.initMethod.invoke(instance);
                    }
                } catch (Exception e) {
                    throw new EasyContainerException("fail to invoke the init method", e);
                }
            }
            //注册服务
            easyContainer.registerBean(beanRuntimeInfo.beanInfo.getTypes(), instance, beanRuntimeInfo.beanInfo.getProperties());
        }
    }
}

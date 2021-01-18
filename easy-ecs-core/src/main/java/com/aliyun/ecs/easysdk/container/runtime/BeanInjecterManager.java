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
import com.aliyun.ecs.easysdk.container.annotation.Autowired;
import com.aliyun.ecs.easysdk.container.annotation.BeanProperty;
import com.aliyun.ecs.easysdk.container.annotation.Qualifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanInjecterManager {

    private static final Logger logger = LoggerFactory.getLogger(BeanInjecterManager.class);

    private EasyContainer easyContainer;

    public BeanInjecterManager(EasyContainer easyContainer) {
        this.easyContainer = easyContainer;
    }

    public void inject(Object instance) {
        if (instance == null) {
            return;
        }
        Class<?> clazz = instance.getClass();
        List<Field> injectFields = easyContainer.getBeanMetadataManager().getInjectionFields(clazz);
        if (injectFields == null || injectFields.size() == 0) {
            return;
        }
        inject(instance, injectFields);
    }

    public void inject(Object instance, List<Field> injectFields) {
        if (instance == null || injectFields == null || injectFields.size() == 0) {
            return;
        }

        for (Field injectField : injectFields) {
            Map<String, String> filter = null;
            Qualifier qualifier = injectField.getAnnotation(Qualifier.class);
            if (qualifier != null && qualifier.filters() != null && qualifier.filters().length > 0) {
                filter = new HashMap<String, String>();
                BeanProperty[] beanProperties = qualifier.filters();
                for (BeanProperty beanProperty : beanProperties) {
                    if (beanProperty.key() == null || beanProperty.value() == null) {
                        continue;
                    }
                    filter.put(beanProperty.key(), beanProperty.value());
                }
            }
            //
            Autowired autowired = injectField.getAnnotation(Autowired.class);
            Object targetBean = easyContainer.getBean(injectField.getType(), filter);
            if (targetBean == null) {
                logger.warn("No avaiable service for field " + injectField.getDeclaringClass().getName() + "." + injectField.getName());
                continue;
            }
            try {
                injectField.setAccessible(true);
                injectField.set(instance, targetBean);
            } catch (Exception e) {
                logger.error("Fail to inject the field " + injectField.getDeclaringClass().getName() + "." + injectField.getName(), e);
            }
        }
    }

}

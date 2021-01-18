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

import com.aliyun.ecs.easysdk.container.annotation.Autowired;
import com.aliyun.ecs.easysdk.container.annotation.BeanProperty;
import com.aliyun.ecs.easysdk.container.annotation.Qualifier;
import com.aliyun.ecs.easysdk.container.meta.BeanInfo;
import com.aliyun.ecs.easysdk.container.meta.ModuleInfo;
import com.aliyun.ecs.easysdk.container.meta.SortInfo;
import com.aliyun.ecs.easysdk.container.util.SortUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class BeanMetadataManager {

    private static final Logger logger = LoggerFactory.getLogger(BeanMetadataManager.class);

    private String[] systemPackagePrefixes = {"android.", "java.", "javax."};

    protected boolean isSystemClass(Class<?> clazz) {
        if (clazz == null || clazz == Object.class) {
            return true;
        }
        String clazzName = clazz.getName();
        for (String systemPackagePrefix : systemPackagePrefixes) {
            if (clazzName.startsWith(systemPackagePrefix)) {
                return true;
            }
        }
        return false;
    }

    public void setSystemPackagePrefixes(String[] systemPackagePrefixes) {
        if (systemPackagePrefixes == null) {
            return;
        }
        String[] copiedSystemPackagePrefixes = new String[systemPackagePrefixes.length];
        System.arraycopy(systemPackagePrefixes, 0, copiedSystemPackagePrefixes, 0, systemPackagePrefixes.length);
        this.systemPackagePrefixes = copiedSystemPackagePrefixes;
    }

    protected BeanRuntimeInfo parse(BeanInfo beanInfo) {
        BeanRuntimeInfo runtimeInfo = new BeanRuntimeInfo();
        runtimeInfo.beanInfo = beanInfo;
        if (beanInfo.getImplType() == null && beanInfo.getInstance() == null) {
            throw new IllegalStateException("implType and instance could not be null at the same time for bean " + beanInfo);
        }
        Class<?> beanImplClazz = beanInfo.getImplType() == null ? beanInfo.getInstance().getClass() : beanInfo.getImplType();
        runtimeInfo.injectFields = getInjectionFields(beanImplClazz);
        if (beanInfo.getInitMethod() != null) {
            //now, init method must be public ...
            try {
                runtimeInfo.initMethod = beanImplClazz.getMethod(beanInfo.getInitMethod());
            } catch (Exception e1) {
                throw new EasyContainerException("invalid init method for bean " + beanInfo, e1);
            }
        }
        return runtimeInfo;
    }

    public List<Field> getInjectionFields(Class<?> clazz) {
        List<Field> injectionFields = new ArrayList<Field>();
        while (!isSystemClass(clazz)) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class) || field.isAnnotationPresent(Resource.class)) {
                    injectionFields.add(field);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return injectionFields;
    }

    public BeanRuntimeInfo[] buildBeanRuntimeInfos(Set<ModuleInfo> moduleInfos) {
        List<BeanRuntimeInfo> beanRuntimeInfos = new ArrayList<BeanRuntimeInfo>();

        Map<Class<?>, List<BeanRuntimeInfo>> typeBeanRuntimeInfos = new HashMap<Class<?>, List<BeanRuntimeInfo>>();

        //首先初始化 Bean 列表
        int beanIndex = 0;
        for (ModuleInfo moduleInfo : moduleInfos) {
            if (moduleInfo.getBeanInfos() == null) {
                continue;
            }

            Map<String, String> defaultBeanProperties = Collections.singletonMap("module", moduleInfo.getName());

            for (BeanInfo beanInfo : moduleInfo.getBeanInfos()) {

                if (beanInfo.getProperties() == null) {
                    beanInfo.setProperties(defaultBeanProperties);
                } else {
                    beanInfo.setProperties(new HashMap<String, String>(beanInfo.getProperties()));
                    beanInfo.getProperties().put("module", moduleInfo.getName());
                }

                BeanRuntimeInfo beanRuntimeInfo = parse(beanInfo);
                //生成 BeanName
                String shortName = beanInfo.getImplType() == null ? beanInfo.getInstance().getClass().getSimpleName() : beanInfo.getImplType().getSimpleName();
                beanRuntimeInfo.name = shortName + "." + beanIndex++;
                beanRuntimeInfos.add(beanRuntimeInfo);

                for (Class<?> type : beanInfo.getTypes()) {
                    List<BeanRuntimeInfo> curBeanRuntimeInfos = typeBeanRuntimeInfos.get(type);
                    if (curBeanRuntimeInfos == null) {
                        curBeanRuntimeInfos = new ArrayList<BeanRuntimeInfo>();
                        typeBeanRuntimeInfos.put(type, curBeanRuntimeInfos);
                    }
                    curBeanRuntimeInfos.add(beanRuntimeInfo);
                }
            }
        }

        //生成依赖关系
        for (BeanRuntimeInfo beanRuntimeInfo : beanRuntimeInfos) {
            if (beanRuntimeInfo.injectFields == null) {
                beanRuntimeInfo.after = Collections.emptyList();
                beanRuntimeInfo.injectFields = Collections.emptyList();
            } else {
                beanRuntimeInfo.after = new ArrayList<String>();
            }
            for (Field field : beanRuntimeInfo.injectFields) {
                Autowired autowiredAnnotation = field.getAnnotation(Autowired.class);

                Class<?> fieldType = field.getType();
                List<BeanRuntimeInfo> targetBeanRuntimeInfos = typeBeanRuntimeInfos.get(fieldType);
                if (targetBeanRuntimeInfos == null) {
                    String errorMessage = "fail to find matched bean for " + field.getDeclaringClass().getName() + "." + field.getName();
                    logger.error(errorMessage);
                    throw new EasyContainerException(errorMessage);
                }
                Qualifier qualifier = field.getAnnotation(Qualifier.class);
                if (qualifier == null || qualifier.filters() == null || qualifier.filters().length == 0) {
                    if (targetBeanRuntimeInfos.size() > 1) {
                        String errorMessage = "more then one matched bean for " + field.getDeclaringClass().getName() + "." + field.getName() +
                                " matched beans " + getBeanImplDescriptionInfo(targetBeanRuntimeInfos);
                        logger.error(errorMessage);
                        throw new EasyContainerException(errorMessage);
                    }
                    //by default, we add the first bean as the dependency
                    beanRuntimeInfo.after.add(targetBeanRuntimeInfos.get(0).name);
                } else {
                    BeanProperty[] filters = qualifier.filters();
                    List<BeanRuntimeInfo> matchedBeanRuntimeInfos = new ArrayList<BeanRuntimeInfo>(targetBeanRuntimeInfos.size());

                    for (BeanRuntimeInfo targetBeanRuntimeInfo : targetBeanRuntimeInfos) {
                        boolean matched = true;
                        for (BeanProperty beanProperty : filters) {
                            String value = targetBeanRuntimeInfo.beanInfo.getProperties().get(beanProperty.key());
                            if (value == null || !value.equals(beanProperty.value())) {
                                matched = false;
                                break;
                            }
                        }
                        if (matched) {
                            matchedBeanRuntimeInfos.add(targetBeanRuntimeInfo);
                        }
                    }
                    if (matchedBeanRuntimeInfos.size() == 1) {
                        beanRuntimeInfo.after.add(matchedBeanRuntimeInfos.get(0).name);
                    } else if (matchedBeanRuntimeInfos.size() == 0 && autowiredAnnotation.required()) {
                        String errorMessage = "fail to find matched bean for " + field.getDeclaringClass().getName() + "." + field.getName();
                        logger.error(errorMessage);
                        throw new EasyContainerException(errorMessage);
                    } else if (matchedBeanRuntimeInfos.size() > 1) {
                        String errorMessage = "more then one matched bean for " + field.getDeclaringClass().getName() + "." + field.getName() +
                                " matched beans " + getBeanImplDescriptionInfo(targetBeanRuntimeInfos);
                        logger.error(errorMessage);
                        throw new EasyContainerException(errorMessage);
                    }
                }
            }
        }

        BeanRuntimeInfo[] beanRuntimeInfoArray = beanRuntimeInfos.toArray(new BeanRuntimeInfo[0]);
        //排序
        SortUtils.sorts(beanRuntimeInfoArray);
        //返回
        return beanRuntimeInfoArray;
    }

    private String getBeanImplDescriptionInfo(List<BeanRuntimeInfo> beanRuntimeInfos) {
        StringBuilder buffer = new StringBuilder();
        int index = 0;
        for (BeanRuntimeInfo beanRuntimeInfo : beanRuntimeInfos) {
            buffer.append(index).append(" : ");
            if (beanRuntimeInfo.beanInfo.getImplType() != null) {
                buffer.append(beanRuntimeInfo.beanInfo.getImplType());
            } else if (beanRuntimeInfo.beanInfo.getInstance() != null) {
                buffer.append(beanRuntimeInfo.beanInfo.getInstance());
            } else {
                buffer.append("null implType and instance");
            }
            buffer.append("\n");
            index++;
        }
        return buffer.toString();
    }

    public static class BeanRuntimeInfo extends SortInfo {

        public BeanInfo beanInfo;

        public Method initMethod;

        public List<Field> injectFields;
    }
}

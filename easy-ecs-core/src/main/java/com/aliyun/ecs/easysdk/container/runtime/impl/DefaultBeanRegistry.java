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
package com.aliyun.ecs.easysdk.container.runtime.impl;

import com.aliyun.ecs.easysdk.container.runtime.BeanRegistration;
import com.aliyun.ecs.easysdk.container.runtime.BeanRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DefaultBeanRegistry implements BeanRegistry {

    private static final Logger logger = LoggerFactory.getLogger(DefaultBeanRegistry.class);

    private Map<Class<?>, List<ServiceEntry>> typeServiceEntries = new HashMap<Class<?>, List<ServiceEntry>>();

    private Map<BeanRegistration, ServiceEntry> registrationServiceEntries = new HashMap<BeanRegistration, ServiceEntry>();

    private ReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public BeanRegistration registerBean(Class<?>[] types, Object instance, Map<String, String> properties) {
        if (types == null || types.length == 0 || instance == null) {
            throw new IllegalArgumentException("service types and instance must not be null");
        }

        ServiceEntry serviceEntry = new ServiceEntry();
        serviceEntry.instance = instance;
        serviceEntry.types = types;
        serviceEntry.properties = Collections.synchronizedMap(new HashMap<String, String>());
        if (properties != null) {
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                if (entry.getKey() == null || entry.getValue() == null) {
                    continue;
                }
                serviceEntry.properties.put(entry.getKey(), entry.getValue());
            }
        }

        lock.writeLock().lock();
        try {
            for (Class<?> type : types) {
                List<ServiceEntry> instances = typeServiceEntries.get(type);
                if (instances == null) {
                    instances = new ArrayList<ServiceEntry>(2);
                    typeServiceEntries.put(type, instances);
                }
                instances.add(serviceEntry);
            }

            BeanRegistration serviceRegistration = new InternalServiceRegistration(this, serviceEntry.properties);
            this.registrationServiceEntries.put(serviceRegistration, serviceEntry);
            return serviceRegistration;
        } finally {
            lock.writeLock().unlock();
        }

    }

    @Override
    public <T> T getBean(Class<T> type, Map<String, String> filterProperties) {
        lock.readLock().lock();
        try {
            List<ServiceEntry> serviceEntries = typeServiceEntries.get(type);
            if (serviceEntries == null || serviceEntries.size() == 0) {
                return null;
            }

            if (filterProperties == null || filterProperties.size() == 0) {
                return type.cast(serviceEntries.get(0).instance);
            }

            for (ServiceEntry serviceEntry : serviceEntries) {
                if (isServiceMatched(serviceEntry, filterProperties)) {
                    return type.cast(serviceEntry.instance);
                }
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public <T> T[] getBeans(Class<T> type, Map<String, String> filterProperties) {

        lock.readLock().lock();
        try {
            List<ServiceEntry> serviceEntries = typeServiceEntries.get(type);
            if (serviceEntries == null || serviceEntries.size() == 0) {
                return (T[]) Array.newInstance(type, 0);
            }

            if (filterProperties == null || filterProperties.size() == 0) {
                T[] serviceInstances = (T[]) Array.newInstance(type, serviceEntries.size());
                for (int i = 0, length = serviceEntries.size(); i < length; i++) {
                    serviceInstances[i] = type.cast(serviceEntries.get(i).instance);
                }
                return serviceInstances;
            }

            List<T> serviceInstances = new ArrayList<T>(serviceEntries.size());
            for (ServiceEntry serviceEntry : serviceEntries) {
                if (isServiceMatched(serviceEntry, filterProperties)) {
                    serviceInstances.add(type.cast(serviceEntry.instance));
                }
            }
            return serviceInstances.toArray((T[]) Array.newInstance(type, serviceInstances.size()));
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void recycle() {
        lock.writeLock().lock();
        try {
            this.registrationServiceEntries.clear();
            this.typeServiceEntries.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Object unregisterBean(BeanRegistration registration) {
        if (registration == null) {
            return null;
        }

        lock.writeLock().lock();
        try {
            ServiceEntry serviceEntry = registrationServiceEntries.remove(registration);
            if (serviceEntry == null) {
                return null;
            }

            if (serviceEntry.types != null) {
                for (Class<?> type : serviceEntry.types) {
                    List<ServiceEntry> serviceEntries = typeServiceEntries.get(type);

                    // Use object identity to remote the service instance
                    for (Iterator<ServiceEntry> it = serviceEntries.iterator(); it.hasNext(); ) {
                        if (it.next() == serviceEntry) {
                            it.remove();
                            break;
                        }
                    }
                    //

                    if (serviceEntries.size() == 0) {
                        typeServiceEntries.remove(type);
                    }
                }
            }
            return serviceEntry.instance;
        } finally {
            lock.writeLock().unlock();
        }
    }

    private boolean isServiceMatched(ServiceEntry serviceEntry, Map<String, String> filterProperties) {
        for (Map.Entry<String, String> entry : filterProperties.entrySet()) {
            String value = serviceEntry.properties.get(entry.getKey());
            if (value == null || !value.equals(entry.getValue())) {
                return false;
            }
        }
        return true;
    }

    static class ServiceEntry {
        public Class<?>[] types;
        public Object instance;
        public Map<String, String> properties;
    }

    static class InternalServiceRegistration implements BeanRegistration {

        private final String uuid = UUID.randomUUID().toString();

        private BeanRegistry serviceRegistry;

        private Map<String, String> properties;

        public InternalServiceRegistration(BeanRegistry serviceRegistry, Map<String, String> properties) {
            this.serviceRegistry = serviceRegistry;
            this.properties = properties;
        }

        @Override
        public void setProperties(Map<String, String> properties) {
            if (properties == null) {
                return;
            }
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                if (entry.getKey() != null && entry.getValue() != null) {
                    this.properties.put(entry.getKey(), entry.getValue());
                }
            }
        }

        @Override
        public void unregister() {
            serviceRegistry.unregisterBean(this);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            InternalServiceRegistration other = (InternalServiceRegistration) obj;
            return uuid.equals(other.uuid);
        }
    }
}

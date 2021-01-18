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
package com.aliyun.ecs.easysdk.system.config;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigurationService {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationService.class);

    private static final String CONFIG_PROPERTIES_FILE_NAME = "ecs.easysdk.properties";

    private static ConfigurationService INSTANCE = new ConfigurationService();

    private Map<String, String> properties = new ConcurrentHashMap<>();

    public static ConfigurationService getInstance() {
        return INSTANCE;
    }

    private ConfigurationService() {
    }

    /**
     * load local property files, includes the default configurations
     */
    public void init() {
        //load property file with classloader
        String propertyFileName = getStringProperty(ConfigKeys.ALIYUN_EASY_ECS_SDK_CONFIGURATION_FILE_NAME, CONFIG_PROPERTIES_FILE_NAME);
        InputStream in = null;
        try {
            in = ConfigurationService.class.getClassLoader().getResourceAsStream(propertyFileName);
            if(in != null) {
                Properties configProperties = new Properties();
                configProperties.load(in);
                for(Map.Entry<Object, Object> entry : configProperties.entrySet()) {
                    properties.putIfAbsent((String)entry.getKey(), (String)entry.getValue());
                }
            }
        } catch (Exception e) {
            logger.error("Fail to read the config file " + propertyFileName, e);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    public String getStringProperty(String key) {
        return properties.get(key);
    }

    public String getStringProperty(String key, String defaultValue) {
        String value = getStringProperty(key);
        return value == null ? defaultValue : value;
    }

    public int getIntProperty(String key, int defaultValue) {
        String value = getStringProperty(key);
        if (value != null && value.length() != 0) {
            try {
                return Integer.parseInt(value);
            } catch (Exception e) {
                //ignore
            }
        }
        return defaultValue;
    }

    public long getLongProperty(String key, long defaultValue) {
        String value = getStringProperty(key);
        if (value != null && value.length() != 0) {
            try {
                return Long.parseLong(value);
            } catch (Exception e) {
                //ignore
            }
        }
        return defaultValue;
    }

    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getStringProperty(key);
        if (value != null && value.length() != 0) {
            try {
                return Boolean.parseBoolean(value);
            } catch (Exception e) {
                //ignore
            }
        }
        return defaultValue;
    }

    public void setProperty(String key, String value) {
        properties.put(key, value);
    }
}

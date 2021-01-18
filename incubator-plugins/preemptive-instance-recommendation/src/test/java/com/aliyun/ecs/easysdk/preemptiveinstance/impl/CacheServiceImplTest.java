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
package com.aliyun.ecs.easysdk.preemptiveinstance.impl;

import java.util.List;

import com.alibaba.fastjson.JSON;

import com.aliyun.ecs.easysdk.EasyEcsSDK;
import com.aliyun.ecs.easysdk.preemptiveinstance.CacheService;
import com.aliyun.ecs.easysdk.system.config.ConfigKeys;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class CacheServiceImplTest {
    static CacheService cacheService;

    @BeforeClass
    public static void setUp() {
        EasyEcsSDK.setProperty(ConfigKeys.ALIYUN_EASY_ECS_SDK_ACCESS_KEY_ID, "<your-access-key>");
        EasyEcsSDK.setProperty(ConfigKeys.ALIYUN_EASY_ECS_SDK_ACCESS_SECRET, "<your-secret-key>");
        EasyEcsSDK.init();
        cacheService = EasyEcsSDK.getService(CacheService.class);
    }

    @Test
    public void testCache(){
        cacheService.put("sample-key","sample-value");
        Assert.assertEquals("sample-value",cacheService.get("sample-key"));
        Assert.assertNull(cacheService.get("random-key"));

        cacheService.put(Lists.newArrayList("name-a","name-b"),"name-value");
        Assert.assertEquals("name-value",cacheService.get(Lists.newArrayList("name-a","name-b")));
        Assert.assertNull(cacheService.get(Lists.newArrayList("name-c","name-d")));
    }
}

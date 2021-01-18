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
import java.util.concurrent.TimeUnit;

import com.aliyun.ecs.easysdk.preemptiveinstance.CacheService;
import com.aliyun.ecs.easysdk.preemptiveinstance.utils.CacheHelper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class CacheServiceImpl implements CacheService {

    private Cache<String, String> cache;

    public void init() {
        cache = CacheBuilder.newBuilder().expireAfterAccess(1L, TimeUnit.HOURS).build();
    }

    @Override
    public void put(String key, String value) {
        cache.put(key, value);
    }

    @Override
    public void put(List<String> parts, String value) {
        cache.put(CacheHelper.getKey(parts), value);
    }

    @Override
    public String get(String key) {
        return cache.asMap().get(key);
    }

    @Override
    public String get(List<String> parts) {
        return get(CacheHelper.getKey(parts));
    }

}

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
package com.aliyun.ecs.easysdk.preemptiveinstance.constants;

public class EcsInstanceConstants {
    public static final String ECS_API_DEFAULT_REGION = "cn-beijing";
    public static final Integer LARGE_INSTANCE_TYPE_CORES = 32;
    /**
     * 同代折扣绝对差<=5, 则认为基本无差异.
     */
    public static final Integer DISCOUNT_DIFF_IN_SAME_GENERATION = 5;
    /**
     * 入门级产品跨代折扣绝对差<=8, 则认为基本无差异;非入门级产品跨代折扣绝对差<=15
     */
    public static final Integer DISCOUNT_DIFF_IN_DIFFERENT_GENERATION_OTHER_CATEGORY = 15;
    public static final Integer DISCOUNT_DIFF_IN_DIFFERENT_GENERATION_ENTRY_LEVEL = 8;


    /**
     * 同代价格差异<=10%, 则认为基本无差异
     */
    public static final Integer PRICE_DIFF_IN_SAME_GENERATION = 10;
    /**
     * 跨代价格差异<=20%, 则认为基本无差异
     */
    public static final Integer PRICE_DIFF_IN_DIFFERENT_GENERATION = 20;
}

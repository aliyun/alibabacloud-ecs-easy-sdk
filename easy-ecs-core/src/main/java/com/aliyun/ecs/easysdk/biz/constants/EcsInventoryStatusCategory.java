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
package com.aliyun.ecs.easysdk.biz.constants;

/**
 * 参照<a href="https://help.aliyun.com/document_detail/66186.html">DescribeAvailable</a>接口描述的库存状态
 */
public enum EcsInventoryStatusCategory {
    /**
     * 有库存, 库存补货能力正常
     */
    WithStock,
    /**
     * 有库存, 库存补货能力低
     */
    ClosedWithStock,
    /**
     * 无库存, 库存补货能力正常
     */
    WithoutStock,
    /**
     * 无库存, 库存补货能力低
     */
    ClosedWithoutStock;
}

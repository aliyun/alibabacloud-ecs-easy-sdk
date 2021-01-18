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
package com.aliyun.ecs.easysdk.preemptiveinstance.strategy;

import com.aliyun.ecs.easysdk.preemptiveinstance.model.DiscountInventoryModel;
import com.aliyun.ecs.easysdk.preemptiveinstance.model.SpotPrice;

import java.io.Serializable;
import java.util.Comparator;

public class InventoryComparator implements Comparator<DiscountInventoryModel>, Serializable {

    private static final long serialVersionUID = 5497222642129803879L;

    @Override
    public int compare(DiscountInventoryModel a, DiscountInventoryModel b) {
        SpotPrice price = a.getPrice();
        SpotPrice price1 = b.getPrice();
        // 1. 按照同规格族价格阶梯进行渐进式的比较
        int compareOutput = StrategyUtils.progressiveDiscountComparison(a, b);
        if (compareOutput != 0) {
            return compareOutput;
        }
        // 2: 相同大规格折扣情况下, 则按 {当前规格折扣情况, 历史折扣状况} 排序
        return Integer.compare(price.getDiscount(), price1.getDiscount());
    }
}

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
import com.aliyun.ecs.easysdk.preemptiveinstance.utils.PreemptiveInstanceUtils;

import java.io.Serializable;
import java.util.Comparator;

public class ProductGenerationComparator implements Comparator<DiscountInventoryModel> , Serializable {
    private static final long serialVersionUID = -726804793427741130L;

    @Override
    public int compare(DiscountInventoryModel a, DiscountInventoryModel b) {
        String generationA = a.getInstanceGeneration();
        String generationB = b.getInstanceGeneration();
        // 1. 按照ECS迭代降序
        int i = PreemptiveInstanceUtils.compareEcsGeneration(generationA, generationB);
        if (i != 0) {
            return i > 0 ? -1 : 1;
        }
        // 2. 如果ECS相同代数, 则按照库存排序
        int compareOutput = StrategyUtils.progressiveDiscountComparison(a,b);
        if (compareOutput != 0) {
            return compareOutput;
        }
        // 3. 大规格折扣近似, 则看当前规格折扣
        SpotPrice price = a.getPrice();
        SpotPrice price1 = b.getPrice();
        if (!price.getDiscount().equals(price1.getDiscount())) {
            return price.getDiscount() > price1.getDiscount() ? 1 : -1;
        }
        return 0;
    }
}

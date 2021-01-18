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

import static com.aliyun.ecs.easysdk.preemptiveinstance.constants.EcsInstanceConstants.*;

public class PriceComparator implements Comparator<DiscountInventoryModel> , Serializable {
    private static final int PERCENTAGE_RATIO = 100;
    private static final long serialVersionUID = 2726806906002233942L;

    @Override
    public int compare(DiscountInventoryModel a, DiscountInventoryModel b) {
        SpotPrice price = a.getPrice();
        SpotPrice price1 = b.getPrice();
        // 同代之间: 价格相差在10%以内, 区分度不明显, 可以认为是相同价格
        // 跨代之间: 价格相差在20%以内, 区分度不明显, 可以认为是相同价格
        Float spotPrice = price.getSpotPrice();
        Float spotPrice1 = price1.getSpotPrice();
        String generationA = a.getInstanceGeneration();
        String generationB = b.getInstanceGeneration();
        int generationIndex = PreemptiveInstanceUtils.compareEcsGeneration(generationA, generationB);
        Float priceDifferenceRatio = calPriceDifferenceRatio(spotPrice, spotPrice1);
        if (generationIndex == 0 && priceDifferenceRatio >= PRICE_DIFF_IN_SAME_GENERATION) {
            return Float.compare(spotPrice, spotPrice1);
        }
        if (generationIndex != 0 && priceDifferenceRatio >= PRICE_DIFF_IN_DIFFERENT_GENERATION) {
            return Float.compare(spotPrice, spotPrice1);
        }
        // 价格上没有区分度的两个推荐结果再进行渐进式折扣比较
        int compareOutput = StrategyUtils.progressiveDiscountComparison(a, b);
        if (compareOutput != 0) {
            return compareOutput;
        }
        return Float.compare(spotPrice, spotPrice1);
    }

    private Float calPriceDifferenceRatio(Float spotPrice, Float spotPrice1) {
        Float largerPrice = Math.max(spotPrice, spotPrice1);
        Float smallerPrice = Math.min(spotPrice, spotPrice1);
        Float differenceValue = largerPrice - smallerPrice;
        return PERCENTAGE_RATIO * (differenceValue / smallerPrice);
    }
}

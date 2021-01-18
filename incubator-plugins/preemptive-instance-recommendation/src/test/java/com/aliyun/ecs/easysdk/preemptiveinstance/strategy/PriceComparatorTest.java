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

import com.aliyun.ecs.easysdk.preemptiveinstance.constants.EcsInstanceConstants;
import com.aliyun.ecs.easysdk.preemptiveinstance.model.DiscountInventoryModel;
import com.aliyun.ecs.easysdk.preemptiveinstance.model.EcsInstanceType;
import com.aliyun.ecs.easysdk.preemptiveinstance.model.SpotPrice;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class PriceComparatorTest {
    @Test
    public void testPriceComparator() {
        PriceComparator priceComparator = new PriceComparator();
        DiscountInventoryModel a;
        DiscountInventoryModel b;
        SpotPrice price;
        SpotPrice price1;

        // Case 1:同代，价格相差10%以上
        float basePrice = 1.0f;
        float difference = 1.0f;
        a = new DiscountInventoryModel();
        b = new DiscountInventoryModel();
        a.setInstanceGeneration("ecs-1");
        b.setInstanceGeneration("ecs-1");
        price = new SpotPrice();
        price1 = new SpotPrice();
        price.setSpotPrice(basePrice);
        price1.setSpotPrice(basePrice * (1f + (EcsInstanceConstants.PRICE_DIFF_IN_SAME_GENERATION + difference) / 100));
        a.setPrice(price);
        b.setPrice(price1);
        Assert.assertEquals(-1, priceComparator.compare(a, b));

        // Case 2:不同代，价格相差20%以上
        a = new DiscountInventoryModel();
        b = new DiscountInventoryModel();
        a.setInstanceGeneration("ecs-1");
        b.setInstanceGeneration("ecs-2");
        price = new SpotPrice();
        price1 = new SpotPrice();
        price.setSpotPrice(basePrice);
        price1.setSpotPrice(basePrice * (1f + (EcsInstanceConstants.PRICE_DIFF_IN_DIFFERENT_GENERATION + difference) / 100));
        a.setPrice(price);
        b.setPrice(price1);
        Assert.assertEquals(-1, priceComparator.compare(a, b));

        // Case 3:不同代，价格相差20%以内，渐进式比较结果为二者相同
        a = new DiscountInventoryModel();
        b = new DiscountInventoryModel();
        price = new SpotPrice();
        price1 = new SpotPrice();
        price.setSpotPrice(basePrice);
        price1.setSpotPrice(basePrice * (1f + (EcsInstanceConstants.PRICE_DIFF_IN_DIFFERENT_GENERATION - difference)/100));
        a.setPrice(price);
        b.setPrice(price1);
        a.setInstanceGeneration("ecs-1");
        b.setInstanceGeneration("ecs-2");
        a.setInstanceType("InstanceTypeA");
        b.setInstanceType("InstanceTypeB");
        Map<String, Integer> discountMap = Maps.newHashMap();
        Map<String, Integer> discountMap1 = Maps.newHashMap();
        discountMap.put("InstanceTypeA", 10);
        discountMap1.put("InstanceTypeB", 10);
        a.setInstanceTypesToSpotDiscountMap(discountMap);
        b.setInstanceTypesToSpotDiscountMap(discountMap1);
        a.setInstanceFamilyLevel("EntryLevel");
        b.setInstanceFamilyLevel("EntryLevel");
        List<EcsInstanceType> instanceTypes = Lists.newArrayList();
        List<EcsInstanceType> instanceTypes1 = Lists.newArrayList();
        EcsInstanceType ecsInstanceType = new EcsInstanceType();
        EcsInstanceType ecsInstanceType1 = new EcsInstanceType();
        ecsInstanceType.setInstanceTypeId("InstanceTypeA");
        ecsInstanceType.setCpuCoreCount(2);
        ecsInstanceType1.setInstanceTypeId("InstanceTypeB");
        ecsInstanceType1.setCpuCoreCount(2);
        instanceTypes.add(ecsInstanceType);
        instanceTypes1.add(ecsInstanceType1);
        a.setInstanceTypesInFamily(instanceTypes);
        b.setInstanceTypesInFamily(instanceTypes1);
        Assert.assertEquals(-1, priceComparator.compare(a, b));
    }
}

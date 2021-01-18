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
import com.aliyun.ecs.easysdk.preemptiveinstance.model.EcsInstanceType;
import com.aliyun.ecs.easysdk.preemptiveinstance.model.SpotPrice;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class ProductGenerationComparatorTest {
    @Test
    public void testProductGenerationComparatorTest(){
        ProductGenerationComparator productGenerationComparator = new ProductGenerationComparator();
        DiscountInventoryModel a;
        DiscountInventoryModel b;
        // Case 1: Ecs产品代数不同
        a = new DiscountInventoryModel();
        b = new DiscountInventoryModel();
        a.setInstanceGeneration("ecs-1");
        b.setInstanceGeneration("ecs-2");
        Assert.assertEquals(1,productGenerationComparator.compare(a,b));
        // Case 2: 相同产品代数，库存状态相同，当前折扣不同
        a = new DiscountInventoryModel();
        b = new DiscountInventoryModel();
        a.setInstanceGeneration("ecs-1");
        b.setInstanceGeneration("ecs-1");
        SpotPrice price;
        SpotPrice price1;
        price = new SpotPrice();
        price1 = new SpotPrice();
        price.setSpotPrice(1.0f);
        price1.setSpotPrice(1.17f);
        price.setDiscount(20);
        price1.setDiscount(10);
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
        Assert.assertEquals(1,productGenerationComparator.compare(a,b));
    }
}

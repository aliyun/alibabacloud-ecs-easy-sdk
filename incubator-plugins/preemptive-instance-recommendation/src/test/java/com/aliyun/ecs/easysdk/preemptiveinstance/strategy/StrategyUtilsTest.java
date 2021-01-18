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
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class StrategyUtilsTest {
    @Test
    public void progressiveDiscountComparisonTest() {
        DiscountInventoryModel a = new DiscountInventoryModel();
        DiscountInventoryModel b = new DiscountInventoryModel();

        // Case 1: 同代，折扣差超过5
        a.setInstanceType("A");
        b.setInstanceType("B");
        a.setInstanceGeneration("ecs-1");
        b.setInstanceGeneration("ecs-1");
        Map<String, Integer> discountMap = Maps.newHashMap();
        Map<String, Integer> discountMap1 = Maps.newHashMap();
        int baseDiscount = 15;
        int difference = 1;
        discountMap.put("A",baseDiscount);
        discountMap1.put("B",baseDiscount + EcsInstanceConstants.DISCOUNT_DIFF_IN_SAME_GENERATION + difference);
        a.setInstanceTypesToSpotDiscountMap(discountMap);
        b.setInstanceTypesToSpotDiscountMap(discountMap1);
        Assert.assertEquals(-1,StrategyUtils.progressiveDiscountComparison(a,b));

        // Case 2: 入门级非同代产品，折扣差大于8
        a = new DiscountInventoryModel();
        b = new DiscountInventoryModel();
        a.setInstanceType("A");
        b.setInstanceType("B");
        a.setInstanceFamilyLevel("EntryLevel");
        b.setInstanceFamilyLevel("EntryLevel");
        a.setInstanceGeneration("ecs-1");
        b.setInstanceGeneration("ecs-2");
        discountMap = Maps.newHashMap();
        discountMap1 = Maps.newHashMap();
        discountMap.put("A",baseDiscount);
        discountMap1.put("B",baseDiscount + EcsInstanceConstants.DISCOUNT_DIFF_IN_DIFFERENT_GENERATION_ENTRY_LEVEL + difference);
        a.setInstanceTypesToSpotDiscountMap(discountMap);
        b.setInstanceTypesToSpotDiscountMap(discountMap1);
        Assert.assertEquals(-1,StrategyUtils.progressiveDiscountComparison(a,b));

        // Case 3: 非入门级非同代产品，折扣差大于15
        a = new DiscountInventoryModel();
        b = new DiscountInventoryModel();
        a.setInstanceType("A");
        b.setInstanceType("B");
        a.setInstanceFamilyLevel("EnterpriseLevel");
        b.setInstanceFamilyLevel("EnterpriseLevel");
        a.setInstanceGeneration("ecs-1");
        b.setInstanceGeneration("ecs-2");
        discountMap = Maps.newHashMap();
        discountMap1 = Maps.newHashMap();
        discountMap.put("A",baseDiscount);
        discountMap1.put("B",baseDiscount + EcsInstanceConstants.DISCOUNT_DIFF_IN_DIFFERENT_GENERATION_OTHER_CATEGORY + difference);
        a.setInstanceTypesToSpotDiscountMap(discountMap);
        b.setInstanceTypesToSpotDiscountMap(discountMap1);
        Assert.assertEquals(-1,StrategyUtils.progressiveDiscountComparison(a,b));

        // Case 4: 同代，折扣差小于5，进行渐进式比较
        a = new DiscountInventoryModel();
        b = new DiscountInventoryModel();
        a.setInstanceType("A");
        b.setInstanceType("B");
        a.setInstanceGeneration("ecs-1");
        b.setInstanceGeneration("ecs-1");
        discountMap = Maps.newHashMap();
        discountMap1 = Maps.newHashMap();
        discountMap.put("A",baseDiscount);
        discountMap1.put("B",baseDiscount);
        discountMap.put("C",baseDiscount);
        discountMap1.put("D",baseDiscount + EcsInstanceConstants.DISCOUNT_DIFF_IN_SAME_GENERATION + difference);
        discountMap.put("G",baseDiscount);
        discountMap1.put("F",baseDiscount);
        a.setInstanceTypesToSpotDiscountMap(discountMap);
        b.setInstanceTypesToSpotDiscountMap(discountMap1);
        List<EcsInstanceType> instanceTypes = generateInstanceTypes(Lists.newArrayList(1,2,2,4,8),Lists.newArrayList("A","C","E","G","I"));
        List<EcsInstanceType> instanceTypes1 = generateInstanceTypes(Lists.newArrayList(1,2,4,4,4,4),Lists.newArrayList("B","D","F","H","J","K"));
        a.setInstanceTypesInFamily(instanceTypes);
        b.setInstanceTypesInFamily(instanceTypes1);
        Assert.assertEquals(-1,StrategyUtils.progressiveDiscountComparison(a,b));
    }

    @Test
    public void testFilterUnrelatedInstanceTypes() {
        List<EcsInstanceType> instanceTypes = Lists.newArrayList();
        List<EcsInstanceType> filteredInstanceTypes = StrategyUtils.filterUnrelatedInstanceTypes(instanceTypes);
        Assert.assertEquals(0, filteredInstanceTypes.size());

        instanceTypes = Lists.newArrayList();
        instanceTypes.addAll(generateInstanceTypes(Lists.newArrayList(1, 2, 2, 2, 4, 4, 8), Lists.newArrayList("A", "B", "BNE", "C", "DN", "D", "E")));
        filteredInstanceTypes = StrategyUtils.filterUnrelatedInstanceTypes(instanceTypes);
        Assert.assertEquals(4, filteredInstanceTypes.size());
        Assert.assertEquals("A", filteredInstanceTypes.get(0).getInstanceTypeId());
        Assert.assertEquals("B", filteredInstanceTypes.get(1).getInstanceTypeId());
        Assert.assertEquals("D", filteredInstanceTypes.get(2).getInstanceTypeId());
        Assert.assertEquals("E", filteredInstanceTypes.get(3).getInstanceTypeId());

        instanceTypes = Lists.newArrayList();
        instanceTypes.addAll(generateInstanceTypes(Lists.newArrayList(1, 2, 4, 8), Lists.newArrayList("A", "AB", "ABC", "D")));
        filteredInstanceTypes = StrategyUtils.filterUnrelatedInstanceTypes(instanceTypes);
        Assert.assertEquals(4, filteredInstanceTypes.size());
        Assert.assertEquals("A", filteredInstanceTypes.get(0).getInstanceTypeId());
        Assert.assertEquals("AB", filteredInstanceTypes.get(1).getInstanceTypeId());
        Assert.assertEquals("ABC", filteredInstanceTypes.get(2).getInstanceTypeId());
        Assert.assertEquals("D", filteredInstanceTypes.get(3).getInstanceTypeId());
    }

    @Test
    public void testExtractSameCoreInstanceTypes() {
        List<EcsInstanceType> instanceTypes = Lists.newArrayList();
        List<EcsInstanceType> instanceTypes1 = Lists.newArrayList();
        List<List<EcsInstanceType>> sameCoreInstanceTypesList = StrategyUtils.extractSameCoreInstanceTypes(instanceTypes, instanceTypes1);
        Assert.assertEquals(0, sameCoreInstanceTypesList.get(0).size());
        Assert.assertEquals(0, sameCoreInstanceTypesList.get(1).size());

        instanceTypes = Lists.newArrayList(generateInstanceTypes(Lists.newArrayList(1, 2, 4), Lists.newArrayList("A", "B", "C")));
        instanceTypes1 = Lists.newArrayList(generateInstanceTypes(Lists.newArrayList(1, 2, 4, 8), Lists.newArrayList("D", "E", "F", "G")));
        sameCoreInstanceTypesList = StrategyUtils.extractSameCoreInstanceTypes(instanceTypes, instanceTypes1);
        Assert.assertEquals(3, sameCoreInstanceTypesList.get(0).size());
        Assert.assertEquals(3, sameCoreInstanceTypesList.get(1).size());
        Assert.assertEquals("D",sameCoreInstanceTypesList.get(1).get(0).getInstanceTypeId());
        Assert.assertEquals("E",sameCoreInstanceTypesList.get(1).get(1).getInstanceTypeId());
        Assert.assertEquals("F",sameCoreInstanceTypesList.get(1).get(2).getInstanceTypeId());

        instanceTypes = Lists.newArrayList(generateInstanceTypes(Lists.newArrayList(1, 2, 4, 8), Lists.newArrayList("D", "E", "F", "G")));
        instanceTypes1 = Lists.newArrayList(generateInstanceTypes(Lists.newArrayList(1, 2, 4), Lists.newArrayList("A", "B", "C")));
        sameCoreInstanceTypesList = StrategyUtils.extractSameCoreInstanceTypes(instanceTypes, instanceTypes1);
        Assert.assertEquals(3, sameCoreInstanceTypesList.get(0).size());
        Assert.assertEquals(3, sameCoreInstanceTypesList.get(1).size());
        Assert.assertEquals("A",sameCoreInstanceTypesList.get(1).get(0).getInstanceTypeId());
        Assert.assertEquals("B",sameCoreInstanceTypesList.get(1).get(1).getInstanceTypeId());
        Assert.assertEquals("C",sameCoreInstanceTypesList.get(1).get(2).getInstanceTypeId());
    }

    private List<EcsInstanceType> generateInstanceTypes(List<Integer> coreList, List<String> instanceTypeIdList) {
        List<EcsInstanceType> instanceTypes = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(coreList)) {
            for (int i = 0; i < coreList.size(); i++) {
                EcsInstanceType ecsInstanceType = new EcsInstanceType();
                ecsInstanceType.setCpuCoreCount(coreList.get(i));
                ecsInstanceType.setInstanceTypeId(instanceTypeIdList.get(i));
                instanceTypes.add(ecsInstanceType);
            }
        }
        return instanceTypes;
    }
}

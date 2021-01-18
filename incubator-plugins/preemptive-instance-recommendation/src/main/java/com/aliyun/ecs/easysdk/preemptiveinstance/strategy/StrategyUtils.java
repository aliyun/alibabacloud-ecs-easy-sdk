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

import com.aliyun.ecs.easysdk.biz.constants.EnumEcsProductCategory;
import com.aliyun.ecs.easysdk.preemptiveinstance.model.DiscountInventoryModel;
import com.aliyun.ecs.easysdk.preemptiveinstance.model.EcsInstanceType;
import com.aliyun.ecs.easysdk.preemptiveinstance.utils.PreemptiveInstanceUtils;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

import static com.aliyun.ecs.easysdk.preemptiveinstance.constants.EcsInstanceConstants.*;

public class StrategyUtils {
    public static int progressiveDiscountComparison(DiscountInventoryModel a, DiscountInventoryModel b) {
        List<EcsInstanceType> instanceTypes = a.getInstanceTypesInFamily();
        List<EcsInstanceType> instanceTypes1 = b.getInstanceTypesInFamily();
        Map<String, Integer> discountMap = a.getInstanceTypesToSpotDiscountMap();
        Map<String, Integer> discountMap1 = b.getInstanceTypesToSpotDiscountMap();
        String generationA = a.getInstanceGeneration();
        String generationB = b.getInstanceGeneration();
        int generationIndex = PreemptiveInstanceUtils.compareEcsGeneration(generationA, generationB);
        // 先比较用户所需求的这个规格的折扣
        int instanceTypeDiscount = discountMap.get(a.getInstanceType());
        int instanceTypeDiscount1 = discountMap1.get(b.getInstanceType());
        if (generationIndex == 0 && Math.abs(instanceTypeDiscount - instanceTypeDiscount1) >= DISCOUNT_DIFF_IN_SAME_GENERATION) {
            return instanceTypeDiscount > instanceTypeDiscount1 ? 1 : -1;
        }
        int comparisonIndex;
        // 入门级非同代产品折扣差距8%以上认为价格不同,非入门级非同代产品折扣差距15%以上认为价格不同
        if (EnumEcsProductCategory.EntryLevel.name().equals(a.getInstanceFamilyLevel())) {
            comparisonIndex = DISCOUNT_DIFF_IN_DIFFERENT_GENERATION_ENTRY_LEVEL;
        } else {
            comparisonIndex = DISCOUNT_DIFF_IN_DIFFERENT_GENERATION_OTHER_CATEGORY;
        }
        if (generationIndex != 0 && Math.abs(instanceTypeDiscount - instanceTypeDiscount1) >= comparisonIndex) {
            return instanceTypeDiscount > instanceTypeDiscount1 ? 1 : -1;
        }
        List<EcsInstanceType> filteredInstanceTypes = filterUnrelatedInstanceTypes(instanceTypes);
        List<EcsInstanceType> filteredInstanceTypes1 = filterUnrelatedInstanceTypes(instanceTypes1);
        List<List<EcsInstanceType>> sameCoreInstanceTypesList = extractSameCoreInstanceTypes(filteredInstanceTypes,filteredInstanceTypes1);
        List<EcsInstanceType> sameCoreInstanceTypes = sameCoreInstanceTypesList.get(0);
        List<EcsInstanceType> sameCoreInstanceTypes1 = sameCoreInstanceTypesList.get(1);

        for (int j = 0; j < sameCoreInstanceTypes.size(); j++) {
            // 获取分别的Discount
            Integer discount = discountMap.get(sameCoreInstanceTypes.get(j).getInstanceTypeId());
            Integer discount1 = discountMap1.get(sameCoreInstanceTypes1.get(j).getInstanceTypeId());
            if (discount == null || discount1 == null) {
                continue;
            }
            // 同代产品间折扣差距5%以上认为价格不同
            if (generationIndex == 0 && Math.abs(discount - discount1) >= DISCOUNT_DIFF_IN_SAME_GENERATION) {
                return discount > discount1 ? 1 : -1;
            }
            if (generationIndex != 0 && Math.abs(discount - discount1) >= comparisonIndex) {
                return discount > discount1 ? 1 : -1;
            }
        }
        return 0;
    }

    /**
     * 抽取出给定的两个实例规格列表中具有和另一个实例规格列表某规格具有相同核数的规格列表，如输入的两个规格列表的核数情况为{1,2,4,8}和{1,4,8},则输出为{1,4,8}{1,4,8}
     * @param filteredInstanceTypes 给定实例规格列表,各个规格的核数均不相同
     * @param filteredInstanceTypes1 给定的第二个实例规格列表,各个规格的核数均不相同
     * @return 过滤后的两个实例规格列表，index分别为0和1
     */
    public static List<List<EcsInstanceType>> extractSameCoreInstanceTypes(List<EcsInstanceType> filteredInstanceTypes, List<EcsInstanceType> filteredInstanceTypes1) {
        List<EcsInstanceType> sameCoreInstanceTypes = Lists.newArrayList();
        List<EcsInstanceType> sameCoreInstanceTypes1 = Lists.newArrayList();
        for (EcsInstanceType ecsInstanceType : filteredInstanceTypes) {
            for (EcsInstanceType ecsInstanceType1 : filteredInstanceTypes1) {
                if (ecsInstanceType.getCpuCoreCount() < ecsInstanceType1.getCpuCoreCount()) {
                    break;
                } else if (ecsInstanceType.getCpuCoreCount().equals(ecsInstanceType1.getCpuCoreCount())) {
                    sameCoreInstanceTypes.add(ecsInstanceType);
                    sameCoreInstanceTypes1.add(ecsInstanceType1);
                }
            }
        }
        List<List<EcsInstanceType>> sameCoreInstanceTypesList = Lists.newArrayList();
        sameCoreInstanceTypesList.add(sameCoreInstanceTypes);
        sameCoreInstanceTypesList.add(sameCoreInstanceTypes1);
        return sameCoreInstanceTypesList;
    }

    /**
     * 过滤掉实例规格列表中,具有相同核数的规格中instanceTypeId较长的规格，且保证过滤后的实例规格列表中每一个规格的核数均不相同
     * @param instanceTypes 待过滤的实例规格,需要是按照核数从小到大有序排列的有序列表
     * @return 输出的实例规格列表，其中的每一个实例规格的核数均不相同，且均为同核数同规格族下的实例规格中，instanceTypeId最短的
     */
    public static List<EcsInstanceType> filterUnrelatedInstanceTypes(List<EcsInstanceType> instanceTypes) {
        List<EcsInstanceType> filteredInstanceTypes = Lists.newArrayList();
        EcsInstanceType candidateInstanceType;
        for (int i = 0; i < instanceTypes.size(); i++) {
            EcsInstanceType instanceType = instanceTypes.get(i);
            candidateInstanceType = instanceType;
            int coreCount = instanceType.getCpuCoreCount();
            while (i + 1 < instanceTypes.size() && instanceTypes.get(i + 1).getCpuCoreCount() == coreCount) {
                if (instanceTypes.get(i + 1).getInstanceTypeId().length() < instanceType.getInstanceTypeId().length()) {
                    candidateInstanceType = instanceTypes.get(i + 1);
                }
                i++;
            }
            filteredInstanceTypes.add(candidateInstanceType);
        }
        return filteredInstanceTypes;
    }
}

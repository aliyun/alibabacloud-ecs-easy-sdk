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
package com.aliyun.ecs.easysdk.preemptiveinstance.model;

import java.util.List;
import java.util.Map;

import com.aliyun.ecs.easysdk.biz.constants.EcsInventoryStatusCategory;
import com.aliyun.ecs.easysdk.biz.constants.EnumEcsProductCategory;

public class DiscountInventoryModel {
    String region;
    String zone;
    /**
     * {@linkplain EnumEcsProductCategory}
     */
    String instanceFamilyLevel;
    String instanceGeneration;
    String instanceFamily;
    String instanceType;
    String status;
    EcsInventoryStatusCategory statusCategory;
    SpotPrice price;

    /**
     * 同规格族下的规格与对应的SPOT折扣对应表
     */
    Map<String, Integer> instanceTypesToSpotDiscountMap;

    /**
     * 与此规格处于同一规格族下的所有规格（包括本身）
     */
    List<EcsInstanceType> instanceTypesInFamily;

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public EcsInventoryStatusCategory getStatusCategory() {
        return statusCategory;
    }

    public void setStatusCategory(EcsInventoryStatusCategory statusCategory) {
        this.statusCategory = statusCategory;
    }

    public String getInstanceType() {
        return instanceType;
    }

    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

    public SpotPrice getPrice() {
        return price;
    }

    public void setPrice(SpotPrice price) {
        this.price = price;
    }

    public String getInstanceFamily() {
        return instanceFamily;
    }

    public void setInstanceFamily(String instanceFamily) {
        this.instanceFamily = instanceFamily;
    }

    public String getInstanceFamilyLevel() {
        return instanceFamilyLevel;
    }

    public void setInstanceFamilyLevel(String instanceFamilyLevel) {
        this.instanceFamilyLevel = instanceFamilyLevel;
    }

    public String getInstanceGeneration() {
        return instanceGeneration;
    }

    public void setInstanceGeneration(String instanceGeneration) {
        this.instanceGeneration = instanceGeneration;
    }

    public Map<String, Integer> getInstanceTypesToSpotDiscountMap() {
        return instanceTypesToSpotDiscountMap;
    }

    public void setInstanceTypesToSpotDiscountMap(Map<String, Integer> instanceTypesToSpotDiscountMap) {
        this.instanceTypesToSpotDiscountMap = instanceTypesToSpotDiscountMap;
    }

    public List<EcsInstanceType> getInstanceTypesInFamily() {
        return instanceTypesInFamily;
    }

    public void setInstanceTypesInFamily(List<EcsInstanceType> instanceTypesInFamily) {
        this.instanceTypesInFamily = instanceTypesInFamily;
    }
}

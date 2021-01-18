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

import com.aliyun.ecs.easysdk.biz.constants.EnumEcsProductCategory;
import com.aliyun.ecs.easysdk.biz.constants.EcsInventoryStatusCategory;

public class PreemptiveInstanceRecommendation {

    private String zone;
    /**
     * {@linkplain EnumEcsProductCategory}
     */
    private String instanceFamilyLevel;
    private String instanceGeneration;
    private String instanceTypeFamily;
    private String instanceType;

    private EcsInventoryStatusCategory statusCategory;

    private Float spotPrice;
    private Float originPrice;
    private Integer discount;

    private String largeFlavorInstanceType;
    private Integer largeFlavorDiscount;

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getInstanceType() {
        return instanceType;
    }

    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

    public Float getSpotPrice() {
        return spotPrice;
    }

    public void setSpotPrice(Float spotPrice) {
        this.spotPrice = spotPrice;
    }

    public Float getOriginPrice() {
        return originPrice;
    }

    public void setOriginPrice(Float originPrice) {
        this.originPrice = originPrice;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public String getInstanceTypeFamily() {
        return instanceTypeFamily;
    }

    public void setInstanceTypeFamily(String instanceTypeFamily) {
        this.instanceTypeFamily = instanceTypeFamily;
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

    public String getLargeFlavorInstanceType() {
        return largeFlavorInstanceType;
    }

    public void setLargeFlavorInstanceType(String largeFlavorInstanceType) {
        this.largeFlavorInstanceType = largeFlavorInstanceType;
    }

    public Integer getLargeFlavorDiscount() {
        return largeFlavorDiscount;
    }

    public void setLargeFlavorDiscount(Integer largeFlavorDiscount) {
        this.largeFlavorDiscount = largeFlavorDiscount;
    }

    public EcsInventoryStatusCategory getStatusCategory() {
        return statusCategory;
    }

    public void setStatusCategory(EcsInventoryStatusCategory statusCategory) {
        this.statusCategory = statusCategory;
    }
}

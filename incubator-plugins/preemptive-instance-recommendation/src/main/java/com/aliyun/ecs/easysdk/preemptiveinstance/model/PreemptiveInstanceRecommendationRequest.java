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

import com.aliyun.ecs.easysdk.biz.constants.EnumEcsProductCategory;

public class PreemptiveInstanceRecommendationRequest {

    private List<String> regions;

    private List<String> zones;

    private EnumEcsProductCategory productCategory;

    private EnumRecommendationStrategy strategy = EnumRecommendationStrategy.SUFFICIENT_INVENTORY_FIRST;

    private int cores;

    private int memory;

    private String instanceType;

    private int limit = 5;

    public EnumEcsProductCategory getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(EnumEcsProductCategory productCategory) {
        this.productCategory = productCategory;
    }

    public EnumRecommendationStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(EnumRecommendationStrategy strategy) {
        this.strategy = strategy;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public List<String> getZones() {
        return zones;
    }

    public void setZones(List<String> zones) {
        this.zones = zones;
    }

    public int getCores() {
        return cores;
    }

    public void setCores(int cores) {
        this.cores = cores;
    }

    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    public List<String> getRegions() {
        return regions;
    }

    public void setRegions(List<String> regions) {
        this.regions = regions;
    }

    public String getInstanceType() {
        return instanceType;
    }

    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

    public PreemptiveInstanceRecommendationRequest copy() {
        PreemptiveInstanceRecommendationRequest preemptiveInstanceRecommendationRequest = new PreemptiveInstanceRecommendationRequest();
        preemptiveInstanceRecommendationRequest.setZones(this.getZones());
        preemptiveInstanceRecommendationRequest.setRegions(this.getRegions());
        preemptiveInstanceRecommendationRequest.setProductCategory(this.getProductCategory());
        preemptiveInstanceRecommendationRequest.setStrategy(this.getStrategy());
        preemptiveInstanceRecommendationRequest.setMemory(this.getMemory());
        preemptiveInstanceRecommendationRequest.setCores(this.getCores());
        preemptiveInstanceRecommendationRequest.setInstanceType(this.getInstanceType());
        preemptiveInstanceRecommendationRequest.setLimit(this.getLimit());
        return preemptiveInstanceRecommendationRequest;
    }
}

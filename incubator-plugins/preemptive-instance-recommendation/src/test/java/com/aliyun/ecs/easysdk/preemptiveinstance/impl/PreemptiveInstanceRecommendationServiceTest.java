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
package com.aliyun.ecs.easysdk.preemptiveinstance.impl;

import java.util.List;

import com.alibaba.fastjson.JSON;

import com.aliyun.ecs.easysdk.EasyEcsSDK;
import com.aliyun.ecs.easysdk.biz.constants.EnumEcsProductCategory;
import com.aliyun.ecs.easysdk.biz.model.Response;
import com.aliyun.ecs.easysdk.preemptiveinstance.PreemptiveInstanceRecommendationService;
import com.aliyun.ecs.easysdk.preemptiveinstance.model.EnumRecommendationStrategy;
import com.aliyun.ecs.easysdk.preemptiveinstance.model.PreemptiveInstanceRecommendation;
import com.aliyun.ecs.easysdk.preemptiveinstance.model.PreemptiveInstanceRecommendationRequest;
import com.aliyun.ecs.easysdk.system.config.ConfigKeys;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class PreemptiveInstanceRecommendationServiceTest {

    static PreemptiveInstanceRecommendationServiceImpl preemptiveInstanceRecommendationService;

    @BeforeClass
    public static void setUp() {
        EasyEcsSDK.setProperty(ConfigKeys.ALIYUN_EASY_ECS_SDK_ACCESS_KEY_ID, "<your-access-key>");
        EasyEcsSDK.setProperty(ConfigKeys.ALIYUN_EASY_ECS_SDK_ACCESS_SECRET, "<your-access-secret>");
        EasyEcsSDK.init();
        preemptiveInstanceRecommendationService
                = (PreemptiveInstanceRecommendationServiceImpl) EasyEcsSDK.getService(
                PreemptiveInstanceRecommendationService.class);
    }

    @Test
    public void limitRecommendCountTest(){
        List<PreemptiveInstanceRecommendation> recommendations = Lists.newArrayList();
        int limit = 3;
        Response<List<PreemptiveInstanceRecommendation>> response = new Response<>(recommendations);
        Assert.assertEquals(recommendations.size(),preemptiveInstanceRecommendationService.limitRecommendCount(limit,response).getData().size());
        recommendations.add(new PreemptiveInstanceRecommendation());
        recommendations.add(new PreemptiveInstanceRecommendation());
        recommendations.add(new PreemptiveInstanceRecommendation());
        response = new Response<>(recommendations);
        limit = 5;
        Assert.assertEquals(recommendations.size(),preemptiveInstanceRecommendationService.limitRecommendCount(limit,response).getData().size());
        limit = -3;
        Assert.assertEquals(recommendations.size(),preemptiveInstanceRecommendationService.limitRecommendCount(limit,response).getData().size());
        limit = 1;
        Assert.assertEquals(limit,preemptiveInstanceRecommendationService.limitRecommendCount(limit,response).getData().size());
    }

    @Test
    public void entryLevelSingleRegionMultiZoneTest() {
        // <独享, 单地域, 多可用区, 定规格, 小规格>
        PreemptiveInstanceRecommendationRequest request = new PreemptiveInstanceRecommendationRequest();
        request.setRegions(Lists.newArrayList("cn-hangzhou"));
        request.setZones(Lists.newArrayList("cn-hangzhou-g", "cn-hangzhou-h", "cn-hangzhou-i"));
        request.setCores(4);
        request.setMemory(8);
        request.setInstanceType("ecs.c5.xlarge");
        request.setStrategy(EnumRecommendationStrategy.LOWEST_PRICE_FIRST);
        request.setProductCategory(EnumEcsProductCategory.EnterpriseLevel);
        Response<List<PreemptiveInstanceRecommendation>> recommend = preemptiveInstanceRecommendationService.recommend(
                request);
        System.out.println(JSON.toJSONString(recommend.getData()));
    }

    @Test
    public void entryLevelSingleRegionSingleZoneTest() {
        // <独享, 单地域, 单可用区, 不定规格, 小规格>
        PreemptiveInstanceRecommendationRequest request = new PreemptiveInstanceRecommendationRequest();
        request.setRegions(Lists.newArrayList("cn-hangzhou"));
        request.setZones(Lists.newArrayList("cn-hangzhou-g"));
        request.setCores(4);
        request.setMemory(8);
        request.setStrategy(EnumRecommendationStrategy.LOWEST_PRICE_FIRST);
        request.setProductCategory(EnumEcsProductCategory.EnterpriseLevel);
        Response<List<PreemptiveInstanceRecommendation>> recommend = preemptiveInstanceRecommendationService.recommend(
                request);
        System.out.println(JSON.toJSONString(recommend.getData()));
    }

    @Test
    public void enterpriseLevelSingleRegionSingleZoneTest() {
        // <独享, 单地域, 单可用区, 不定规格, 小规格>
        PreemptiveInstanceRecommendationRequest request = new PreemptiveInstanceRecommendationRequest();
        request.setRegions(Lists.newArrayList("cn-hangzhou"));
        request.setZones(Lists.newArrayList("cn-hangzhou-b"));
        request.setCores(4);
        request.setMemory(8);
        request.setStrategy(EnumRecommendationStrategy.LOWEST_PRICE_FIRST);
        request.setProductCategory(EnumEcsProductCategory.EnterpriseLevel);
        Response<List<PreemptiveInstanceRecommendation>> recommend = preemptiveInstanceRecommendationService.recommend(
                request);
        System.out.println(JSON.toJSONString(recommend.getData()));
    }

    @Test
    public void enterpriseLevelSingleRegionMultiZoneTest() {
        // <独享, 单地域, 多可用区, 不定规格, 小规格>
        PreemptiveInstanceRecommendationRequest request = new PreemptiveInstanceRecommendationRequest();
        request.setRegions(Lists.newArrayList("cn-hangzhou"));
        request.setZones(Lists.newArrayList("cn-hangzhou-b", "cn-hangzhou-e", "cn-hangzhou-f", "cn-hangzhou-g"));
        request.setCores(4);
        request.setMemory(8);
        request.setStrategy(EnumRecommendationStrategy.LOWEST_PRICE_FIRST);
        request.setProductCategory(EnumEcsProductCategory.EnterpriseLevel);
        Response<List<PreemptiveInstanceRecommendation>> recommend = preemptiveInstanceRecommendationService.recommend(
                request);
        System.out.println(JSON.toJSONString(recommend.getData()));

        // <独享, 单地域, 多可用区, 不定规格, 小规格>
        request = new PreemptiveInstanceRecommendationRequest();
        request.setRegions(Lists.newArrayList("cn-hangzhou"));
        request.setZones(Lists.newArrayList("cn-hangzhou-g"));
        request.setCores(4);
        request.setMemory(8);
        request.setStrategy(EnumRecommendationStrategy.LOWEST_PRICE_FIRST);
        request.setProductCategory(EnumEcsProductCategory.EnterpriseLevel);
        recommend = preemptiveInstanceRecommendationService.recommend(
                request);
        System.out.println(JSON.toJSONString(recommend.getData()));
    }
}

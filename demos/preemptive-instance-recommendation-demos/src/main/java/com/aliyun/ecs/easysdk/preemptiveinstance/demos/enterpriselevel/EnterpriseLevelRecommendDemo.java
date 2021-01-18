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
package com.aliyun.ecs.easysdk.preemptiveinstance.demos.enterpriselevel;

import com.alibaba.fastjson.JSON;
import com.aliyun.ecs.easysdk.EasyEcsSDK;
import com.aliyun.ecs.easysdk.biz.constants.EnumEcsProductCategory;
import com.aliyun.ecs.easysdk.biz.model.Response;
import com.aliyun.ecs.easysdk.preemptiveinstance.PreemptiveInstanceBaseService;
import com.aliyun.ecs.easysdk.preemptiveinstance.demos.constants.EcsDemoConstants;
import com.aliyun.ecs.easysdk.preemptiveinstance.demos.utils.RecommendationUtil;
import com.aliyun.ecs.easysdk.preemptiveinstance.PreemptiveInstanceRecommendationService;
import com.aliyun.ecs.easysdk.preemptiveinstance.model.EnumRecommendationStrategy;
import com.aliyun.ecs.easysdk.preemptiveinstance.model.PreemptiveInstanceRecommendation;
import com.aliyun.ecs.easysdk.preemptiveinstance.model.PreemptiveInstanceRecommendationRequest;
import com.aliyuncs.ecs.model.v20140526.DescribeRegionsResponse;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.stream.Collectors;

public class EnterpriseLevelRecommendDemo {
    public static void main(String[] args) {
        // 设定使用的ak，sk
        String accessKey = "<your-access-key>";
        String secretKey = "<your-access-secret>";
        RecommendationUtil.initialization(accessKey,secretKey);
        PreemptiveInstanceRecommendationService preemptiveInstanceRecommendationService = EasyEcsSDK.getService(PreemptiveInstanceRecommendationService.class);
        PreemptiveInstanceBaseService preemptiveInstanceBaseService = EasyEcsSDK.getService(PreemptiveInstanceBaseService.class);
        // 单地域推荐
        singleRegionRecommendation(preemptiveInstanceRecommendationService, preemptiveInstanceBaseService);
        // 全地域推荐
//        allRegionRecommendation(preemptiveInstanceRecommendationService, preemptiveInstanceBaseService);
    }

    /**
     * 全地域，指定机型的企业级抢占式实例推荐；由于地域较多，全地域推荐耗时相对更长
     * @param preemptiveInstanceRecommendationService 抢占式实例推荐服务
     * @param preemptiveInstanceBaseService
     */
    private static void allRegionRecommendation(PreemptiveInstanceRecommendationService preemptiveInstanceRecommendationService, PreemptiveInstanceBaseService preemptiveInstanceBaseService) {
        PreemptiveInstanceRecommendationRequest request = new PreemptiveInstanceRecommendationRequest();
        // 设定推荐的地域范围
        List<String> allRegions = preemptiveInstanceBaseService.describeAllRegions().getData().stream().map(DescribeRegionsResponse.Region::getRegionId).collect(Collectors.toList());
        System.out.println(JSON.toJSONString(allRegions));
        request.setRegions(allRegions);
        // 设定需要推荐的实例规格
        request.setInstanceType("ecs.c5.large");
        // 设定推荐策略，见EnumRecommendationStrategy类
        request.setStrategy(EnumRecommendationStrategy.SUFFICIENT_INVENTORY_FIRST);
        // 设定产品类别
        request.setProductCategory(EnumEcsProductCategory.EnterpriseLevel);
        Response<List<PreemptiveInstanceRecommendation>> recommend = preemptiveInstanceRecommendationService.recommend(request);
        RecommendationUtil.reportRecommendation(recommend);
    }

    /**
     * 单地域，指定核数和CPU数的企业级抢占式实例推荐
     * @param preemptiveInstanceRecommendationService 抢占式实例推荐服务
     * @param preemptiveInstanceBaseService
     */
    private static void singleRegionRecommendation(PreemptiveInstanceRecommendationService preemptiveInstanceRecommendationService, PreemptiveInstanceBaseService preemptiveInstanceBaseService) {
        PreemptiveInstanceRecommendationRequest request = new PreemptiveInstanceRecommendationRequest();
        // 设定推荐的地域范围
        request.setRegions(Lists.newArrayList(EcsDemoConstants.REGION_SHANGHAI));
        // 设定需要推荐的实例核数（个）和内存大小（单位GB）
        request.setCores(2);
        request.setMemory(4);
        // 设定推荐策略，见EnumRecommendationStrategy类
        request.setStrategy(EnumRecommendationStrategy.SUFFICIENT_INVENTORY_FIRST);
        // 设定产品类别
        request.setProductCategory(EnumEcsProductCategory.EnterpriseLevel);
        Response<List<PreemptiveInstanceRecommendation>> recommend = preemptiveInstanceRecommendationService.recommend(request);
        RecommendationUtil.reportRecommendation(recommend);
    }
}

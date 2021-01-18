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

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;

import com.aliyun.ecs.easysdk.biz.constants.EcsInventoryStatusCategory;
import com.aliyun.ecs.easysdk.biz.constants.EnumEcsProductCategory;
import com.aliyun.ecs.easysdk.biz.model.Response;
import com.aliyun.ecs.easysdk.container.annotation.Autowired;
import com.aliyun.ecs.easysdk.container.annotation.BeanProperty;
import com.aliyun.ecs.easysdk.container.annotation.Qualifier;
import com.aliyun.ecs.easysdk.preemptiveinstance.CacheService;
import com.aliyun.ecs.easysdk.preemptiveinstance.PreemptiveInstanceBaseService;
import com.aliyun.ecs.easysdk.preemptiveinstance.PreemptiveInstanceRecommendationService;
import com.aliyun.ecs.easysdk.preemptiveinstance.model.DiscountInventoryModel;
import com.aliyun.ecs.easysdk.preemptiveinstance.model.EcsInstanceType;
import com.aliyun.ecs.easysdk.preemptiveinstance.model.EnumRecommendationStrategy;
import com.aliyun.ecs.easysdk.preemptiveinstance.model.PreemptiveInstanceRecommendation;
import com.aliyun.ecs.easysdk.preemptiveinstance.model.PreemptiveInstanceRecommendationRequest;
import com.aliyun.ecs.easysdk.preemptiveinstance.model.SpotPrice;
import com.aliyun.ecs.easysdk.preemptiveinstance.strategy.InventoryComparator;
import com.aliyun.ecs.easysdk.preemptiveinstance.strategy.PriceComparator;
import com.aliyun.ecs.easysdk.preemptiveinstance.strategy.ProductGenerationComparator;
import com.aliyuncs.ecs.model.v20140526.DescribeAvailableResourceResponse.AvailableZone;
import com.aliyuncs.ecs.model.v20140526.DescribeAvailableResourceResponse.AvailableZone.AvailableResource;
import com.aliyuncs.ecs.model.v20140526.DescribeAvailableResourceResponse.AvailableZone.AvailableResource.SupportedResource;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.aliyun.ecs.easysdk.biz.model.Response.ILLEGAL_PARAM;
import static com.aliyun.ecs.easysdk.preemptiveinstance.constants.EcsInstanceConstants.ECS_API_DEFAULT_REGION;

public class PreemptiveInstanceRecommendationServiceImpl implements PreemptiveInstanceRecommendationService {

    private static final Logger logger = LoggerFactory.getLogger(PreemptiveInstanceRecommendationServiceImpl.class);

    @Autowired
    @Qualifier(filters = {@BeanProperty(key = "cached", value = "true")})
    private PreemptiveInstanceBaseService preemptiveInstanceBaseService;
    @Autowired
    private CacheService cacheService;

    public void init() {
    }

    /**
     * 单地域内部推荐
     *
     * @param request
     * @return
     */
    public Response<List<PreemptiveInstanceRecommendation>> recommendInRegion(
        PreemptiveInstanceRecommendationRequest request) {
        List<String> regions = request.getRegions();
        List<String> zones = request.getZones();
        String region = regions.get(0);
        if (CollectionUtils.isEmpty(zones)) {
            return recommendBasedOnOpenApi(region, null, request);
        } else {
            String zone = zones.get(0);
            return recommendBasedOnOpenApi(region, zone, request);
        }
    }

    @Override
    public Response<List<PreemptiveInstanceRecommendation>> recommend(PreemptiveInstanceRecommendationRequest request) {
        List<String> regions = request.getRegions();
        List<String> zones = request.getZones();
        EnumRecommendationStrategy strategy = request.getStrategy();
        if (CollectionUtils.isEmpty(regions) || null == strategy) {
            return new Response<>(ILLEGAL_PARAM, "Illegal Param");
        }
        if (regions.size() == 1
            && (CollectionUtils.isEmpty(zones) || zones.size() == 1)) {
            return recommendInRegion(request);
        }
        return recommendAcrossRegions(request);
    }

    /**
     * 跨地域推荐
     *
     * @param request
     * @return
     */
    private Response<List<PreemptiveInstanceRecommendation>> recommendAcrossRegions(
        PreemptiveInstanceRecommendationRequest request) {
        EnumRecommendationStrategy strategy = request.getStrategy();
        List<String> regions = request.getRegions();
        List<String> zones = request.getZones();

        int core = request.getCores();
        int memory = request.getMemory();
        String requestInstanceType = request.getInstanceType();
        try {
            List<DiscountInventoryModel> models = buildDiscountInventoryModel(regions, zones, core, memory,
                requestInstanceType);
            if (EnumRecommendationStrategy.LOWEST_PRICE_FIRST == strategy) {
                Comparator<DiscountInventoryModel> priceComparator = new PriceComparator();
                return recommendByStrategy(models, request.getProductCategory(), priceComparator);
            } else if (EnumRecommendationStrategy.SUFFICIENT_INVENTORY_FIRST == strategy) {
                Comparator<DiscountInventoryModel> inventoryComparator = new InventoryComparator();
                return recommendByStrategy(models, request.getProductCategory(), inventoryComparator);
            } else if (EnumRecommendationStrategy.LATEST_PRODUCT_FIRST == strategy) {
                Comparator<DiscountInventoryModel> productGenerationComparator = new ProductGenerationComparator();
                return recommendByStrategy(models, request.getProductCategory(), productGenerationComparator);
            } else {
                return new Response<>(Response.COMMON_ERROR, "strategy not supported");
            }
        } catch (Exception e) {
            logger.error("recommend error. request: {}", JSON.toJSONString(request), e);
            return new Response<>(e);
        }
    }

    private Response<List<PreemptiveInstanceRecommendation>> recommendBasedOnOpenApi(String region, String zone,
                                                                                     PreemptiveInstanceRecommendationRequest request) {
        return preemptiveInstanceBaseService.describeRecommendInstanceType(region, zone, request);
    }

    private List<DiscountInventoryModel> buildDiscountInventoryModel(List<String> regions, List<String> zones, int core,
                                                                     int memory, String requestInstanceType) {
        List<DiscountInventoryModel> models = Lists.newArrayList();
        if (regions == null) {
            return models;
        }
        for (String region : regions) {
            Response<List<AvailableZone>> listResponse = preemptiveInstanceBaseService.describeAvailableResource(region,
                core,
                memory, requestInstanceType);
            if (!listResponse.getSuccess()) {
                continue;
            }
            List<AvailableZone> availableZones = listResponse.getData();
            if (availableZones == null) {
                continue;
            }
            for (AvailableZone availableZone : availableZones) {
                String zoneId = availableZone.getZoneId();
                if (CollectionUtils.isNotEmpty(zones) && !zones.contains(zoneId)) {
                    logger.info("skipped zone: {}", availableZone.getZoneId());
                    continue;
                }
                List<AvailableResource> availableResources = availableZone.getAvailableResources();
                for (AvailableResource availableResource : availableResources) {
                    List<SupportedResource> supportedResources = availableResource.getSupportedResources();
                    for (SupportedResource supportedResource : supportedResources) {
                        StopWatch stopWatch = new StopWatch();
                        stopWatch.start();
                        String status = supportedResource.getStatus();
                        String statusCategory = supportedResource.getStatusCategory();
                        String instanceType = supportedResource.getValue();
                        SpotPrice latestPrice = preemptiveInstanceBaseService.describeLatestSpotPrice(region, zoneId,
                            instanceType).getData();
                        EcsInstanceType ecsInstanceType = preemptiveInstanceBaseService
                            .describeInstanceType(
                                instanceType).getData();
                        if (ecsInstanceType == null) {
                            continue;
                        }
                        Map<String, Integer> instanceTypeToDiscountMap = Maps.newHashMap();
                        Response<List<EcsInstanceType>> listResponse1 = preemptiveInstanceBaseService
                            .describeInstanceTypes(
                                ecsInstanceType.getInstanceTypeFamily(), ECS_API_DEFAULT_REGION);
                        List<EcsInstanceType> ecsInstanceTypeList = Lists.newArrayList();
                        if (listResponse1.getSuccess() && CollectionUtils.isNotEmpty(listResponse1.getData())) {
                            ecsInstanceTypeList = listResponse1.getData();
                            for (EcsInstanceType instanceTypeAmongFamily : ecsInstanceTypeList) {
                                SpotPrice spotPrice = preemptiveInstanceBaseService.describeLatestSpotPrice(region,
                                    zoneId,
                                    instanceTypeAmongFamily.getInstanceTypeId()).getData();
                                if (spotPrice != null) {
                                    instanceTypeToDiscountMap.put(spotPrice.getInstanceType(), spotPrice.getDiscount());
                                }
                            }
                        }
                        DiscountInventoryModel model = new DiscountInventoryModel();
                        model.setRegion(region);
                        model.setZone(zoneId);
                        model.setInstanceFamilyLevel(ecsInstanceType.getInstanceFamilyLevel());
                        model.setInstanceGeneration(ecsInstanceType.getInstanceGeneration());
                        model.setInstanceFamily(ecsInstanceType.getInstanceTypeFamily());
                        model.setInstanceType(instanceType);
                        model.setStatus(status);
                        model.setStatusCategory(EcsInventoryStatusCategory.valueOf(statusCategory));
                        model.setPrice(latestPrice);
                        model.setInstanceTypesToSpotDiscountMap(instanceTypeToDiscountMap);
                        model.setInstanceTypesInFamily(ecsInstanceTypeList);
                        models.add(model);
                    }
                }
            }
        }
        return models;
    }

    private Response<List<PreemptiveInstanceRecommendation>> recommendByStrategy(
        List<DiscountInventoryModel> models, EnumEcsProductCategory productCategory,
        Comparator<DiscountInventoryModel> comparator) {

        // 排除掉关闭售卖且无库存的规格
        List<DiscountInventoryModel> collect = models.stream().filter(e -> {
            EcsInventoryStatusCategory statusCategory = e.getStatusCategory();
            return !(EcsInventoryStatusCategory.ClosedWithoutStock == statusCategory
                || EcsInventoryStatusCategory.WithoutStock == statusCategory);
        }).sorted(comparator).collect(Collectors.toList());

        return buildResponse(collect, productCategory);
    }

    private Response<List<PreemptiveInstanceRecommendation>> buildResponse(List<DiscountInventoryModel> models,
                                                                           EnumEcsProductCategory productCategory) {
        // 构建返回结构
        List<PreemptiveInstanceRecommendation> recommendations = Lists.newArrayList();
        for (DiscountInventoryModel model : models) {
            String instanceType = model.getInstanceType();
            String family = model.getInstanceFamily();
            String instanceFamilyLevel = model.getInstanceFamilyLevel();

            if (productCategory != null
                && !productCategory.name().equals(
                instanceFamilyLevel)) {
                logger.info(
                    "skipped instanceType: {} family: {} expected productCategory: {} actual productCategory: {}",
                    instanceType, family,
                    productCategory.name(), instanceFamilyLevel);
                continue;
            }

            PreemptiveInstanceRecommendation recommendation = new PreemptiveInstanceRecommendation();
            recommendation.setInstanceFamilyLevel(model.getInstanceFamilyLevel());
            recommendation.setInstanceGeneration(model.getInstanceGeneration());
            recommendation.setInstanceTypeFamily(model.getInstanceFamily());
            recommendation.setInstanceType(model.getInstanceType());
            recommendation.setZone(model.getZone());
            recommendation.setSpotPrice(model.getPrice().getSpotPrice());
            recommendation.setOriginPrice(model.getPrice().getOriginPrice());
            recommendation.setDiscount(model.getPrice().getDiscount());
            recommendation.setStatusCategory(model.getStatusCategory());

            recommendations.add(recommendation);
        }
        return new Response<>(recommendations);
    }

}

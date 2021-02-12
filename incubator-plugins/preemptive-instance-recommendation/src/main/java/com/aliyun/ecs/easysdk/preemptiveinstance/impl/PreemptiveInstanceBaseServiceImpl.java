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

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;

import com.aliyun.ecs.easysdk.biz.model.Response;
import com.aliyun.ecs.easysdk.preemptiveinstance.PreemptiveInstanceBaseService;
import com.aliyun.ecs.easysdk.preemptiveinstance.model.EcsInstanceType;
import com.aliyun.ecs.easysdk.preemptiveinstance.model.EnumRecommendationStrategy;
import com.aliyun.ecs.easysdk.preemptiveinstance.model.PreemptiveInstanceRecommendation;
import com.aliyun.ecs.easysdk.preemptiveinstance.model.PreemptiveInstanceRecommendationRequest;
import com.aliyun.ecs.easysdk.preemptiveinstance.model.SpotPrice;
import com.aliyun.ecs.easysdk.preemptiveinstance.utils.PreemptiveInstanceBeanUtils;
import com.aliyun.ecs.easysdk.preemptiveinstance.utils.PreemptiveInstanceDateUtils;
import com.aliyun.ecs.easysdk.preemptiveinstance.utils.PreemptiveInstanceUtils;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.ecs.model.v20140526.DescribeAvailableResourceRequest;
import com.aliyuncs.ecs.model.v20140526.DescribeAvailableResourceResponse;
import com.aliyuncs.ecs.model.v20140526.DescribeAvailableResourceResponse.AvailableZone;
import com.aliyuncs.ecs.model.v20140526.DescribeInstanceTypeFamiliesRequest;
import com.aliyuncs.ecs.model.v20140526.DescribeInstanceTypeFamiliesResponse;
import com.aliyuncs.ecs.model.v20140526.DescribeInstanceTypeFamiliesResponse.InstanceTypeFamily;
import com.aliyuncs.ecs.model.v20140526.DescribeInstanceTypesRequest;
import com.aliyuncs.ecs.model.v20140526.DescribeInstanceTypesResponse;
import com.aliyuncs.ecs.model.v20140526.DescribeInstanceTypesResponse.InstanceType;
import com.aliyuncs.ecs.model.v20140526.DescribeRecommendInstanceTypeRequest;
import com.aliyuncs.ecs.model.v20140526.DescribeRecommendInstanceTypeResponse;
import com.aliyuncs.ecs.model.v20140526.DescribeRegionsRequest;
import com.aliyuncs.ecs.model.v20140526.DescribeRegionsResponse;
import com.aliyuncs.ecs.model.v20140526.DescribeSpotPriceHistoryRequest;
import com.aliyuncs.ecs.model.v20140526.DescribeSpotPriceHistoryResponse;
import com.aliyuncs.ecs.model.v20140526.DescribeZonesRequest;
import com.aliyuncs.ecs.model.v20140526.DescribeZonesResponse;
import com.aliyuncs.exceptions.ClientException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.aliyun.ecs.easysdk.preemptiveinstance.constants.EcsInstanceConstants.ECS_API_DEFAULT_REGION;

public class PreemptiveInstanceBaseServiceImpl implements PreemptiveInstanceBaseService {
    private static final Logger logger = LoggerFactory.getLogger(PreemptiveInstanceBaseServiceImpl.class);

    private static final String VPC_NETWORK_TYPE = "vpc";

    @Resource
    private IAcsClient iAcsClient;
    /**
     * <instanceFamily, generation>
     */
    private Map<String, String> generationInfo;
    /**
     * <instanceType, instanceTypeFamily>
     */
    private Map<String, String> instanceTypeInfo;

    public void init() {
        logger.info("PreemptiveInstanceRecommendationServiceImpl initEcsGeneration start");
        generationInfo = initEcsGeneration();
        logger.info("PreemptiveInstanceRecommendationServiceImpl initEcsGeneration finished. generationInfo.size: {}",
                generationInfo.size());

        instanceTypeInfo = initEcsInstanceType();
        logger.info(
                "PreemptiveInstanceBaseServiceImpl initEcsInstanceType finished. instanceTypeInfo.size: {}",
                instanceTypeInfo.size());
    }

    private Map<String, String> initEcsInstanceType() {
        Map<String, String> info = Maps.newHashMap();
        for (String family : generationInfo.keySet()) {
            Response<List<EcsInstanceType>> listResponse = describeInstanceTypes(family, ECS_API_DEFAULT_REGION);
            if (!listResponse.getSuccess() || null == listResponse.getData()) {
                continue;
            }
            List<EcsInstanceType> data = listResponse.getData();
            for (EcsInstanceType datum : data) {
                info.put(datum.getInstanceTypeId(), family);
            }
        }
        return info;
    }

    Map<String, String> initEcsGeneration() {
        // <family, generation>
        Map<String, String> generationInfo = Maps.newHashMap();
        Response<List<InstanceTypeFamily>> listResponse = describeInstanceTypeFamily();
        if (!listResponse.getSuccess()) {
            logger.error("describeInstanceTypeFamily error. msg: {}", listResponse.getMessage());
            return generationInfo;
        }
        List<InstanceTypeFamily> familyList = listResponse.getData();
        for (InstanceTypeFamily instanceTypeFamily : familyList) {
            String family = instanceTypeFamily.getInstanceTypeFamilyId();
            String generation = instanceTypeFamily.getGeneration();
            generationInfo.put(family, generation);
        }
        return generationInfo;
    }

    @Override
    public Response<List<AvailableZone>> describeAvailableResource(String regionId, Integer cores, Integer memory,
                                                                   String instanceType) {
        DescribeAvailableResourceRequest request = new DescribeAvailableResourceRequest();
        request.setSysRegionId(regionId);
        request.setDestinationResource("InstanceType");
        request.setIoOptimized("optimized");
        request.setNetworkCategory("vpc");
        request.setResourceType("instance");
        request.setInstanceChargeType("PostPaid");
        request.setSpotStrategy("SpotAsPriceGo");
        if (StringUtils.isNotBlank(instanceType)) {
            request.setInstanceType(instanceType);
        } else {
            request.setCores(cores);
            request.setMemory(memory.floatValue());
        }
        try {
            DescribeAvailableResourceResponse acsResponse = iAcsClient.getAcsResponse(request);
            if (CollectionUtils.isEmpty(acsResponse.getAvailableZones())) {
                logger.warn("getAcsResponse return null. request: {} response: {}", JSON.toJSONString(request),
                        JSON.toJSON(acsResponse));
                return new Response<>();
            }
            List<AvailableZone> availableZones = acsResponse.getAvailableZones();
            return new Response<>(availableZones);
        } catch (ClientException e) {
            logger.error("describeAvailableResource error. regionId: {} cores: {} mem: {}", request, cores, memory, e);
            return new Response<>(e);
        }
    }

    @Override
    public Response<SpotPrice> describeLatestSpotPrice(String regionId, String zoneId, String instanceType) {
        if (StringUtils.isBlank(instanceType)) {
            return null;
        }
        // 拉取三小时的历史数据
        Response<List<SpotPrice>> listResponse = describeSpotPriceHistory(regionId, zoneId,
                instanceType,
                3);
        if (!listResponse.getSuccess()) {
            return new Response(listResponse.getCode(), listResponse.getMessage());
        }
        List<SpotPrice> spotPrices = listResponse.getData();
        if (CollectionUtils.isEmpty(spotPrices)) {
            return new Response();
        }
        PreemptiveInstanceUtils.sortHistoryPrice(spotPrices);
        return new Response(spotPrices.get(0));
    }

    @Override
    public Response<List<SpotPrice>> describeSpotPriceHistory(String regionId, String zoneId, String instanceType,
                                                              int hours) {
        try {
            DescribeSpotPriceHistoryRequest acsRequest = new DescribeSpotPriceHistoryRequest();
            acsRequest.setSysRegionId(regionId);
            acsRequest.setZoneId(zoneId);
            acsRequest.setNetworkType("vpc");
            acsRequest.setIoOptimized("optimized");
            acsRequest.setInstanceType(instanceType);
            DescribeSpotPriceHistoryResponse acsResponse = iAcsClient.getAcsResponse(acsRequest);
            if (CollectionUtils.isEmpty(acsResponse.getSpotPrices())) {
                logger.warn("listSpotPriceHistory return null. region: {} zone: {} instanceType: {} days: {}", regionId,
                        zoneId,
                        instanceType, hours);
                return new Response<>();
            }
            List<SpotPrice> prices = Lists.newArrayList();
            for (DescribeSpotPriceHistoryResponse.SpotPriceType spotPrice : acsResponse.getSpotPrices()) {
                SpotPrice price = PreemptiveInstanceBeanUtils.convertSpotType(spotPrice);
                prices.add(price);
            }
            return new Response<>(prices);
        } catch (Exception e) {
            logger.error("listSpotPriceHistory error.", e);
            return new Response<>(e);
        }
    }

    @Override
    public Response<EcsInstanceType> describeInstanceType(String instanceType) {
        String family = getInstanceFamily(instanceType).getData();
        String generation = getGeneration(family);
        Response<List<EcsInstanceType>> listResponse = describeInstanceTypes(family, ECS_API_DEFAULT_REGION);
        if (!listResponse.getSuccess()) {
            return new Response<>(listResponse.getCode(), listResponse.getMessage());
        }
        if (CollectionUtils.isEmpty(listResponse.getData())) {
            logger.warn("describeInstanceTypes return null. family: {}", family);
            return new Response<>();
        }
        List<EcsInstanceType> ecsInstanceTypes = listResponse.getData();

        EcsInstanceType targetEcsInstanceType = null;

        for (EcsInstanceType ecsInstanceType : ecsInstanceTypes) {
            if (instanceType.equals(ecsInstanceType.getInstanceTypeId())) {
                targetEcsInstanceType = ecsInstanceType;
                break;
            }
        }
        if (null == targetEcsInstanceType) {
            logger.warn("describeInstanceType return null. instanceType: {}", instanceType);
            return new Response<>();
        }
        targetEcsInstanceType.setInstanceGeneration(generation);
        return new Response<>(targetEcsInstanceType);
    }

    private String getGeneration(String family) {
        return StringUtils.isBlank(generationInfo.get(family)) ? "ecs-1" : generationInfo.get(family);
    }

    @Override
    public Response<List<EcsInstanceType>> describeInstanceTypes(String instanceFamily, String region) {
        DescribeInstanceTypesRequest describeInstanceTypesRequest = new DescribeInstanceTypesRequest();
        if (!StringUtils.isBlank(region)) {
            describeInstanceTypesRequest.setSysRegionId(region);
        }
        if (!StringUtils.isBlank(instanceFamily)) {
            describeInstanceTypesRequest.setInstanceTypeFamily(instanceFamily);
        }
        List<EcsInstanceType> list = Lists.newArrayList();
        try {
            DescribeInstanceTypesResponse acsResponse = iAcsClient.getAcsResponse(describeInstanceTypesRequest);
            for (InstanceType instanceType : acsResponse.getInstanceTypes()) {
                EcsInstanceType ecsInstanceType = PreemptiveInstanceBeanUtils.convertInstanceType(instanceType);
                list.add(ecsInstanceType);
            }
            if (CollectionUtils.isEmpty(acsResponse.getInstanceTypes())) {
                logger.warn("describeInstanceTypes return null. instanceFamily: {}", instanceFamily);
            }
            // 按照核数从低到高排列
            Collections.sort(list);
            return new Response<>(list);
        } catch (Exception e) {
            logger.error("describeInstanceTypes error. instanceFamily: {}", instanceFamily, e);
            return new Response<>(e);
        }
    }

    @Override
    public Response<List<InstanceTypeFamily>> describeInstanceTypeFamily() {
        try {
            DescribeInstanceTypeFamiliesRequest familiesRequest = new DescribeInstanceTypeFamiliesRequest();
            familiesRequest.setSysRegionId(ECS_API_DEFAULT_REGION);
            DescribeInstanceTypeFamiliesResponse acsResponse = iAcsClient.getAcsResponse(familiesRequest);
            List<InstanceTypeFamily> instanceTypeFamilies = acsResponse.getInstanceTypeFamilies();
            return new Response<>(instanceTypeFamilies);
        } catch (Exception e) {
            logger.error("DescribeInstanceTypeFamilies error", e);
            return new Response<>(e);
        }
    }

    @Override
    public Response<List<DescribeZonesResponse.Zone>> describeZones(String regionId) {
        try {
            DescribeZonesRequest describeZonesRequest = new DescribeZonesRequest();
            describeZonesRequest.setSysRegionId(regionId);
            DescribeZonesResponse acsResponse = iAcsClient.getAcsResponse(describeZonesRequest);
            List<DescribeZonesResponse.Zone> zones = acsResponse.getZones();
            return new Response<>(zones);
        } catch (Exception e) {
            logger.error("Describe Zones error", e);
            return new Response<>(e);
        }
    }

    @Override
    public Response<List<PreemptiveInstanceRecommendation>> describeRecommendInstanceType(String region, String zone, PreemptiveInstanceRecommendationRequest request) {
        List<PreemptiveInstanceRecommendation> recommendations = Lists.newArrayList();
        try {
            DescribeRecommendInstanceTypeRequest describeRecommendInstanceTypeRequest = new DescribeRecommendInstanceTypeRequest();
            describeRecommendInstanceTypeRequest.setSysRegionId(region);
            describeRecommendInstanceTypeRequest.setZoneId(zone);
            describeRecommendInstanceTypeRequest.setNetworkType(VPC_NETWORK_TYPE);
            describeRecommendInstanceTypeRequest.setInstanceChargeType("PostPaid");
            describeRecommendInstanceTypeRequest.setSpotStrategy("SpotAsPriceGo");
            EnumRecommendationStrategy strategy = request.getStrategy();
            if (strategy == EnumRecommendationStrategy.LATEST_PRODUCT_FIRST) {
                describeRecommendInstanceTypeRequest.setPriorityStrategy("NewProductFirst");
            } else if (strategy == EnumRecommendationStrategy.LOWEST_PRICE_FIRST) {
                describeRecommendInstanceTypeRequest.setPriorityStrategy("PriceFirst");
            } else {
                describeRecommendInstanceTypeRequest.setPriorityStrategy("InventoryFirst");
            }
            if (!StringUtils.isBlank(request.getInstanceType())) {
                describeRecommendInstanceTypeRequest.setInstanceType(request.getInstanceType());
            } else {
                describeRecommendInstanceTypeRequest.setCores(request.getCores());
                describeRecommendInstanceTypeRequest.setMemory((float) request.getMemory());
                describeRecommendInstanceTypeRequest.setInstanceFamilyLevel(request.getProductCategory().name());
            }
            DescribeRecommendInstanceTypeResponse acsResponse = iAcsClient.getAcsResponse(describeRecommendInstanceTypeRequest);
            List<DescribeRecommendInstanceTypeResponse.RecommendInstanceType> preemptiveInstanceRecommendations = acsResponse.getData();
            for (DescribeRecommendInstanceTypeResponse.RecommendInstanceType recommendInstanceType : preemptiveInstanceRecommendations) {
                for (DescribeRecommendInstanceTypeResponse.RecommendInstanceType.Zone instanceZone : recommendInstanceType.getZones()) {
                    String instanceType = recommendInstanceType.getInstanceType().getInstanceType();
                    String zoneNo = instanceZone.getZoneNo();
                    PreemptiveInstanceRecommendation preemptiveInstanceRecommendation = new PreemptiveInstanceRecommendation();
                    preemptiveInstanceRecommendation.setInstanceType(instanceType);
                    preemptiveInstanceRecommendation.setZone(zoneNo);
                    // 获得价格信息
                    SpotPrice spotPrice = describeLatestSpotPrice(region, zoneNo, instanceType).getData();
                    preemptiveInstanceRecommendation.setDiscount(spotPrice.getDiscount());
                    preemptiveInstanceRecommendation.setOriginPrice(spotPrice.getOriginPrice());
                    preemptiveInstanceRecommendation.setSpotPrice(spotPrice.getSpotPrice());
                    recommendations.add(preemptiveInstanceRecommendation);
                }
            }
            return new Response<>(recommendations);
        } catch (Exception e) {
            logger.error("DescribeRecommendInstanceTypes error", e);
            logger.error("Request is : " + JSON.toJSONString(request));
            return new Response<>(e);
        }
    }

    @Override
    public Response<List<DescribeRegionsResponse.Region>> describeAllRegions() {
        DescribeRegionsRequest describeRegionsRequest = new DescribeRegionsRequest();
        describeRegionsRequest.setResourceType("instance");
        DescribeRegionsResponse acsResponse = null;
        try {
            acsResponse = iAcsClient.getAcsResponse(describeRegionsRequest);
        } catch (ClientException e) {
            logger.error("Describe All Regions Error", e);
        }
        if (acsResponse == null){
            return new Response<>(Lists.newArrayList());
        }
        return new Response<>(acsResponse.getRegions());
    }

    @Override
    public Response<String> getInstanceFamily(String instanceType) {
        if (instanceTypeInfo.containsKey(instanceType)) {
            return new Response<>(instanceTypeInfo.get(instanceType));
        }
        return new Response<>(PreemptiveInstanceUtils.getInstanceFamily(instanceType));
    }

    public Map<String, String> getGenerationInfo() {
        return generationInfo;
    }

    public Map<String, String> getInstanceTypeInfo() {
        return instanceTypeInfo;
    }

}

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

import com.aliyun.ecs.easysdk.biz.model.Response;
import com.aliyun.ecs.easysdk.container.annotation.Autowired;
import com.aliyun.ecs.easysdk.container.annotation.BeanProperty;
import com.aliyun.ecs.easysdk.container.annotation.Qualifier;
import com.aliyun.ecs.easysdk.preemptiveinstance.CacheService;
import com.aliyun.ecs.easysdk.preemptiveinstance.PreemptiveInstanceBaseService;
import com.aliyun.ecs.easysdk.preemptiveinstance.model.EcsInstanceType;
import com.aliyun.ecs.easysdk.preemptiveinstance.model.PreemptiveInstanceRecommendation;
import com.aliyun.ecs.easysdk.preemptiveinstance.model.PreemptiveInstanceRecommendationRequest;
import com.aliyun.ecs.easysdk.preemptiveinstance.model.SpotPrice;
import com.aliyuncs.ecs.model.v20140526.DescribeAvailableResourceResponse.AvailableZone;
import com.aliyuncs.ecs.model.v20140526.DescribeInstanceTypeFamiliesResponse.InstanceTypeFamily;
import com.aliyuncs.ecs.model.v20140526.DescribeRegionsResponse;
import com.aliyuncs.ecs.model.v20140526.DescribeZonesResponse.Zone;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class PreemptiveInstanceBaseServiceCachedImpl implements PreemptiveInstanceBaseService {
    @Autowired
    @Qualifier(filters = {@BeanProperty(key = "cached", value = "false")})
    private PreemptiveInstanceBaseService preemptiveInstanceBaseService;
    @Autowired
    private CacheService cacheService;

    public void init() {
    }

    @Override
    public Response<List<AvailableZone>> describeAvailableResource(String regionId, Integer cores, Integer memory,
                                                                   String instanceType) {

        return preemptiveInstanceBaseService.describeAvailableResource(regionId, cores, memory, instanceType);
    }

    @Override
    public Response<SpotPrice> describeLatestSpotPrice(String regionId, String zoneId, String instanceType) {
        String value = cacheService.get(Lists.newArrayList("describeLatestSpotPrice", regionId, zoneId,
                instanceType));
        if (StringUtils.isNotBlank(value)) {
            SpotPrice spotPrice = JSON.parseObject(value, SpotPrice.class);
            return new Response(spotPrice);
        }

        Response<List<SpotPrice>> listResponse = describeSpotPriceHistory(regionId, null, instanceType, 3);
        if (!listResponse.getSuccess() || CollectionUtils.isEmpty(listResponse.getData())) {
            return preemptiveInstanceBaseService.describeLatestSpotPrice(regionId, zoneId, instanceType);
        }
        List<SpotPrice> latestPrice = listResponse.getData();
        SpotPrice suitablePrice = null;
        for (SpotPrice spotPrice : latestPrice) {
            if (spotPrice.getZoneId().equals(zoneId)) {
                suitablePrice = spotPrice;
            }
            cacheService.put(Lists.newArrayList("describeLatestSpotPrice", regionId, spotPrice.getZoneId(), instanceType), JSON.toJSONString(spotPrice));
        }
        cacheService.put(Lists.newArrayList("describeLatestSpotPrice", regionId, zoneId,
                instanceType), JSON.toJSONString(suitablePrice));
        return new Response<>(suitablePrice);
    }

    @Override
    public Response<List<SpotPrice>> describeSpotPriceHistory(String regionId, String zoneId, String instanceType,
                                                              int hours) {
        String value = cacheService.get(Lists.newArrayList("describeSpotPriceHistory", regionId, zoneId,
                instanceType, String.valueOf(hours)));
        if (StringUtils.isNotBlank(value)) {
            List<SpotPrice> spotPrices = JSON.parseArray(value, SpotPrice.class);
            return new Response(spotPrices);
        }
        Response<List<SpotPrice>> listResponse = preemptiveInstanceBaseService.describeSpotPriceHistory(regionId,
                zoneId, instanceType, hours);

        if (listResponse.getSuccess() && CollectionUtils.isNotEmpty(listResponse.getData())) {
            for (SpotPrice spotPrice : listResponse.getData()) {
                cacheService.put(Lists.newArrayList("describeLatestSpotPrice", regionId, spotPrice.getZoneId(),
                        instanceType, String.valueOf(hours)), JSON.toJSONString(spotPrice));
            }
            cacheService.put(Lists.newArrayList("describeSpotPriceHistory", regionId, zoneId,
                    instanceType, String.valueOf(hours)), JSON.toJSONString(listResponse.getData()));
        } else {
            cacheService.put(Lists.newArrayList("describeSpotPriceHistory", regionId, zoneId,
                    instanceType, String.valueOf(hours)), JSON.toJSONString(Lists.newArrayList()));
        }
        return listResponse;
    }

    @Override
    public Response<EcsInstanceType> describeInstanceType(String instanceType) {
        String value = cacheService.get(Lists.newArrayList("describeInstanceType", instanceType));
        if (StringUtils.isNotBlank(value)) {
            EcsInstanceType ecsInstanceType = JSON.parseObject(value, EcsInstanceType.class);
            return new Response(ecsInstanceType);
        }
        Response<EcsInstanceType> ecsInstanceTypeResponse = preemptiveInstanceBaseService.describeInstanceType(
                instanceType);

        if (ecsInstanceTypeResponse.getSuccess() && null != ecsInstanceTypeResponse.getData()) {
            cacheService.put(Lists.newArrayList("describeInstanceType", instanceType),
                    JSON.toJSONString(ecsInstanceTypeResponse.getData()));
        } else {
            cacheService.put(Lists.newArrayList("describeInstanceType", instanceType),
                    JSON.toJSONString(null));
        }
        return ecsInstanceTypeResponse;
    }

    @Override
    public Response<List<EcsInstanceType>> describeInstanceTypes(String instanceFamily, String region) {
        String value = cacheService.get(Lists.newArrayList("describeInstanceTypes", instanceFamily, region));
        if (StringUtils.isNotBlank(value)) {
            List<EcsInstanceType> spotPrices = JSON.parseArray(value, EcsInstanceType.class);
            return new Response(spotPrices);
        }
        Response<List<EcsInstanceType>> listResponse = preemptiveInstanceBaseService.describeInstanceTypes(
                instanceFamily, region);
        if (listResponse.getSuccess() && CollectionUtils.isNotEmpty(listResponse.getData())) {
            cacheService.put(Lists.newArrayList("describeInstanceTypes", instanceFamily, region),
                    JSON.toJSONString(listResponse.getData()));
        } else {
            cacheService.put(Lists.newArrayList("describeInstanceTypes", instanceFamily, region),
                    JSON.toJSONString(null));
        }
        return listResponse;
    }

    @Override
    public Response<List<InstanceTypeFamily>> describeInstanceTypeFamily() {
        return preemptiveInstanceBaseService.describeInstanceTypeFamily();
    }

    @Override
    public Response<String> getInstanceFamily(String instanceType) {
        return preemptiveInstanceBaseService.getInstanceFamily(instanceType);
    }

    @Override
    public Response<List<Zone>> describeZones(String regionId) {
        return preemptiveInstanceBaseService.describeZones(regionId);
    }

    @Override
    public Response<List<PreemptiveInstanceRecommendation>> describeRecommendInstanceType(String region, String zone,
                                                                                          PreemptiveInstanceRecommendationRequest productCategory) {
        return preemptiveInstanceBaseService.describeRecommendInstanceType(region, zone, productCategory);
    }

    @Override
    public Response<List<DescribeRegionsResponse.Region>> describeAllRegions() {
        return preemptiveInstanceBaseService.describeAllRegions();
    }
}

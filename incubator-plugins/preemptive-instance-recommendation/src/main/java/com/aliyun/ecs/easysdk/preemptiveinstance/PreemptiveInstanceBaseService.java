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
package com.aliyun.ecs.easysdk.preemptiveinstance;

import java.util.List;

import com.aliyun.ecs.easysdk.biz.model.Response;
import com.aliyun.ecs.easysdk.preemptiveinstance.model.EcsInstanceType;
import com.aliyun.ecs.easysdk.preemptiveinstance.model.PreemptiveInstanceRecommendation;
import com.aliyun.ecs.easysdk.preemptiveinstance.model.PreemptiveInstanceRecommendationRequest;
import com.aliyun.ecs.easysdk.preemptiveinstance.model.SpotPrice;
import com.aliyuncs.ecs.model.v20140526.DescribeAvailableResourceResponse.AvailableZone;
import com.aliyuncs.ecs.model.v20140526.DescribeInstanceTypeFamiliesResponse.InstanceTypeFamily;
import com.aliyuncs.ecs.model.v20140526.DescribeRegionsResponse;
import com.aliyuncs.ecs.model.v20140526.DescribeZonesResponse;

public interface PreemptiveInstanceBaseService {

    Response<List<AvailableZone>> describeAvailableResource(String regionId, Integer cores, Integer memory,
                                                            String instanceType);

    Response<SpotPrice> describeLatestSpotPrice(String regionId, String zoneId, String instanceType);

    Response<List<SpotPrice>> describeSpotPriceHistory(String regionId, String zoneId, String instanceType,
                                                       int hours);

    Response<EcsInstanceType> describeInstanceType(String instanceType);

    Response<List<EcsInstanceType>> describeInstanceTypes(String instanceFamily, String region);

    Response<List<InstanceTypeFamily>> describeInstanceTypeFamily();

    Response<String> getInstanceFamily(String instanceType);

    Response<List<DescribeZonesResponse.Zone>> describeZones(String regionId);

    Response<List<PreemptiveInstanceRecommendation>> describeRecommendInstanceType(String region, String zone, PreemptiveInstanceRecommendationRequest productCategory);

    Response<List<DescribeRegionsResponse.Region>> describeAllRegions();
}

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
import java.util.Map;

import com.aliyun.ecs.easysdk.EasyEcsSDK;
import com.aliyun.ecs.easysdk.biz.constants.EnumEcsProductCategory;
import com.aliyun.ecs.easysdk.biz.model.Response;
import com.aliyun.ecs.easysdk.preemptiveinstance.PreemptiveInstanceBaseService;
import com.aliyun.ecs.easysdk.preemptiveinstance.model.EcsInstanceType;
import com.aliyun.ecs.easysdk.preemptiveinstance.model.SpotPrice;
import com.aliyun.ecs.easysdk.system.config.ConfigKeys;
import com.aliyuncs.ecs.model.v20140526.DescribeInstanceTypeFamiliesResponse.InstanceTypeFamily;
import com.google.common.collect.Maps;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.aliyun.ecs.easysdk.preemptiveinstance.constants.EcsInstanceConstants.ECS_API_DEFAULT_REGION;
import static com.aliyun.ecs.easysdk.preemptiveinstance.constants.EcsInstanceConstants.LARGE_INSTANCE_TYPE_CORES;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class PreemptiveInstanceBaseServiceImplTest {
    static PreemptiveInstanceBaseService preemptiveInstanceBaseService;

    @BeforeClass
    public static void setUp() {
        EasyEcsSDK.setProperty(ConfigKeys.ALIYUN_EASY_ECS_SDK_ACCESS_KEY_ID, "<your-access-key>");
        EasyEcsSDK.setProperty(ConfigKeys.ALIYUN_EASY_ECS_SDK_ACCESS_SECRET, "<your-secret-key>");
        EasyEcsSDK.init();
        Map<String, String> properties = Maps.newHashMap();
        properties.put("cached", "true");
        preemptiveInstanceBaseService
                = EasyEcsSDK.getService(
                PreemptiveInstanceBaseService.class, properties);
    }

    @Test
    public void describeInstanceTypesTest() {
        String instanceFamily = "ecs.t5";
        Response<List<EcsInstanceType>> response = preemptiveInstanceBaseService.describeInstanceTypes(
                instanceFamily, ECS_API_DEFAULT_REGION);
        assertNotNull(response.getData());
        assertTrue(response.getData().size() > 0);
        assertEquals(EnumEcsProductCategory.CreditEntryLevel.name(),
                response.getData().get(0).getInstanceFamilyLevel());

        instanceFamily = "ecs.s6";
        response = preemptiveInstanceBaseService.describeInstanceTypes(
                instanceFamily, ECS_API_DEFAULT_REGION);
        assertNotNull(response.getData());
        assertTrue(response.getData().size() > 0);
        assertTrue(response.getData().get(response.getData().size() - 1).getCpuCoreCount() <
                LARGE_INSTANCE_TYPE_CORES);
        assertEquals(EnumEcsProductCategory.EntryLevel.name(), response.getData().get(0).getInstanceFamilyLevel());

        instanceFamily = "ecs.c6";
        response = preemptiveInstanceBaseService.describeInstanceTypes(
                instanceFamily, ECS_API_DEFAULT_REGION);
        assertNotNull(response.getData());
        assertTrue(response.getData().size() > 0);
        assertEquals(EnumEcsProductCategory.EnterpriseLevel.name(), response.getData().get(0).getInstanceFamilyLevel());
    }

    @Test
    public void describeInstanceTypeTest() {
        String instanceType = "ecs.c6.xlarge";
        Response<EcsInstanceType> response = preemptiveInstanceBaseService.describeInstanceType(
                instanceType);
        assertNotNull(response.getData());
        instanceType = "ecs.c6vn.8xlarge";
        response = preemptiveInstanceBaseService.describeInstanceType(
                instanceType);
        assertNotNull(response.getData());
    }

    @Test
    public void describeInstanceTypeFamilyTest() {
        Response<List<InstanceTypeFamily>> listResponse = preemptiveInstanceBaseService.describeInstanceTypeFamily();
        assertNotNull(listResponse.getData());
    }

    @Test
    public void describeSpotPriceHistoryTest() {
        String region = "cn-hangzhou";
        String zone = "cn-hangzhou-j";
        String instanceType = "ecs.c6.8xlarge";
        Response<List<SpotPrice>> listResponse = preemptiveInstanceBaseService.describeSpotPriceHistory(region, zone,
                instanceType, 1);
        assertNotNull(listResponse.getData());
    }

    @Test
    public void describeAllRegionsTest(){
        Assert.assertNotNull(preemptiveInstanceBaseService.describeAllRegions().getData());
    }
}

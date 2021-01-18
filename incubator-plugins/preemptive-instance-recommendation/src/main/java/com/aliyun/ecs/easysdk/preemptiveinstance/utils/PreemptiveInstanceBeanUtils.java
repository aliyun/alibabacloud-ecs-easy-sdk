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
package com.aliyun.ecs.easysdk.preemptiveinstance.utils;

import com.aliyun.ecs.easysdk.preemptiveinstance.model.EcsInstanceType;
import com.aliyun.ecs.easysdk.preemptiveinstance.model.SpotPrice;
import com.aliyuncs.ecs.model.v20140526.DescribeInstanceTypesResponse.InstanceType;
import com.aliyuncs.ecs.model.v20140526.DescribeSpotPriceHistoryResponse;

public class PreemptiveInstanceBeanUtils {

    public static SpotPrice convertSpotType(DescribeSpotPriceHistoryResponse.SpotPriceType spotPriceType) {
        SpotPrice spotPrice = new SpotPrice();
        spotPrice.setInstanceType(spotPriceType.getInstanceType());
        spotPrice.setSpotPrice(spotPriceType.getSpotPrice());
        spotPrice.setNetworkType(spotPriceType.getNetworkType());
        spotPrice.setIoOptimized(spotPriceType.getIoOptimized());
        spotPrice.setOriginPrice(spotPriceType.getOriginPrice());
        spotPrice.setTimestamp(spotPriceType.getTimestamp());
        spotPrice.setZoneId(spotPriceType.getZoneId());
        spotPrice.setDiscount((int)(spotPriceType.getSpotPrice() * 100 / spotPriceType.getOriginPrice()));
        return spotPrice;
    }

    public static EcsInstanceType convertInstanceType(InstanceType instanceType) {
        EcsInstanceType ecsInstanceType = new EcsInstanceType();
        ecsInstanceType.setInstanceTypeId(instanceType.getInstanceTypeId());
        ecsInstanceType.setCpuCoreCount(instanceType.getCpuCoreCount());
        ecsInstanceType.setMemorySize(instanceType.getMemorySize());
        ecsInstanceType.setInstanceFamilyLevel(instanceType.getInstanceFamilyLevel());
        ecsInstanceType.setInstanceTypeFamily(instanceType.getInstanceTypeFamily());
        ecsInstanceType.setLocalStorageCapacity(instanceType.getLocalStorageCapacity());
        ecsInstanceType.setLocalStorageAmount(instanceType.getLocalStorageAmount());
        ecsInstanceType.setGpuAmount(instanceType.getGPUAmount());
        ecsInstanceType.setGpuSpec(instanceType.getGPUSpec());
        ecsInstanceType.setInitialCredit(instanceType.getInitialCredit());
        ecsInstanceType.setBaselineCredit(instanceType.getBaselineCredit());
        ecsInstanceType.setEniQuantity(instanceType.getEniQuantity());
        ecsInstanceType.setEniPrivateIpAddressQuantity(instanceType.getEniPrivateIpAddressQuantity());
        ecsInstanceType.setEniIpv6AddressQuantity(instanceType.getEniIpv6AddressQuantity());
        ecsInstanceType.setInstanceBandwidthRx(instanceType.getInstanceBandwidthRx());
        ecsInstanceType.setInstanceBandwidthTx(instanceType.getInstanceBandwidthTx());
        ecsInstanceType.setInstancePpsRx(instanceType.getInstancePpsRx());
        ecsInstanceType.setInstancePpsTx(instanceType.getInstancePpsTx());
        ecsInstanceType.setTotalEniQueueQuantity(instanceType.getEniTotalQuantity());
        ecsInstanceType.setEniTrunkSupported(instanceType.getEniTrunkSupported());
        ecsInstanceType.setEniTotalQuantity(instanceType.getEniTotalQuantity());
        return ecsInstanceType;
    }
}

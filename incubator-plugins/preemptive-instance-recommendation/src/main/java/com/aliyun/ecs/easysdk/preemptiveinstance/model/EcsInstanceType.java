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
import org.apache.commons.lang3.StringUtils;

public class EcsInstanceType implements Comparable<EcsInstanceType> {

    private String instanceTypeId;

    private Integer cpuCoreCount;

    private Float memorySize;
    /**
     * {@linkplain EnumEcsProductCategory}
     */
    private String instanceFamilyLevel;

    private String instanceGeneration;

    private String instanceTypeFamily;

    private Long localStorageCapacity;

    private Integer localStorageAmount;

    private String localStorageCategory;

    private Integer gpuAmount;

    private String gpuSpec;

    private Integer initialCredit;

    private Integer baselineCredit;

    private Integer eniQuantity;

    private Integer eniPrivateIpAddressQuantity;

    private Integer eniIpv6AddressQuantity;

    private Integer instanceBandwidthRx;

    private Integer instanceBandwidthTx;

    private Long instancePpsRx;

    private Long instancePpsTx;

    private Integer totalEniQueueQuantity;

    private Boolean eniTrunkSupported;

    private Integer eniTotalQuantity;

    @Override
    public int compareTo(EcsInstanceType o) {
        if (this.getCpuCoreCount() > o.getCpuCoreCount()) {
            return 1;
        } else if (this.getCpuCoreCount() < o.getCpuCoreCount()) {
            return -1;
        }

        if (Float.compare(this.getMemorySize(),o.getMemorySize()) == 1) {
            return 1;
        } else if (Float.compare(this.getMemorySize(),o.getMemorySize()) == -1 ) {
            return -1;
        }
        if (StringUtils.length(this.getInstanceTypeId()) > StringUtils.length(o.getInstanceTypeId())) {
            return 1;
        }
        return -1;
    }

    public String getInstanceTypeId() {
        return instanceTypeId;
    }

    public void setInstanceTypeId(String instanceTypeId) {
        this.instanceTypeId = instanceTypeId;
    }

    public Integer getCpuCoreCount() {
        return cpuCoreCount;
    }

    public void setCpuCoreCount(Integer cpuCoreCount) {
        this.cpuCoreCount = cpuCoreCount;
    }

    public Float getMemorySize() {
        return memorySize;
    }

    public void setMemorySize(Float memorySize) {
        this.memorySize = memorySize;
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

    public String getInstanceTypeFamily() {
        return instanceTypeFamily;
    }

    public void setInstanceTypeFamily(String instanceTypeFamily) {
        this.instanceTypeFamily = instanceTypeFamily;
    }

    public Long getLocalStorageCapacity() {
        return localStorageCapacity;
    }

    public void setLocalStorageCapacity(Long localStorageCapacity) {
        this.localStorageCapacity = localStorageCapacity;
    }

    public Integer getLocalStorageAmount() {
        return localStorageAmount;
    }

    public void setLocalStorageAmount(Integer localStorageAmount) {
        this.localStorageAmount = localStorageAmount;
    }

    public String getLocalStorageCategory() {
        return localStorageCategory;
    }

    public void setLocalStorageCategory(String localStorageCategory) {
        this.localStorageCategory = localStorageCategory;
    }

    public Integer getGpuAmount() {
        return gpuAmount;
    }

    public void setGpuAmount(Integer gpuAmount) {
        this.gpuAmount = gpuAmount;
    }

    public String getGpuSpec() {
        return gpuSpec;
    }

    public void setGpuSpec(String gpuSpec) {
        this.gpuSpec = gpuSpec;
    }

    public Integer getInitialCredit() {
        return initialCredit;
    }

    public void setInitialCredit(Integer initialCredit) {
        this.initialCredit = initialCredit;
    }

    public Integer getBaselineCredit() {
        return baselineCredit;
    }

    public void setBaselineCredit(Integer baselineCredit) {
        this.baselineCredit = baselineCredit;
    }

    public Integer getEniQuantity() {
        return eniQuantity;
    }

    public void setEniQuantity(Integer eniQuantity) {
        this.eniQuantity = eniQuantity;
    }

    public Integer getEniPrivateIpAddressQuantity() {
        return eniPrivateIpAddressQuantity;
    }

    public void setEniPrivateIpAddressQuantity(Integer eniPrivateIpAddressQuantity) {
        this.eniPrivateIpAddressQuantity = eniPrivateIpAddressQuantity;
    }

    public Integer getEniIpv6AddressQuantity() {
        return eniIpv6AddressQuantity;
    }

    public void setEniIpv6AddressQuantity(Integer eniIpv6AddressQuantity) {
        this.eniIpv6AddressQuantity = eniIpv6AddressQuantity;
    }

    public Integer getInstanceBandwidthRx() {
        return instanceBandwidthRx;
    }

    public void setInstanceBandwidthRx(Integer instanceBandwidthRx) {
        this.instanceBandwidthRx = instanceBandwidthRx;
    }

    public Integer getInstanceBandwidthTx() {
        return instanceBandwidthTx;
    }

    public void setInstanceBandwidthTx(Integer instanceBandwidthTx) {
        this.instanceBandwidthTx = instanceBandwidthTx;
    }

    public Long getInstancePpsRx() {
        return instancePpsRx;
    }

    public void setInstancePpsRx(Long instancePpsRx) {
        this.instancePpsRx = instancePpsRx;
    }

    public Long getInstancePpsTx() {
        return instancePpsTx;
    }

    public void setInstancePpsTx(Long instancePpsTx) {
        this.instancePpsTx = instancePpsTx;
    }

    public Integer getTotalEniQueueQuantity() {
        return totalEniQueueQuantity;
    }

    public void setTotalEniQueueQuantity(Integer totalEniQueueQuantity) {
        this.totalEniQueueQuantity = totalEniQueueQuantity;
    }

    public Boolean getEniTrunkSupported() {
        return eniTrunkSupported;
    }

    public void setEniTrunkSupported(Boolean eniTrunkSupported) {
        this.eniTrunkSupported = eniTrunkSupported;
    }

    public Integer getEniTotalQuantity() {
        return eniTotalQuantity;
    }

    public void setEniTotalQuantity(Integer eniTotalQuantity) {
        this.eniTotalQuantity = eniTotalQuantity;
    }
}

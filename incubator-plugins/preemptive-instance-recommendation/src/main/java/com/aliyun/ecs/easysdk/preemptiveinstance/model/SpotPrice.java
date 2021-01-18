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

public class SpotPrice {
    private String zoneId;

    private String instanceType;

    private String ioOptimized;

    private String timestamp;

    private String networkType;

    private Float spotPrice;

    private Float originPrice;

    private Integer discount;

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public String getInstanceType() {
        return instanceType;
    }

    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

    public String getIoOptimized() {
        return ioOptimized;
    }

    public void setIoOptimized(String ioOptimized) {
        this.ioOptimized = ioOptimized;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    public Float getSpotPrice() {
        return spotPrice;
    }

    public void setSpotPrice(Float spotPrice) {
        this.spotPrice = spotPrice;
    }

    public Float getOriginPrice() {
        return originPrice;
    }

    public void setOriginPrice(Float originPrice) {
        this.originPrice = originPrice;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }
}

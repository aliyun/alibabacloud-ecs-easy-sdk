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
package com.aliyun.ecs.easysdk.preemptiveinstance.task;

import com.aliyun.ecs.easysdk.preemptiveinstance.PreemptiveInstanceBaseService;
import com.aliyun.ecs.easysdk.preemptiveinstance.model.SpotPrice;

import java.util.concurrent.RecursiveTask;

/**
 * @author xianzhi
 * @date Jan 25th, 2021
 */
public class DescribeLatestSpotPriceTask extends RecursiveTask<SpotPrice> {

    private final PreemptiveInstanceBaseService preemptiveInstanceBaseService;
    private final String regionId;
    private final String zoneId;
    private final String instanceTypeId;

    public DescribeLatestSpotPriceTask(String regionId, String zoneId, String instanceTypeId, PreemptiveInstanceBaseService preemptiveInstanceBaseService){
        this.regionId = regionId;
        this.zoneId = zoneId;
        this.instanceTypeId = instanceTypeId;
        this.preemptiveInstanceBaseService = preemptiveInstanceBaseService;
    }

    @Override
    protected SpotPrice compute() {
        return preemptiveInstanceBaseService.describeLatestSpotPrice(regionId, zoneId,
                instanceTypeId).getData();
    }
}

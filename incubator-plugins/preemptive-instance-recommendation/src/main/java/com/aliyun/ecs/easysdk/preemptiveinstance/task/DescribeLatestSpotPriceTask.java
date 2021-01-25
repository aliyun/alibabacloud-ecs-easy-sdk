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

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

import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.aliyun.ecs.easysdk.biz.constants.EcsInventoryStatusCategory;
import com.aliyun.ecs.easysdk.preemptiveinstance.PreemptiveInstanceBaseService;
import com.aliyun.ecs.easysdk.preemptiveinstance.model.SpotPrice;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PreemptiveInstanceUtils {
    private static final Logger logger = LoggerFactory.getLogger(PreemptiveInstanceUtils.class);

    /**
     * 按时间倒序排列
     *
     * @param spotPrices
     */
    public static void sortHistoryPrice(List<SpotPrice> spotPrices) {
        Collections.sort(spotPrices, (a, b) -> {
            String timestamp = a.getTimestamp();
            String timestamp1 = b.getTimestamp();
            try {
                Date date = PreemptiveInstanceDateUtils.parseGmt2LocalDate(timestamp);
                Date date1 = PreemptiveInstanceDateUtils.parseGmt2LocalDate(timestamp1);
                return date.before(date1) ? 1 : -1;
            } catch (Exception e) {
                logger.error("parseGMT2LocalDate error timestamp: {} timestamp1: {}", timestamp, timestamp1, e);
            }
            return 0;
        });
    }

    public static int compareEcsGeneration(String generationA, String generationB) {
        int expectedStringSize = 2;
        List<String> stringsA = Splitter.on('-').splitToList(generationA);
        List<String> stringsB = Splitter.on('-').splitToList(generationB);
        if (stringsA.size() != expectedStringSize || stringsB.size() != expectedStringSize) {
            logger.error("illegal generation generationA: {} generationB: {}", generationA, generationB);
            return 0;
        }
        int gA = Integer.parseInt(stringsA.get(1));
        int gB = Integer.parseInt(stringsB.get(1));
        return gA - gB;
    }

    /**
     * 查询实例对应的规格族. 作为FallBack的实现方式. 默认请使用:
     * {@linkplain PreemptiveInstanceBaseService#getInstanceFamily(java.lang.String)}
     *
     * @param instanceType
     * @return
     */
    public static String getInstanceFamily(String instanceType) {
        List<String> strings = Splitter.on('.').splitToList(instanceType);
        List<String> subList = strings.subList(0, strings.size() - 1);
        String str2 = Joiner.on('.').join(subList);
        if (str2.indexOf('-') < 0) {
            return str2;
        }
        return str2.substring(0, str2.indexOf('-'));
    }

    /**
     * (price1 - price) / price
     *
     * @param price
     * @param price1
     * @return
     */
    public static int priceDiffPercent(Float price, Float price1) {
        return (int) ((price1 - price) * 100 / price);
    }

    /**
     * 比较库存状态.
     *
     * @return
     */
    public static int compareInventoryStatus(EcsInventoryStatusCategory category,
                                             EcsInventoryStatusCategory category1) {
        return category.ordinal() - category1.ordinal();
    }
}

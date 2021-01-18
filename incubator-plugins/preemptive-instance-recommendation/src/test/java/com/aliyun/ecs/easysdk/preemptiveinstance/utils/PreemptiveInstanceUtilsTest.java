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

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.aliyun.ecs.easysdk.preemptiveinstance.model.SpotPrice;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class PreemptiveInstanceUtilsTest {
    @Test
    public void sortHistoryPriceTest() throws ParseException {
        List<SpotPrice> prices = Lists.newArrayList();
        SpotPrice price = new SpotPrice();
        Date now = Calendar.getInstance().getTime();
        price.setTimestamp(PreemptiveInstanceDateUtils.formatIso8601Date(now));
        prices.add(price);

        price = new SpotPrice();
        Date date = DateUtils.addHours(now, 1);
        price.setTimestamp(PreemptiveInstanceDateUtils.formatIso8601Date(date));
        prices.add(price);

        price = new SpotPrice();
        date = DateUtils.addHours(now, 4);
        price.setTimestamp(PreemptiveInstanceDateUtils.formatIso8601Date(date));
        prices.add(price);

        price = new SpotPrice();
        date = DateUtils.addDays(now, 4);
        price.setTimestamp(PreemptiveInstanceDateUtils.formatIso8601Date(date));
        prices.add(price);

        price = new SpotPrice();
        date = DateUtils.addDays(now, -7);
        price.setTimestamp(PreemptiveInstanceDateUtils.formatIso8601Date(date));
        prices.add(price);

        PreemptiveInstanceUtils.sortHistoryPrice(prices);
        SpotPrice spotPrice = prices.get(0);
        SpotPrice spotPrice1 = prices.get(1);
        SpotPrice spotPrice2 = prices.get(2);
        SpotPrice spotPrice3 = prices.get(3);
        assertTrue(PreemptiveInstanceDateUtils.parseGmt2LocalDate(spotPrice.getTimestamp())
                .after(PreemptiveInstanceDateUtils.parseGmt2LocalDate(spotPrice1.getTimestamp())));
        assertTrue(PreemptiveInstanceDateUtils.parseGmt2LocalDate(spotPrice1.getTimestamp())
                .after(PreemptiveInstanceDateUtils.parseGmt2LocalDate(spotPrice2.getTimestamp())));
        assertTrue(PreemptiveInstanceDateUtils.parseGmt2LocalDate(spotPrice2.getTimestamp())
                .after(PreemptiveInstanceDateUtils.parseGmt2LocalDate(spotPrice3.getTimestamp())));
    }

    @Test
    public void getInstanceFamilyTest() {
        String instanceType = "ecs.c5.large";
        String instanceFamily = PreemptiveInstanceUtils.getInstanceFamily(instanceType);
        assertEquals("ecs.c5", instanceFamily);

        instanceType = "ecs.t5-c1m2.xlarge";
        instanceFamily = PreemptiveInstanceUtils.getInstanceFamily(instanceType);
        assertEquals("ecs.t5", instanceFamily);
    }

    @Test
    public void compareEcsGenerationTest() {
        String generationA = "ecs-1";
        String generationB = "ecs-3";
        int i = PreemptiveInstanceUtils.compareEcsGeneration(generationA, generationB);
        assertTrue(i < 0);

        generationA = "ecs-4";
        generationB = "ecs-3";
        i = PreemptiveInstanceUtils.compareEcsGeneration(generationA, generationB);
        assertTrue(i > 0);

        generationA = "ecs-3";
        generationB = "ecs-11";
        i = PreemptiveInstanceUtils.compareEcsGeneration(generationA, generationB);
        assertTrue(i < 0);

        generationA = "ecs-3";
        generationB = "ecs-3";
        i = PreemptiveInstanceUtils.compareEcsGeneration(generationA, generationB);
        assertTrue(i == 0);
    }

    @Test
    public void priceDiffPercentTest() {
        Float price = 0.03f;
        Float price1 = 0.032f;
        int i = PreemptiveInstanceUtils.priceDiffPercent(price, price1);
        assertEquals(6, i);

        price = 0.032f;
        price1 = 0.03f;
        i = PreemptiveInstanceUtils.priceDiffPercent(price, price1);
        assertEquals(-6, i);
    }
}

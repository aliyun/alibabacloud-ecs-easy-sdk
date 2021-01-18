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
import java.util.Date;

import org.apache.commons.lang3.time.FastDateFormat;
import org.junit.Test;

import static com.aliyun.ecs.easysdk.preemptiveinstance.utils.PreemptiveInstanceDateUtils.DATE_FORMAT;
import static junit.framework.TestCase.assertEquals;

public class PreemptiveInstanceDateUtilsTest {

    @Test
    public void parseGMT2LocalDateTest() throws ParseException {
        // UTC+0
        String timeStr = "2020-09-22T00:00:00Z";
        Date date = PreemptiveInstanceDateUtils.parseGmt2LocalDate(timeStr);
        FastDateFormat fdf = FastDateFormat.getInstance(DATE_FORMAT);
        // UTC+8
        String format = fdf.format(date);
        assertEquals("2020-09-22 08:00:00", format);
    }

    @Test
    public void formatIso8601DateTest() throws ParseException {
        FastDateFormat fdf = FastDateFormat.getInstance(DATE_FORMAT);
        Date date = fdf.parse("2020-09-22 08:00:00");
        String format = PreemptiveInstanceDateUtils.formatIso8601Date(date);
        assertEquals("2020-09-22T00:00:00Z", format);
    }
}

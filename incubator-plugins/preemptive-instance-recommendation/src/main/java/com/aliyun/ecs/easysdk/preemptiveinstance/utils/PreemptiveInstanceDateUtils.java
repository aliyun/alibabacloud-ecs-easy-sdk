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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;

public class PreemptiveInstanceDateUtils {
    public static final String ISO8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String GMT = "GMT";

    /**
     * 将Date类型转换成ISO8601标准表示, 并使用UTC +0时间, 格式为yyyy-MM-ddTHH:mm:ssZ
     *
     * @param date
     * @return
     */
    public static String formatIso8601Date(Date date) {
        SimpleDateFormat rfc822DateFormat = new SimpleDateFormat(ISO8601_DATE_FORMAT, Locale.US);
        rfc822DateFormat.setTimeZone(new SimpleTimeZone(0, GMT));
        return rfc822DateFormat.format(date);
    }

    /**
     * @param iso8601GmtDate ISO8601格式的gmt时间
     * @throws ParseException
     */
    public static Date parseGmt2LocalDate(String iso8601GmtDate) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ISO8601_DATE_FORMAT);
        simpleDateFormat.setTimeZone(SimpleTimeZone.getTimeZone(GMT));
        return simpleDateFormat.parse(iso8601GmtDate);
    }

}

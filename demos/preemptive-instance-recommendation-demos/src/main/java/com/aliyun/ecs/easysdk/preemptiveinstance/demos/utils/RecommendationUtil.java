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
package com.aliyun.ecs.easysdk.preemptiveinstance.demos.utils;

import com.alibaba.fastjson.JSON;
import com.aliyun.ecs.easysdk.EasyEcsSDK;
import com.aliyun.ecs.easysdk.biz.model.Response;
import com.aliyun.ecs.easysdk.preemptiveinstance.model.PreemptiveInstanceRecommendation;
import com.aliyun.ecs.easysdk.system.config.ConfigKeys;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

public class RecommendationUtil {
    public static void initialization(String accessKey, String secretKey){
        EasyEcsSDK.setProperty(ConfigKeys.ALIYUN_EASY_ECS_SDK_ACCESS_KEY_ID, accessKey);
        EasyEcsSDK.setProperty(ConfigKeys.ALIYUN_EASY_ECS_SDK_ACCESS_SECRET, secretKey);
        EasyEcsSDK.init();
    }

    public static void reportRecommendation(Response<List<PreemptiveInstanceRecommendation>> response){
        if (CollectionUtils.isEmpty(response.getData())){
            System.out.println("Blank Recommendation Response");
            return;
        }
        System.out.println(JSON.toJSONString(response));
        System.out.println("------------Recommend List------------");
        for (PreemptiveInstanceRecommendation preemptiveInstanceRecommendation : response.getData()){
            System.out.println(JSON.toJSONString(preemptiveInstanceRecommendation));
        }
        System.out.println("----------Recommend List End----------");
    }
}

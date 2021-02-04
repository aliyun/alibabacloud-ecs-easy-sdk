package com.aliyun.ecs.easysdk;/*
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
import com.alibaba.fastjson.JSON;
import com.aliyun.ecs.easysdk.biz.constants.EnumEcsProductCategory;
import com.aliyun.ecs.easysdk.biz.model.Response;
import com.aliyun.ecs.easysdk.preemptiveinstance.PreemptiveInstanceRecommendationService;
import com.aliyun.ecs.easysdk.preemptiveinstance.model.EnumRecommendationStrategy;
import com.aliyun.ecs.easysdk.preemptiveinstance.model.PreemptiveInstanceRecommendation;
import com.aliyun.ecs.easysdk.preemptiveinstance.model.PreemptiveInstanceRecommendationRequest;
import com.aliyun.ecs.easysdk.system.config.ConfigKeys;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.aliyun.ecs.easysdk.model.InstanceTypeInfo;
import com.aliyun.ecs.easysdk.model.UserCredential;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * @author xianzhi
 * @date Jan 25, 2021
 */
public class CommandLineToolMain {
    public static void main(String[] args) {
        Options options = getOptions();
        HelpFormatter helpFormatter = new HelpFormatter();
        CommandLine commandLine;
        CommandLineParser parser = new DefaultParser();
        try {
            commandLine = parser.parse(options, args);
            if (commandLine.hasOption(Constant.HELP_OPTION_SHORT)) {
                // 打印使用帮助
                helpFormatter.printHelp("Alibaba Cloud EasySDK", options, true);
            }
            UserCredential userCredential = getUserCredential(commandLine);
            if (userCredential == null) {
                return;
            }
            InstanceTypeInfo instanceTypeInfo = getInstanceTypeInfo(commandLine);
            if (instanceTypeInfo == null) {
                System.out.println("InstanceType Information and Cpu&Memory is blank, please check");
                return;
            }
            List<String> regions = getRegions(commandLine);
            if (regions == null) {
                System.out.println("Region Information is blank, please check");
                return;
            }
            EnumEcsProductCategory productCategory = getEcsProductCategory(commandLine);
            if (productCategory == null) {
                // 使用默认产品类别：企业级实例
                productCategory = EnumEcsProductCategory.EnterpriseLevel;
            }
            EnumRecommendationStrategy recommendationStrategy = getRecommendationStrategy(commandLine);
            if (recommendationStrategy == null) {
                // 使用默认策略：库存优先策略
                recommendationStrategy = EnumRecommendationStrategy.SUFFICIENT_INVENTORY_FIRST;
            }
            int limit = 5;
            if (commandLine.hasOption(Constant.LIMIT_OPTION_SHORT)) {
                limit = Integer.parseInt(commandLine.getOptionValue(Constant.LIMIT_OPTION_SHORT));
            }
            PreemptiveInstanceRecommendationRequest request = generateRequest(instanceTypeInfo, regions, productCategory, recommendationStrategy, limit);
            printRequestInfo(request);
            initEasySdk(userCredential.getAccessKey(), userCredential.getSecretKey());
            PreemptiveInstanceRecommendationService preemptiveInstanceRecommendationService = EasyEcsSDK.getService(PreemptiveInstanceRecommendationService.class);
            Response<List<PreemptiveInstanceRecommendation>> response = preemptiveInstanceRecommendationService.recommend(request);
            reportRecommendations(response);
        } catch (Exception e) {
            System.out.println("Parameter error, please check your command");
            e.printStackTrace();
            helpFormatter.printHelp("Alibaba Cloud EasySDK", options, true);
        }
    }

    private static void printRequestInfo(PreemptiveInstanceRecommendationRequest request) {
        System.out.println("------------Request Info------------");
        System.out.println("Regions : " + request.getRegions().toString());
        System.out.println("Cpu Core Count : " + request.getCores());
        System.out.println("Memory Size(G) : " + request.getMemory());
        System.out.println("Instance Type : " + request.getInstanceType());
        System.out.println("Product Category : " + request.getProductCategory().name());
        System.out.println("Recommend Strategy : " + request.getStrategy().name());
    }

    private static PreemptiveInstanceRecommendationRequest generateRequest(InstanceTypeInfo instanceTypeInfo, List<String> regions,  EnumEcsProductCategory productCategory, EnumRecommendationStrategy recommendationStrategy, int limit) {
        PreemptiveInstanceRecommendationRequest request = new PreemptiveInstanceRecommendationRequest();
        request.setRegions(regions);
        if (!StringUtils.isBlank(instanceTypeInfo.getInstanceType())) {
            request.setInstanceType(instanceTypeInfo.getInstanceType());
        } else {
            request.setCores(instanceTypeInfo.getCores());
            request.setMemory(instanceTypeInfo.getMemory());
        }
        request.setLimit(limit);
        request.setProductCategory(productCategory);
        request.setStrategy(recommendationStrategy);
        return request;
    }

    public static InstanceTypeInfo getInstanceTypeInfo(CommandLine commandLine) {
        // 读取CPU&Memory或实例规格信息
        InstanceTypeInfo instanceTypeInfo = new InstanceTypeInfo();
        if (commandLine.hasOption(Constant.CORES_OPTION_SHORT) && commandLine.hasOption(Constant.MEMORY_OPTION_SHORT)) {
            try {
                instanceTypeInfo.setCores(Integer.parseInt(commandLine.getOptionValue(Constant.CORES_OPTION_SHORT)));
                instanceTypeInfo.setMemory(Integer.parseInt(commandLine.getOptionValue(Constant.MEMORY_OPTION_SHORT)));
            }catch (NumberFormatException e){
                // 提示输入的cpu或memory不合法，返回null
                System.out.println("CPU or Memory format is illegal, please input integer number");
                return null;
            }

        } else if (commandLine.hasOption(Constant.INSTANCETYPE_OPTION_SHORT)) {
            instanceTypeInfo.setInstanceType(commandLine.getOptionValue(Constant.INSTANCETYPE_OPTION_SHORT));
        } else {
            // 提示未指定实例类型或CPU&Memory，返回null
            return null;
        }
        return instanceTypeInfo;
    }

    public static List<String> getRegions(CommandLine commandLine) {
        // 读取regions信息
        List<String> regions;
        if (commandLine.hasOption(Constant.REGIONS_OPTION_SHORT)) {
            String regionString = commandLine.getOptionValue(Constant.REGIONS_OPTION_SHORT);
            regions = Splitter.on(",").splitToList(regionString);
            if (regions.size() == 0) {
                // 未指定regions，返回null
                return null;
            }
        } else {
            // 未指定regions，返回null
            return null;
        }
        return regions;
    }

    private static UserCredential getUserCredential(CommandLine commandLine) {
        // 读取ak，sk
        UserCredential userCredential = new UserCredential();
        if (commandLine.hasOption(Constant.AK_OPTION_SHORT) && commandLine.hasOption(Constant.SK_OPTION_SHORT)) {
            userCredential.setAccessKey(commandLine.getOptionValue(Constant.AK_OPTION_SHORT));
            userCredential.setSecretKey(commandLine.getOptionValue(Constant.SK_OPTION_SHORT));
        } else if (commandLine.hasOption(Constant.FILE_OPTION_SHORT)) {
            // 从.properties文件中读取ak和sk
            String filePath = commandLine.getOptionValue(Constant.FILE_OPTION_SHORT);
            Properties properties = new Properties();
            try {
                FileInputStream fileInputStream = new FileInputStream(filePath);
                properties.load(fileInputStream);
                userCredential.setAccessKey(properties.getProperty("accessKey"));
                userCredential.setSecretKey(properties.getProperty("secretKey"));
            } catch (IOException e) {
                System.out.println("Please check the .properties file path and the standard file format");
                return null;
            }
        } else {
            // 提示未指定ak或sk,返回null
            return null;
        }
        return userCredential;
    }

    public static EnumEcsProductCategory getEcsProductCategory(CommandLine commandLine) {
        List<EnumEcsProductCategory> categoryOptions = Lists.newArrayList(EnumEcsProductCategory.EnterpriseLevel, EnumEcsProductCategory.EntryLevel, EnumEcsProductCategory.CreditEntryLevel);
        EnumEcsProductCategory productCategory = null;
        if (commandLine.hasOption(Constant.CATEGORY_OPTION_SHORT)) {
            try{
                int categoryInput = Integer.parseInt(commandLine.getOptionValue(Constant.CATEGORY_OPTION_SHORT));
                for (EnumEcsProductCategory category : categoryOptions) {
                    if (category.ordinal() + 1 == categoryInput) {
                        productCategory = category;
                    }
                }
            }catch (NumberFormatException e){
                return null;
            }
        } else {
            // 未指定category，程序退出
            return null;
        }
        return productCategory;
    }

    public static EnumRecommendationStrategy getRecommendationStrategy(CommandLine commandLine) {
        // 读取recommendationStrategy信息
        List<EnumRecommendationStrategy> strategyOptions = Lists.newArrayList(EnumRecommendationStrategy.SUFFICIENT_INVENTORY_FIRST, EnumRecommendationStrategy.LATEST_PRODUCT_FIRST, EnumRecommendationStrategy.LOWEST_PRICE_FIRST);
        EnumRecommendationStrategy recommendationStrategy = null;
        if (commandLine.hasOption(Constant.STRATEGY_OPTION_SHORT)) {
            int strategyInput = Integer.parseInt(commandLine.getOptionValue(Constant.STRATEGY_OPTION_SHORT));
            for (EnumRecommendationStrategy strategy : strategyOptions) {
                if (strategy.ordinal() + 1 == strategyInput) {
                    recommendationStrategy = strategy;
                }
            }
        } else {
            // 未指定recommendationStrategy信息，返回null
            return null;
        }
        return recommendationStrategy;
    }

    private static void initEasySdk(String accessKey, String secretKey) {
        EasyEcsSDK.setProperty(ConfigKeys.ALIYUN_EASY_ECS_SDK_ACCESS_KEY_ID, accessKey);
        EasyEcsSDK.setProperty(ConfigKeys.ALIYUN_EASY_ECS_SDK_ACCESS_SECRET, secretKey);
        EasyEcsSDK.init();
    }

    private static void reportRecommendations(Response<List<PreemptiveInstanceRecommendation>> response) {
        if (CollectionUtils.isEmpty(response.getData())) {
            System.out.println("Blank Recommendation Response");
            return;
        }
        System.out.println("------------Recommend List------------");
        for (PreemptiveInstanceRecommendation preemptiveInstanceRecommendation : response.getData()) {
            System.out.println(JSON.toJSONString(preemptiveInstanceRecommendation));
        }
        System.out.println("----------Recommend List End----------");
    }

    public static Options getOptions() {
        Options options = new Options();
        Option opt = new Option(Constant.HELP_OPTION_SHORT, Constant.HELP_OPTION_LONG, false, "Print help");
        options.addOption(opt);

        opt = new Option(Constant.AK_OPTION_SHORT, Constant.AK_OPTION_LONG, true, "Your aliyun access key");
        options.addOption(opt);

        opt = new Option(Constant.SK_OPTION_SHORT, Constant.SK_OPTION_LONG, true, "Your aliyun secret key");
        options.addOption(opt);

        opt = new Option(Constant.REGIONS_OPTION_SHORT, Constant.REGIONS_OPTION_LONG, true, "Regions, split by comma, such as cn-shanghai,cn-beijing");
        options.addOption(opt);

        opt = new Option(Constant.CORES_OPTION_SHORT, Constant.CORES_OPTION_LONG, true, "Core number such as 2");
        options.addOption(opt);

        opt = new Option(Constant.MEMORY_OPTION_SHORT, Constant.MEMORY_OPTION_LONG, true, "Memory size(G) such as 4");
        options.addOption(opt);

        opt = new Option(Constant.INSTANCETYPE_OPTION_SHORT, Constant.INSTANCETYPE_OPTION_LONG, true, "InstanceType such as ecs.g6.large");
        options.addOption(opt);

        opt = new Option(Constant.LIMIT_OPTION_SHORT, Constant.LIMIT_OPTION_LONG, true, "Maximum recommendation count, default value is 5");
        options.addOption(opt);

        opt = new Option(Constant.CATEGORY_OPTION_SHORT, Constant.CATEGORY_OPTION_LONG, true, "ProductCategory; 1 means EnterpriseLevel; 2 means EntryLevel; 3 means CreditEntryLevel");
        options.addOption(opt);

        opt = new Option(Constant.STRATEGY_OPTION_SHORT, Constant.STRATEGY_OPTION_LONG, true, "RecommendStrategy; 1 means LOWEST_PRICE_FIRST; 2 means SUFFICIENT_INVENTORY_FIRST; 3 means LATEST_PRODUCT_FIRST");
        options.addOption(opt);

        opt = new Option(Constant.FILE_OPTION_SHORT, Constant.FILE_OPTION_LONG, true, "AK/SK file path; Note: it must be a java .properties file, following the standard format");
        options.addOption(opt);
        return options;
    }
}

# Alibaba Cloud EasySDK Project - 阿里云EasySDK项目

Alibaba Cloud EasySDK is a Java-Based Plugin Framework Which Aims to Provide Multiple Functionalities Such as Instance Recommendation, Instance Lifecycle Management to the Alibaba Cloud Users. 

阿里云EasySDK是一个基于Java的插件框架，致力于为阿里云的用户提供多种实用功能，如：实例推荐，实例生命周期管理等

## Features - 功能
### Preemptive Instance Recommendation - 抢占式实例推荐
User can get preemptive instance recommendation from the recommendation plugin based on some requirement description of the instance. This feature will benefit much on cloud user's using experience to help the user find cheapest or more long-lasting preemptive instances. 

用户可以基于对实例的一些描述从而获得一些列的实例推荐结果。这个功能会对用户用户购买到最便宜或更持久的抢占式实例有很大的帮助。

See https://github.com/aliyun/alibabacloud-ecs-easy-sdk/blob/master/incubator-plugins/preemptive-instance-recommendation/README.md

详见 https://github.com/aliyun/alibabacloud-ecs-easy-sdk/blob/master/incubator-plugins/preemptive-instance-recommendation/README.md

## Getting started - 快速开始
The demos module contains some easy to use code demos which can help you get recommendations in a few  seconds. You can clone this project and run demos in com.aliyun.ecs.easysdk.preemptiveinstance.demos.enterpriselevel package. For example, in EnterpriseLevelRecommendDemo.java, input your access key and secret key and change the region, zone, strategy properties if you need. Then just run this java class and you will see a list of recommendation.

项目中的demo模块包含了许多简单好用的代码示例，可以帮助你快速地获得实例推荐结果。你可以Clone此代码库到本地并且运行com.aliyun.ecs.easysdk.preemptiveinstance.demos.enterpriselevel包中的代码示例。比如，在EnterpriseLevelRecommendDemo.java中，输入你的access key与secret key，并且输入你所需购买的抢占式实例所在的地域、可用区、推荐策略等信息并且运行这个类，你将会看到一系列的推荐结果

## Contribute & Reporting bugs - 贡献代码&报告代码缺陷
You can open issues if you want to contribute to this project or report bugs.

如果你想帮助此项目更加完善，你可以通过创建issues的方式来提供意见或者报告代码缺陷

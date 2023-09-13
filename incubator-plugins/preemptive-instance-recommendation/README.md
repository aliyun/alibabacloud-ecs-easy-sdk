# Preemptive Instance Recommendation - 抢占式实例推荐

抢占式实例推荐是阿里云EasySDK项目中的一个插件。它致力于基于用户的需求提供一系列的阿里云抢占式实例推荐结果以帮助用户获得更好的使用体验。

Preemptive Instance Recommendation is one of the plugins provided by Alibaba Cloud EasySDK Project. It aims to give out a series Alibaba Cloud preemptive instance recommendations based on user's requirement to help with user's better using experience.

## Quick Start - 快速开始

引入Maven依赖

```
<dependency>
  <groupId>com.aliyun.ecs.easysdk</groupId>
  <artifactId>preemptive-instance-recommendation</artifactId>
  <version>1.1.0</version>
</dependency>
```

调用推荐接口,并且获取推荐结果

```java
// --------------------------代码示例1--------------------------
// 填写自己阿里云账号的access key与secret key
String accessKey = "<your-access-key>";
String secretKey = "<your-secret-key>";
// EasySDK初始化
EasyEcsSDK.setProperty(ConfigKeys.ALIYUN_EASY_ECS_SDK_ACCESS_KEY_ID, accessKey);
EasyEcsSDK.setProperty(ConfigKeys.ALIYUN_EASY_ECS_SDK_ACCESS_SECRET, secretKey);
EasyEcsSDK.init();
PreemptiveInstanceRecommendationService preemptiveInstanceRecommendationService = EasyEcsSDK.getService(PreemptiveInstanceRecommendationService.class);
PreemptiveInstanceRecommendationRequest request = new PreemptiveInstanceRecommendationRequest();
// 设定推荐的地域范围，如：上海和杭州；推荐接口支持多地域推荐，将多个地域同时传入List中即可
List<String> regionList = new ArrayList<>();
regionList.add("cn-shanghai");
regionList.add("cn-hangzhou");
request.setRegions(regionList);
// 设定需要推荐的实例规格，如：ecs.gn6e-c12g1.3xlarge；推荐接口目前仅支持指定单个实例规格；还可以通过指定CPU，Memeory数量进行推荐，见示例2
request.setInstanceType("ecs.gn6e-c12g1.3xlarge");
// 设定推荐策略，见EnumRecommendationStrategy类，共支持三种推荐策略：库存优先，价格优先，新代产品优先
request.setStrategy(EnumRecommendationStrategy.SUFFICIENT_INVENTORY_FIRST);
// 设定产品类别，见EnumEcsProductCategory类，共支持三种实例类型：企业级、入门级、性能突发型；实例类型需要与request.setInstanceType中传入的规格一致
request.setProductCategory(EnumEcsProductCategory.EnterpriseLevel);
// 获取推荐结果并输出
Response<List<PreemptiveInstanceRecommendation>> recommend = preemptiveInstanceRecommendationService.recommend(request);
if (recommend.getData() == null || recommend.getData().size() == 0){
  System.out.println("Blank Recommendation Response");
  return;
}
System.out.println(JSON.toJSONString(recommend));
System.out.println("------------Recommend List------------");
for (PreemptiveInstanceRecommendation preemptiveInstanceRecommendation : recommend.getData()){
  System.out.println(JSON.toJSONString(preemptiveInstanceRecommendation));
}
System.out.println("----------Recommend List End----------");


// --------------------------代码示例2--------------------------
// 填写自己阿里云账号的access key与secret key
String accessKey = "<your-access-key>";
String secretKey = "<your-secret-key>";
// EasySDK初始化
EasyEcsSDK.setProperty(ConfigKeys.ALIYUN_EASY_ECS_SDK_ACCESS_KEY_ID, accessKey);
EasyEcsSDK.setProperty(ConfigKeys.ALIYUN_EASY_ECS_SDK_ACCESS_SECRET, secretKey);
EasyEcsSDK.init();
PreemptiveInstanceRecommendationService preemptiveInstanceRecommendationService = EasyEcsSDK.getService(PreemptiveInstanceRecommendationService.class);
PreemptiveInstanceRecommendationRequest request = new PreemptiveInstanceRecommendationRequest();
// 设定推荐的地域范围，如：上海和杭州；推荐接口支持多地域推荐，将多个地域同时传入List中即可
List<String> regionList = new ArrayList<>();
regionList.add("cn-shanghai");
regionList.add("cn-hangzhou");
request.setRegions(regionList);
// 设定需要推荐的实例CPU(核数)以及Memory(G),如2核4G;指定核数和内存大小时，由于需要进行排序的实例规格数量较多，推荐结果返回需要一定时间
request.setCores(2);
request.setMemory(4);
// 设定推荐策略，见EnumRecommendationStrategy类，共支持三种推荐策略：库存优先，价格优先，新代产品优先
request.setStrategy(EnumRecommendationStrategy.SUFFICIENT_INVENTORY_FIRST);
// 设定产品类别，见EnumEcsProductCategory类，共支持三种实例类型：企业级、入门级、性能突发型
request.setProductCategory(EnumEcsProductCategory.EnterpriseLevel);
// 获取推荐结果并输出
Response<List<PreemptiveInstanceRecommendation>> recommend = preemptiveInstanceRecommendationService.recommend(request);
if (recommend.getData() == null || recommend.getData().size() == 0){
  System.out.println("Blank Recommendation Response");
  return;
}
System.out.println(JSON.toJSONString(recommend));
System.out.println("------------Recommend List------------");
for (PreemptiveInstanceRecommendation preemptiveInstanceRecommendation : recommend.getData()){
  System.out.println(JSON.toJSONString(preemptiveInstanceRecommendation));
}
System.out.println("----------Recommend List End----------");

```

推荐结果解读

一条示例结果：{"discount":20,"instanceFamilyLevel":"EnterpriseLevel","instanceGeneration":"ecs-5","instanceType":"ecs.c6e.large","instanceTypeFamily":"ecs.c6e","originPrice":0.265,"spotPrice":0.053,"statusCategory":"WithStock","zone":"cn-shanghai-b"}

* discount - 折扣百分数，如：20代表此抢占式实例实际价格为同可用区下同实例规格的按量付费实例的20%
* instanceFamilyLevel - EnterpriseLevel代表企业级实例；EntryLevel代表入门级实例；CreditEntryLevel代表性能突发型实例
* instanceGeneration - 实例产品代数
* instanceType - 实例规格
* instanceTypeFamily - 实例规格族
* originPrice - 同可用区下同实例规格的按量付费实例价格
* spotPrice - 抢占式实例价格
* statusCategory - 库存状态
* zone - 可用区

#EasySDK命令行工具插件 - 抢占式实例推荐

EasySDK命令行工具插件致力于为EasySDK中的插件提供命令行入口，降低接入成本，提升跨语言接入能力，目前已支持调用抢占式实例推荐功能。


## Getting started - 快速开始

1.安装JDK并且配置java环境变量

2.下载jar文件（与此说明文档同路径下）执行命令，配置参数

```shell
# 推荐北京和上海两个地域内的所有az的2核4G企业级实例以库存优先策略进行推荐
java -jar EasySDK-cli-tool-1.1.1-SNAPSHOT.jar --profile /Users/oneuser/Desktop/test.properties -c 2 -m 4 -p 1 -st 2 -r cn-shanghai,cn-beijing -l 15
```

3.参数解释

```
usage: Alibaba Cloud EasySDK [-ak <arg>] [-c <arg>] [-h] [-i <arg>] [-l
       <arg>] [-m <arg>] [-p <arg>] [-profile <arg>] [-r <arg>] [-s <arg>]
       [-st <arg>]
 -ak,--accesskey <arg>        Your aliyun access key
 -c,--cores <arg>             Core number such as 2
 -h,--help                    Print help
 -i,--instanceType <arg>      InstanceType such as ecs.g6.large
 -l,--limit <arg>             Maximum recommendation count, default value
                              is 5
 -m,--memory <arg>            Memory size(G) such as 4
 -p,--productCategory <arg>   ProductCategory; 1 means EnterpriseLevel; 2
                              means EntryLevel; 3 means CreditEntryLevel
 -profile,--profile <arg>     AK/SK file path; Note: it must be a java
                              .properties file, following the standard
                              format
 -r,--regions <arg>           Regions, split by comma, such as
                              cn-shanghai,cn-beijing
 -s,--sk <arg>                Your aliyun secret key
 -st,--strategy <arg>         RecommendStrategy; 1 means
                              LOWEST_PRICE_FIRST; 2 means
                              SUFFICIENT_INVENTORY_FIRST; 3 means
                              LATEST_PRODUCT_FIRST
```

其中，test.properties文件中按照.properties文件格式存储ak和sk

```
accessKey=abcdefg
secretKey=gfedcbasdfsffdsf
```

4.示例输出

```
------------Request Info------------
Regions : [cn-shanghai, cn-beijing]
Cpu Core Count : 2
Memory Size(G) : 4
Instance Type : null
Product Category : EnterpriseLevel
Recommend Strategy : SUFFICIENT_INVENTORY_FIRST
------------Recommend List------------
{"discount":6,"instanceFamilyLevel":"EnterpriseLevel","instanceGeneration":"ecs-2","instanceType":"ecs.sn1.medium","instanceTypeFamily":"ecs.sn1","originPrice":0.348,"spotPrice":0.021,"statusCategory":"WithStock","zone":"cn-shanghai-b"}
{"discount":8,"instanceFamilyLevel":"EnterpriseLevel","instanceGeneration":"ecs-3","instanceType":"ecs.sn1ne.large","instanceTypeFamily":"ecs.sn1ne","originPrice":0.265,"spotPrice":0.022,"statusCategory":"WithStock","zone":"cn-shanghai-d"}
{"discount":8,"instanceFamilyLevel":"EnterpriseLevel","instanceGeneration":"ecs-3","instanceType":"ecs.sn1ne.large","instanceTypeFamily":"ecs.sn1ne","originPrice":0.265,"spotPrice":0.022,"statusCategory":"WithStock","zone":"cn-shanghai-b"}
{"discount":6,"instanceFamilyLevel":"EnterpriseLevel","instanceGeneration":"ecs-3","instanceType":"ecs.sn1ne.large","instanceTypeFamily":"ecs.sn1ne","originPrice":0.265,"spotPrice":0.016,"statusCategory":"WithStock","zone":"cn-beijing-c"}
{"discount":6,"instanceFamilyLevel":"EnterpriseLevel","instanceGeneration":"ecs-3","instanceType":"ecs.sn1ne.large","instanceTypeFamily":"ecs.sn1ne","originPrice":0.265,"spotPrice":0.016,"statusCategory":"WithStock","zone":"cn-beijing-e"}
{"discount":10,"instanceFamilyLevel":"EnterpriseLevel","instanceGeneration":"ecs-4","instanceType":"ecs.c5.large","instanceTypeFamily":"ecs.c5","originPrice":0.241,"spotPrice":0.025,"statusCategory":"WithStock","zone":"cn-beijing-h"}
{"discount":20,"instanceFamilyLevel":"EnterpriseLevel","instanceGeneration":"ecs-6","instanceType":"ecs.hfc7.large","instanceTypeFamily":"ecs.hfc7","originPrice":0.304,"spotPrice":0.061,"statusCategory":"WithStock","zone":"cn-shanghai-b"}
{"discount":20,"instanceFamilyLevel":"EnterpriseLevel","instanceGeneration":"ecs-6","instanceType":"ecs.c6a.large","instanceTypeFamily":"ecs.c6a","originPrice":0.189,"spotPrice":0.038,"statusCategory":"WithStock","zone":"cn-shanghai-b"}
{"discount":10,"instanceFamilyLevel":"EnterpriseLevel","instanceGeneration":"ecs-4","instanceType":"ecs.c5.large","instanceTypeFamily":"ecs.c5","originPrice":0.241,"spotPrice":0.025,"statusCategory":"WithStock","zone":"cn-shanghai-g"}
{"discount":9,"instanceFamilyLevel":"EnterpriseLevel","instanceGeneration":"ecs-4","instanceType":"ecs.c5.large","instanceTypeFamily":"ecs.c5","originPrice":0.241,"spotPrice":0.022,"statusCategory":"WithStock","zone":"cn-shanghai-b"}
{"discount":20,"instanceFamilyLevel":"EnterpriseLevel","instanceGeneration":"ecs-5","instanceType":"ecs.c6.large","instanceTypeFamily":"ecs.c6","originPrice":0.251,"spotPrice":0.051,"statusCategory":"WithStock","zone":"cn-shanghai-g"}
{"discount":18,"instanceFamilyLevel":"EnterpriseLevel","instanceGeneration":"ecs-5","instanceType":"ecs.c6.large","instanceTypeFamily":"ecs.c6","originPrice":0.251,"spotPrice":0.046,"statusCategory":"WithStock","zone":"cn-shanghai-b"}
{"discount":7,"instanceFamilyLevel":"EnterpriseLevel","instanceGeneration":"ecs-3","instanceType":"ecs.sn1ne.large","instanceTypeFamily":"ecs.sn1ne","originPrice":0.265,"spotPrice":0.019,"statusCategory":"WithStock","zone":"cn-shanghai-e"}
----------Recommend List End----------
```


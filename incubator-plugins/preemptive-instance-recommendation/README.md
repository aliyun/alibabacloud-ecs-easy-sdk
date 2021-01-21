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
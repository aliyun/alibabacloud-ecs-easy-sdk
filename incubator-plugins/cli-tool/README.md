# EasySDK CLI TOOL - EasySDK命令行工具插件

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


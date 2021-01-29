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

import com.aliyun.ecs.easysdk.CommandLineToolMain;
import com.aliyun.ecs.easysdk.biz.constants.EnumEcsProductCategory;
import com.aliyun.ecs.easysdk.preemptiveinstance.model.EnumRecommendationStrategy;
import com.google.common.collect.Lists;
import com.aliyun.ecs.easysdk.model.InstanceTypeInfo;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class testCommandLineTool {

    @Test
    public void testGetInstanceTypeInfo() {
        String wrongMemory = "4k";
        String memory = "4";
        String cores = "2";
        List<String> argList = Lists.newArrayList("-c", cores, "-m", memory);
        CommandLine commandLine = getCommandLine(argList);
        Assert.assertNotNull(commandLine);
        InstanceTypeInfo instanceTypeInfo = CommandLineToolMain.getInstanceTypeInfo(commandLine);
        Assert.assertNotNull(instanceTypeInfo);
        Assert.assertEquals(memory, String.valueOf(instanceTypeInfo.getMemory()));
        Assert.assertEquals(cores, String.valueOf(instanceTypeInfo.getCores()));
        Assert.assertEquals(0, instanceTypeInfo.getInstanceType().length());

        argList = Lists.newArrayList("-c", cores, "-m", wrongMemory);
        commandLine = getCommandLine(argList);
        Assert.assertNotNull(commandLine);
        instanceTypeInfo = CommandLineToolMain.getInstanceTypeInfo(commandLine);
        Assert.assertNull(instanceTypeInfo);

        argList = Lists.newArrayList("-c", "-m", memory);
        commandLine = getCommandLine(argList);
        Assert.assertNull(commandLine);

        String instanceType = "instanceType-a";
        argList = Lists.newArrayList("-i", instanceType);
        commandLine = getCommandLine(argList);
        instanceTypeInfo = CommandLineToolMain.getInstanceTypeInfo(commandLine);
        Assert.assertNotNull(commandLine);
        Assert.assertNotNull(instanceTypeInfo);
        Assert.assertEquals(instanceType,instanceTypeInfo.getInstanceType());
        Assert.assertEquals(0, instanceTypeInfo.getMemory());
        Assert.assertEquals(0, instanceTypeInfo.getCores());
    }

    @Test
    public void testGetRegions() {
        String regionA = "region-a";
        String regionB = "region-b";
        List<String> argList = Lists.newArrayList("-r", regionA + "," + regionB);
        CommandLine commandLine = getCommandLine(argList);
        Assert.assertNotNull(commandLine);
        List<String> regions = CommandLineToolMain.getRegions(commandLine);
        Assert.assertNotNull(regions);
        Assert.assertEquals(2, regions.size());
        Assert.assertEquals(regionA, regions.get(0));
        Assert.assertEquals(regionB, regions.get(1));

        argList = Lists.newArrayList("-r");
        commandLine = getCommandLine(argList);
        Assert.assertNull(commandLine);

        argList = Lists.newArrayList("-r", regionA + "." + regionB);
        commandLine = getCommandLine(argList);
        Assert.assertNotNull(commandLine);
        regions = CommandLineToolMain.getRegions(commandLine);
        Assert.assertNotNull(regions);
        Assert.assertEquals(1, regions.size());

    }

    @Test
    public void testGetEcsProductCategory() {
        EnumEcsProductCategory selectedCategory = EnumEcsProductCategory.EnterpriseLevel;
        List<String> argList = Lists.newArrayList("-p", String.valueOf(selectedCategory.ordinal() + 1));
        CommandLine commandLine = getCommandLine(argList);
        Assert.assertNotNull(commandLine);
        EnumEcsProductCategory category = CommandLineToolMain.getEcsProductCategory(commandLine);
        Assert.assertEquals(selectedCategory, category);

        argList = Lists.newArrayList("-p");
        commandLine = getCommandLine(argList);
        Assert.assertNull(commandLine);

        argList = Lists.newArrayList("-p", "-1");
        commandLine = getCommandLine(argList);
        Assert.assertNotNull(commandLine);
        category = CommandLineToolMain.getEcsProductCategory(commandLine);
        Assert.assertNull(category);

    }

    @Test
    public void testGetRecommendationStrategy() {
        EnumRecommendationStrategy selectStrategy = EnumRecommendationStrategy.SUFFICIENT_INVENTORY_FIRST;
        List<String> argList = Lists.newArrayList("-st", String.valueOf(selectStrategy.ordinal() + 1));
        CommandLine commandLine = getCommandLine(argList);
        Assert.assertNotNull(commandLine);
        EnumRecommendationStrategy strategy = CommandLineToolMain.getRecommendationStrategy(commandLine);
        Assert.assertEquals(selectStrategy, strategy);

        argList = Lists.newArrayList("-st");
        commandLine = getCommandLine(argList);
        Assert.assertNull(commandLine);

        argList = Lists.newArrayList("-st", "-1");
        commandLine = getCommandLine(argList);
        Assert.assertNotNull(commandLine);
        strategy = CommandLineToolMain.getRecommendationStrategy(commandLine);
        Assert.assertNull(strategy);
    }

    private CommandLine getCommandLine(List<String> argList) {
        String[] args = new String[argList.size()];
        args = argList.toArray(args);
        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine = null;
        try {
            commandLine = parser.parse(CommandLineToolMain.getOptions(), args);
        } catch (ParseException ignored) {

        }
        return commandLine;
    }

}

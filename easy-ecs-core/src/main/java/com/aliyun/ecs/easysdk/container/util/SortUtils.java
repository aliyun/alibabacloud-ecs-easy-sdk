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
package com.aliyun.ecs.easysdk.container.util;

import com.aliyun.ecs.easysdk.container.meta.SortInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SortUtils {

    public static void sorts(SortInfo[] sortInfos) {

        Map<String, Set<String>> depends = new HashMap<String, Set<String>>();
        Map<String, SortInfo> nameSortInfos = new HashMap<String, SortInfo>();

        // convert before to after
        for (SortInfo sortInfo : sortInfos) {
            if (sortInfo.before != null) {
                for (String name : sortInfo.before) {
                    Set<String> depend = depends.get(name);
                    if (depend == null) {
                        depend = new HashSet<String>();
                        depends.put(name, depend);
                    }
                    depend.add(sortInfo.name);
                }
            }
            Set<String> depend = depends.get(sortInfo.name);
            if (depend == null) {
                depend = new HashSet<String>();
                depends.put(sortInfo.name, depend);
            }
            if (sortInfo.after != null) {
                depend.addAll(sortInfo.after);
            }
            nameSortInfos.put(sortInfo.name, sortInfo);

        }

        // normalize the cascade dependencies
        Set<String> visited = new HashSet<String>();
        for (SortInfo sortInfo : sortInfos) {
            normalize(sortInfo.name, sortInfo.name, visited, depends);
            depends.get(sortInfo.name).addAll(visited);
            visited.clear();
        }

        // Normalize beforeAll/afterAll
        Set<String> beforeAllNames = new HashSet<String>();
        Set<String> afterAllNames = new HashSet<String>();
        for (SortInfo sortInfo : sortInfos) {
            String name = sortInfo.name;
            if (sortInfo.beforeAll != null && sortInfo.beforeAll) {
                beforeAllNames.add(name);
                Set<String> dependsOf = depends.get(name);
                beforeAllNames.addAll(dependsOf);
            } else if (sortInfo.afterAll != null && sortInfo.afterAll) {
                afterAllNames.add(name);
                for (Map.Entry<String, Set<String>> entry : depends.entrySet()) {
                    if (entry.getValue().contains(name)) {
                        afterAllNames.add(entry.getKey());
                    }
                }
            }
        }
        for (Map.Entry<String, Set<String>> entry : depends.entrySet()) {
            if (!beforeAllNames.contains(entry.getKey())) {
                entry.getValue().addAll(beforeAllNames);
            }
        }
        Set<String> otherNames = new HashSet<String>(depends.keySet());
        otherNames.removeAll(afterAllNames);
        for (String name : afterAllNames) {
            Set<String> dependsOf = depends.get(name);
            dependsOf.addAll(otherNames);
        }

        Set<String> addedThisRound = new HashSet<String>();
        Set<String> addedLastRound = new HashSet<String>();
        List<String> orderedPluginNames = new ArrayList<String>(sortInfos.length);
        while (depends.size() > 0) {
            for (Iterator<Map.Entry<String, Set<String>>> it = depends.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, Set<String>> entry = it.next();
                entry.getValue().removeAll(addedLastRound);
                entry.getValue().retainAll(depends.keySet());
                if (entry.getValue().size() == 0) {
                    addedThisRound.add(entry.getKey());
                    orderedPluginNames.add(entry.getKey());
                    it.remove();
                }
            }
            if (addedThisRound.size() == 0) {
                throw new IllegalStateException("Circular found for " + depends);
            }
            addedLastRound.clear();
            addedLastRound.addAll(addedThisRound);
            addedThisRound.clear();
        }

        //orderedPluginNames could have more elements, as some plugins may declare the dependency while
        //the target dependeny is not in the passed sortInfo list
        int sortInfoIndex = 0;
        for (String name : orderedPluginNames) {
            SortInfo sortInfo = nameSortInfos.get(name);
            if (sortInfo != null) {
                sortInfos[sortInfoIndex++] = sortInfo;
            }
        }
    }

    private static void normalize(String startName, String name, Set<String> visited, Map<String, Set<String>> depends) {
        Set<String> dependsOf = depends.get(name);
        if (dependsOf == null || dependsOf.contains(startName)) {
            return;
        }
        for (String dependOf : dependsOf) {
            if (!visited.add(dependOf)) {
                continue;
            }
            normalize(startName, dependOf, visited, depends);
        }
    }
}

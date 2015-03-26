/*
 *  Copyright 2015 Eurocommercial Properties NV
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.app.interactivemap;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.isisaddons.wicket.svg.cpt.applib.Color;

public class ColorMapHelper {

    public static Map<Color, Integer> addToMap(final Map<Color, Integer> map, final Color color) {
        if (map.containsKey(color)) {
            map.put(color, map.get(color) + 1);
        } else {
            map.put(color, 1);
        }
        return map;
    }

    public static List<Color> sortByValue(Map<Color, Integer> map) {
        List<Map.Entry<Color, Integer>> list = new LinkedList<Map.Entry<Color, Integer>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<Color, Integer>>() {
            public int compare(Map.Entry<Color, Integer> o1, Map.Entry<Color, Integer> o2) {
                return o1.getValue().compareTo(o2.getValue()) * -1;
            }
        });

        return Lists.transform(list, mapEntryToColor);
    }

    private static Function<Map.Entry<Color, Integer>, Color> mapEntryToColor =
            new Function<Map.Entry<Color, Integer>, Color>() {
                public Color apply(Map.Entry<Color, Integer> entry) {
                    return entry.getKey();
                }
            };

}

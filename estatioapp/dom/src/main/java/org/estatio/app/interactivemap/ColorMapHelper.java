package org.estatio.app.interactivemap;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

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

package com.eurocommercialproperties.estatio.dom.utils;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

public class StringUtils {
	
	private StringUtils() {}
	
    private static Function<String, String> LOWER_CASE_THEN_CAPITALIZE = new Function<String, String>() {
        @Override
        public String apply(String input) {
            return StringUtils.capitalize(input.toLowerCase());
        }
    };

    private static Function<String, String> UPPER_CASE = new Function<String, String>() {
        @Override
        public String apply(String input) {
            return input.toUpperCase();
        }
    };

	
    public static String enumTitle(String string) {
        return Joiner.on(" ").join(Iterables.transform(Splitter.on("_").split(string), LOWER_CASE_THEN_CAPITALIZE));
    }

    public static String enumDeTitle(String string) {
        return Joiner.on("_").join(Iterables.transform(Splitter.on(" ").split(string), UPPER_CASE));
    }

    public static String wildcardToRegex(String pattern)
    {
        return pattern.replace("*", ".*").replace("?", ".");
    }    

    private static String capitalize(final String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        if (str.length() == 1) {
            return str.toUpperCase();
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }


}

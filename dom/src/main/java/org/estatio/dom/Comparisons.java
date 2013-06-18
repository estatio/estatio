package org.estatio.dom;

import java.lang.reflect.Method;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;


public class Comparisons {
    
    private static Function<String,String> TRIM = new Function<String,String>() {
        @Override
        public String apply(String input) {
            return input!=null?input.trim():null;
        }
    };

    private Comparisons() {}

    public static <T> int compare(T p, T q, String propertyNames) {
        final Iterable<String> propertyNameIter = Splitter.on(',').split(propertyNames);
        ComparisonChain chain = ComparisonChain.start();
        for (String propertyName : Iterables.transform(propertyNameIter, TRIM)) {
            chain = chain.compare(getPropertyValue(p, propertyName), getPropertyValue(q, propertyName), Ordering.natural().nullsFirst());
        }
        return chain.result();
    }
    
    @SuppressWarnings("rawtypes")
    private static Comparable getPropertyValue(Object obj, String propertyName) {
        String methodName = buildMethodName(propertyName);
        try {
            final Method getterMethod = obj.getClass().getMethod(methodName);
            return (Comparable) getterMethod.invoke(obj);
        } catch (Exception e) {
            throw new IllegalArgumentException("No such method ' " + methodName + "'", e);
        }
    }

    private static String buildMethodName(String propertyName) {
        return "get" + upperFirst(propertyName);
    }

    private static String upperFirst(final String str) {
        if (Strings.isNullOrEmpty(str)) {
            return str;
        }
        if (str.length() == 1) {
            return str.toUpperCase();
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }


}

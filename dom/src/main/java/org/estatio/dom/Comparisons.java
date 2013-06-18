package org.estatio.dom;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;


public class Comparisons {
    
    static class OrderClause {
        private static Pattern pattern = Pattern.compile("\\W*(\\w+)\\W*(asc|desc)?\\W*");
        enum Direction {
            ASC {
                @Override
                public Comparator<Comparable<?>> getOrdering() {
                    return Ordering.natural().nullsFirst();
                }
            }, 
            DESC {
                @Override
                public Comparator<Comparable<?>> getOrdering() {
                    return Ordering.natural().nullsLast().reverse();
                }
            };

            public abstract Comparator<Comparable<?>> getOrdering();
            
            public static Direction valueOfElseAsc(String str) {
                return str!=null?valueOf(str.toUpperCase()):ASC;
            }
        }
        private String propertyName;
        private Direction direction;
        static OrderClause parse(String input) {
            final Matcher matcher = pattern.matcher(input);
            if(!matcher.matches()) {
                return null;
            }
            return new OrderClause(matcher.group(1), Direction.valueOfElseAsc(matcher.group(2)));
        }
        OrderClause(String propertyName, Direction direction) {
            this.propertyName = propertyName;
            this.direction = direction;
        }
        String getPropertyName() {
            return propertyName;
        }
        Direction getDirection() {
            return direction;
        }
        @SuppressWarnings("unchecked" )
        public <T> Comparable<T> getValueOf(Object obj) {
            String methodName = buildMethodName(propertyName);
            try {
                final Method getterMethod = obj.getClass().getMethod(methodName);
                return (Comparable<T>) getterMethod.invoke(obj);
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
    
    private static Function<String,OrderClause> OrderClause_parse = new Function<String,OrderClause>() {
        @Override
        public OrderClause apply(String input) {
            return OrderClause.parse(input);
        }
    };

    private Comparisons() {}

    public static <T> int compare(T p, T q, String propertyNames) {
        final Iterable<String> propertyNameIter = Splitter.on(',').split(propertyNames);
        ComparisonChain chain = ComparisonChain.start();
        for (OrderClause orderClause : Iterables.transform(propertyNameIter, OrderClause_parse)) {
            final Comparable<T> propertyValue = orderClause.getValueOf(p);
            final Comparable<T> propertyValue2 = orderClause.getValueOf(q);
            chain = chain.compare(propertyValue, propertyValue2, orderClause.getDirection().getOrdering());
        }
        return chain.result();
    }
}

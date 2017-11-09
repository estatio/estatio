package org.estatio.module.base.platform.applib;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import org.apache.isis.applib.util.ObjectContracts;

// TODO: move back into isis (this is a copy of ObjectContracts in isis' applib, enhanced to deal with "cross-type comparisons", to allow task#object to be sorted)
public class ObjectContracts2 {
    private final List<ObjectContracts.ToStringEvaluator> evaluators = Lists.newArrayList();

    public ObjectContracts2() {
    }

    /** @deprecated */
    @Deprecated
    public static <T> int compare(T p, T q, String propertyNames) {
        Iterable<String> propertyNamesIter = csvToIterable(propertyNames);
        return compare(p, q, propertyNamesIter);
    }

    /** @deprecated */
    @Deprecated
    public static <T> int compare(T p, T q, String... propertyNames) {
        Iterable<String> propertyNamesIter = varargsToIterable(propertyNames);
        return compare(p, q, (Iterable)propertyNamesIter);
    }

    private static <T> int compare(T p, T q, Iterable<String> propertyNamesIter) {
        if(p == null) { return -1;}
        if(q == null) { return +1;}
        if(p.getClass() != q.getClass()) {
            // just sort on the class type
            return Ordering.natural().onResultOf(x -> x.getClass().getSimpleName()).compare(p, q);
        }

        Iterable<Clause> clauses = clausesFor(propertyNamesIter);
        ComparisonChain chain = ComparisonChain.start();

        Clause clause;
        Comparable propertyValueOfP;
        Comparable propertyValueOfQ;
        for(Iterator var5 = clauses.iterator(); var5.hasNext(); chain = chain.compare(propertyValueOfP, propertyValueOfQ, clause.getDirection().getOrdering())) {
            clause = (Clause)var5.next();
            propertyValueOfP = (Comparable)clause.getValueOf(p);
            propertyValueOfQ = (Comparable)clause.getValueOf(q);
        }

        return chain.result();
    }

    /** @deprecated */
    @Deprecated
    public static <T> Comparator<T> compareBy(final String propertyNames) {
        return new Comparator<T>() {
            public int compare(T p, T q) {
                return ObjectContracts.compare(p, q, propertyNames);
            }
        };
    }

    /** @deprecated */
    @Deprecated
    public static <T> Comparator<T> compareBy(final String... propertyNames) {
        return new Comparator<T>() {
            public int compare(T p, T q) {
                return ObjectContracts.compare(p, q, propertyNames);
            }
        };
    }

    /** @deprecated */
    @Deprecated
    public static String toString(Object p, String propertyNames) {
        return (new ObjectContracts()).toStringOf(p, propertyNames);
    }

    /** @deprecated */
    @Deprecated
    public static String toString(Object p, String... propertyNames) {
        return (new ObjectContracts()).toStringOf(p, propertyNames);
    }

    /** @deprecated */
    @Deprecated
    public static int hashCode(Object obj, String propertyNames) {
        Iterable<String> propertyNamesIter = csvToIterable(propertyNames);
        return hashCode(obj, propertyNamesIter);
    }

    /** @deprecated */
    @Deprecated
    public static int hashCode(Object obj, String... propertyNames) {
        Iterable<String> propertyNamesIter = varargsToIterable(propertyNames);
        return hashCode(obj, (Iterable)propertyNamesIter);
    }

    private static int hashCode(Object obj, Iterable<String> propertyNamesIter) {
        List<Object> propertyValues = Lists.newArrayList();
        Iterator var3 = clausesFor(propertyNamesIter).iterator();

        while(var3.hasNext()) {
            Clause clause = (Clause)var3.next();
            Object propertyValue = clause.getValueOf(obj);
            if (propertyValue != null) {
                propertyValues.add(propertyValue);
            }
        }

        return Objects.hashCode(propertyValues.toArray());
    }

    /** @deprecated */
    @Deprecated
    public static boolean equals(Object p, Object q, String propertyNames) {
        if (p == null && q == null) {
            return true;
        } else if (p != null && q != null) {
            if (p.getClass() != q.getClass()) {
                return false;
            } else {
                Iterable<String> propertyNamesIter = csvToIterable(propertyNames);
                return equals(p, q, propertyNamesIter);
            }
        } else {
            return false;
        }
    }

    /** @deprecated */
    @Deprecated
    public static boolean equals(Object p, Object q, String... propertyNames) {
        if (p == null && q == null) {
            return true;
        } else if (p != null && q != null) {
            if (p.getClass() != q.getClass()) {
                return false;
            } else {
                Iterable<String> propertyNamesIter = varargsToIterable(propertyNames);
                return equals(p, q, (Iterable)propertyNamesIter);
            }
        } else {
            return false;
        }
    }

    private static boolean equals(Object p, Object q, Iterable<String> propertyNamesIter) {
        Iterable<Clause> clauses = clausesFor(propertyNamesIter);
        Iterator var4 = clauses.iterator();

        Object pValue;
        Object qValue;
        do {
            if (!var4.hasNext()) {
                return true;
            }

            Clause clause = (Clause)var4.next();
            pValue = clause.getValueOf(p);
            qValue = clause.getValueOf(q);
        } while(Objects.equal(pValue, qValue));

        return false;
    }

    private static Iterable<Clause> clausesFor(Iterable<String> iterable) {
        return Iterables.transform(iterable, new Function<String, Clause>() {
            public Clause apply(String input) {
                return Clause.parse(input);
            }
        });
    }

    private static Iterable<String> csvToIterable(String propertyNames) {
        return Splitter.on(',').split(propertyNames);
    }

    private static List<String> varargsToIterable(String[] iterable) {
        return Arrays.asList(iterable);
    }

    public ObjectContracts2 with(ObjectContracts.ToStringEvaluator evaluator) {
        this.evaluators.add(evaluator);
        return this;
    }

    /** @deprecated */
    @Deprecated
    public String toStringOf(Object p, String propertyNames) {
        Iterable<String> propertyNamesIter = csvToIterable(propertyNames);
        return this.toStringOf(p, propertyNamesIter);
    }

    /** @deprecated */
    @Deprecated
    public String toStringOf(Object p, String... propertyNames) {
        Iterable<String> propertyNamesIter = varargsToIterable(propertyNames);
        return this.toStringOf(p, (Iterable)propertyNamesIter);
    }

    private String toStringOf(Object p, Iterable<String> propertyNamesIter) {
        Objects.ToStringHelper stringHelper = Objects.toStringHelper(p);
        Iterator var4 = clausesFor(propertyNamesIter).iterator();

        while(var4.hasNext()) {
            Clause clause = (Clause)var4.next();
            stringHelper.add(clause.getPropertyName(), this.asString(clause, p));
        }

        return stringHelper.toString();
    }

    private String asString(Clause clause, Object p) {
        Object value = clause.getValueOf(p);
        if (value == null) {
            return null;
        } else {
            Iterator var4 = this.evaluators.iterator();

            ObjectContracts.ToStringEvaluator evaluator;
            do {
                if (!var4.hasNext()) {
                    return value.toString();
                }

                evaluator = (ObjectContracts.ToStringEvaluator)var4.next();
            } while(!evaluator.canEvaluate(value));

            return evaluator.evaluate(value);
        }
    }

    public interface ToStringEvaluator {
        boolean canEvaluate(Object var1);

        String evaluate(Object var1);
    }
}

// copy of Isis' applib Clause
class Clause {
    private static Pattern pattern = Pattern.compile("\\W*(\\w+)\\W*(asc|asc nullsFirst|asc nullsLast|desc|desc nullsFirst|desc nullsLast)?\\W*");
    private String propertyName;
    private Clause.Direction direction;

    static Clause parse(String input) {
        Matcher matcher = pattern.matcher(input);
        return !matcher.matches() ? null : new Clause(matcher.group(1), Clause.Direction.valueOfElseAsc(matcher.group(2)));
    }

    Clause(String propertyName, Clause.Direction direction) {
        this.propertyName = propertyName;
        this.direction = direction;
    }

    String getPropertyName() {
        return this.propertyName;
    }

    Clause.Direction getDirection() {
        return this.direction;
    }

    public Object getValueOf(Object obj) {
        if (obj == null) {
            return null;
        } else {
            String methodNameSuffix = upperFirst(this.propertyName);
            String getMethodName = "get" + methodNameSuffix;

            try {
                Method getterMethod = obj.getClass().getMethod(getMethodName);
                return getterMethod.invoke(obj);
            } catch (NoSuchMethodException var9) {
                String isMethodName = "is" + methodNameSuffix;

                try {
                    Method getterMethod = obj.getClass().getMethod(isMethodName);
                    return getterMethod.invoke(obj);
                } catch (NoSuchMethodException var7) {
                    throw new IllegalArgumentException("No such method ' " + getMethodName + "' or '" + isMethodName + "'", var9);
                } catch (Exception var8) {
                    throw new RuntimeException(var8);
                }
            } catch (Exception var10) {
                throw new RuntimeException(var10);
            }
        }
    }

    private static String upperFirst(String str) {
        if (Strings.isNullOrEmpty(str)) {
            return str;
        } else {
            return str.length() == 1 ? str.toUpperCase() : str.substring(0, 1).toUpperCase() + str.substring(1);
        }
    }

    static enum Direction {
        ASC {
            public Comparator<Comparable<?>> getOrdering() {
                return Ordering.natural().nullsFirst();
            }
        },
        ASC_NULLS_LAST {
            public Comparator<Comparable<?>> getOrdering() {
                return Ordering.natural().nullsLast();
            }
        },
        DESC {
            public Comparator<Comparable<?>> getOrdering() {
                return Ordering.natural().nullsLast().reverse();
            }
        },
        DESC_NULLS_LAST {
            public Comparator<Comparable<?>> getOrdering() {
                return Ordering.natural().nullsFirst().reverse();
            }
        };

        private Direction() {
        }

        public abstract Comparator<Comparable<?>> getOrdering();

        public static Clause.Direction valueOfElseAsc(String str) {
            if ("asc".equals(str)) {
                return ASC;
            } else if ("asc nullsFirst".equals(str)) {
                return ASC;
            } else if ("asc nullsLast".equals(str)) {
                return ASC_NULLS_LAST;
            } else if ("desc".equals(str)) {
                return DESC;
            } else if ("desc nullsFirst".equals(str)) {
                return DESC;
            } else {
                return "desc nullsLast".equals(str) ? DESC_NULLS_LAST : ASC;
            }
        }
    }
}

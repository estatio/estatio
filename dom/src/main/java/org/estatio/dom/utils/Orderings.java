package org.estatio.dom.utils;

import com.google.common.collect.Ordering;

import org.joda.time.LocalDate;

public final class Orderings {
    
    public static final Ordering<LocalDate> LOCAL_DATE_NATURAL_NULLS_FIRST = Ordering.<LocalDate>natural().nullsFirst();
    public static final Ordering<LocalDate> LOCAL_DATE_NATURAL_REVERSED = Ordering.<LocalDate>natural().reverse();

    public static final <T> Ordering<T> classCanonicalName() {
        return new Ordering<T>() { 
            public int compare(T p, T q) {
                return Ordering.<String> natural().compare(p.getClass().getCanonicalName(), q.getClass().getCanonicalName());
            }
        };
    }
    
    private Orderings(){}

}

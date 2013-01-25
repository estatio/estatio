package org.estatio.dom.utils;

import com.google.common.collect.Ordering;

import org.joda.time.LocalDate;

public final class Orderings {
    
    public static final Ordering<LocalDate> lOCAL_DATE_NATURAL_NULLS_FIRST = Ordering.<LocalDate>natural().nullsFirst();

    private Orderings(){}

}

package org.estatio.dom.utils;

import java.util.Iterator;


public class ValueUtils {
    
    private ValueUtils(){}

    /**
     * First non-null value.
     */
    public static <T> T coalesce(T eitherThis, T orThat) {
        return eitherThis != null ? eitherThis : orThat;
    }

    public static <T> T firstElseNull(Iterable<T> iterable) {
        Iterator<T> iterator = iterable.iterator();
        return iterator.hasNext() ? iterator.next() : null;
    }

}

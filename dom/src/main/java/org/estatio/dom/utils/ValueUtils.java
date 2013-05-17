package org.estatio.dom.utils;


public class ValueUtils {
    
    private ValueUtils(){}

    /**
     * First non-null value.
     */
    public static <T> T coalesce(T eitherThis, T orThat) {
        return eitherThis != null ? eitherThis : orThat;
    }

}

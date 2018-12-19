package org.estatio.module.coda.dom.doc;

import java.util.Arrays;

public enum Location {
    BOOKS,
    INTRAY,
    ;

    public static Location lookup(final String value) {
        return Arrays.stream(values())
                .filter(location -> location.name().equalsIgnoreCase(value))
                .findFirst()
                .orElse(null);
    }

}

package org.estatio.module.coda.dom.doc;

public enum LineType {
    SUMMARY,
    ANALYSIS,
    // not adding TAX yet until we find we need it.
    ;

    public static LineType lookup(final String value) {
        for (final LineType lineType : values()) {
            if(lineType.name().equalsIgnoreCase(value)) {
                return lineType;
            }
        }
        return null;
    }
}

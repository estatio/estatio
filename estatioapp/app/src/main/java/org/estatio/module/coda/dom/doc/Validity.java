package org.estatio.module.coda.dom.doc;

public enum Validity {
    VALID("Valid", "valid "),
    NOT_VALID("Not valid", "not valid "),
    BOTH("Both valid & not valid", ""),
    ;

    private final String title;
    private final String str;

    Validity(final String title, final String str) {
        this.title = title;
        this.str = str;
    }
    public String title() {
        return title;
    }
    public String toString() {
        return title;
    }

    public String asStr() {
        return str;
    }
}

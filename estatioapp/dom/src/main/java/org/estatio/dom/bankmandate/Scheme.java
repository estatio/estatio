package org.estatio.dom.bankmandate;


public enum Scheme {

    CORE(org.estatio.canonical.bankmandate.v1.Scheme.CORE),
    B2B(org.estatio.canonical.bankmandate.v1.Scheme.B2B);

    private final org.estatio.canonical.bankmandate.v1.Scheme scheme;

    Scheme(final org.estatio.canonical.bankmandate.v1.Scheme scheme) {
        this.scheme = scheme;
    }

    public org.estatio.canonical.bankmandate.v1.Scheme forDto() {
        return this.scheme;
    }

}

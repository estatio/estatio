package org.estatio.agreement;

public enum AgreementType {

    DEFAULT("Default");

    private final String title;

    private AgreementType(String title) {
        this.title = title;
    }

    public String title() {
        return title;
    }

}

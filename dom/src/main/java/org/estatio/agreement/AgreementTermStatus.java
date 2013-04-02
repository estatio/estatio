package org.estatio.agreement;

public enum AgreementTermStatus {

    APPROVED("Approved"), 
    NEW("New");

    private final String title;

    private AgreementTermStatus(String title) {
        this.title = title;
    }

    public String title() {
        return title;
    }

 }

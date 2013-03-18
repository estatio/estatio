package org.estatio.dom.lease;

public enum LeaseStatus {

    APPROVED("Approved"), NEW("New");

    private final String title;

    private LeaseStatus(String title) {
        this.title = title;
    }

    public String title() {
        return title;
    }

 }

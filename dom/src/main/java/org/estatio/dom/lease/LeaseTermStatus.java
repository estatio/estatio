package org.estatio.dom.lease;

public enum LeaseTermStatus {

    APPROVED("Approved"), CONCEPT("Concept");

    private final String title;

    private LeaseTermStatus(String title) {
        this.title = title;
    }

    public String title() {
        return title;
    }

 }

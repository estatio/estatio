package org.estatio.dom.lease;

public enum LeaseItemStatus {

    APPROVED("Approved"), 
    NEW("New");

    private final String title;

    private LeaseItemStatus(String title) {
        this.title = title;
    }

    public String title() {
        return title;
    }

 }

package com.eurocommercialproperties.estatio.dom.lease;

public enum LeaseItemStatus {

    APPROVED("Approved"), CONCEPT("Concept");

    private final String title;

    private LeaseItemStatus(String title) {
        this.title = title;
    }

    public String title() {
        return title;
    }

 }

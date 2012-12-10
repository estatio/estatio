package com.eurocommercialproperties.estatio.dom.lease;

public enum LeaseStatus {

    APPROVED("Approved"), CONCEPT("Concept");

    private final String title;

    private LeaseStatus(String title) {
        this.title = title;
    }

    public String title() {
        return title;
    }

 }

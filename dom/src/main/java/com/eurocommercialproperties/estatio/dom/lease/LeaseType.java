package com.eurocommercialproperties.estatio.dom.lease;

public enum LeaseType {

    UNIT_LEASE("Unit Lease"), BUSINESS_LEASE("Business Lease");

    private final String title;

    private LeaseType(String title) {
        this.title = title;
    }

    public String title() {
        return title;
    }
    
    //TODO: Handle localised titles.
    
}

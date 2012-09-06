package com.eurocommercialproperties.estatio.dom.lease;

public enum LeaseItemType {

    RENT("Rent"), DISCOUNT("Discount"), SERVICE_CHARGE("Service Charge"), OTHER("Other");

    private final String title;

    private LeaseItemType(String title) {
        this.title = title;
    }

    public String title() {
        return title;
    }
    
    //TODO: Handle localised titles. Maybe this should go into an entity when we need more attributes.
    
}

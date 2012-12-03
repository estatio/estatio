package com.eurocommercialproperties.estatio.dom.lease;

public enum LeaseActorType {

    TENANT("Tenant"), 
    LANDLORD("Landlord"),
    MANAGER("Manager");

    private final String title;

    private LeaseActorType(String title) {
        this.title = title;
    }

    public String title() {
        return title;
    }

}

package com.eurocommercialproperties.estatio.dom.lease;

public enum LeaseActorType {

    TENTANT("Tenant"), 
    LANDLORD("Landlord");

    private final String title;

    private LeaseActorType(String title) {
        this.title = title;
    }

    public String title() {
        return title;
    }

}

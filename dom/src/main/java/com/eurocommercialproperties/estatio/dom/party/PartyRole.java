package com.eurocommercialproperties.estatio.dom.party;

/**
 * 
 * 
 * @version $Rev$ $Date$
 */
public enum PartyRole {

    OWNER("Owner"), TENANT("Tenant"), LANDLORD("Landlord");

    private final String title;

    PartyRole(String title) {
        this.title = title;
    }

    public String title() {
        return title;
    }

}

package org.estatio.dom.party;

/**
 * 
 * 
 * @version $Rev$ $Date$
 */
public enum PartyRoleType {

    OWNER("Owner"), TENANT("Tenant"), LANDLORD("Landlord");

    private final String title;

    PartyRoleType(String title) {
        this.title = title;
    }

    public String title() {
        return title;
    }

}

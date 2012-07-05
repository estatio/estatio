package com.eurocommercialproperties.estatio.dom.party;

/**
 * 
 *
 * @version $Rev$ $Date$
 */
public enum PartyRole {
	
	OWNER ("Owner"),
	TENANT ("Tenant"),
	LANDLORD ("Landlord");
	
	private final String description;
	
	    PartyRole(String description) {
	        this.description = description;
	    }
	    public String description()   { return description; }

}

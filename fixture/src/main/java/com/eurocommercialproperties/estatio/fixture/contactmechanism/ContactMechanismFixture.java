package com.eurocommercialproperties.estatio.fixture.contactmechanism;


import org.apache.isis.applib.fixtures.AbstractFixture;

import com.eurocommercialproperties.estatio.dom.contactmechanism.ContactMechanism;
import com.eurocommercialproperties.estatio.dom.contactmechanism.ContactMechanismType;
import com.eurocommercialproperties.estatio.dom.contactmechanism.PostalAddress;
import com.eurocommercialproperties.estatio.dom.contactmechanism.PostalAddresses;
import com.eurocommercialproperties.estatio.dom.geography.Countries;
import com.eurocommercialproperties.estatio.dom.geography.Country;
import com.eurocommercialproperties.estatio.dom.geography.State;
import com.eurocommercialproperties.estatio.dom.geography.States;


public class ContactMechanismFixture extends AbstractFixture {

    @Override
    public void install() {
    	ContactMechanismType postalAddressContactMechanismType = createContactMechanismType(PostalAddress.class); 
    	
    	Country country1 = countries.newCountry("NL", "Netherlands");
    	State state1 = states.newState("NL-NH", "Noord-Holland", country1);  
    	PostalAddress pa1 = postalAddresses.newPostalAddress("Street", "", "Amsterdam", country1, state1);
    	
    	Country country2 = countries.newCountry("UK", "United Kingdom");
    	State state2 = states.newState("GB-OXF", "Oxfordshire", country2);  
    	PostalAddress pa2 = postalAddresses.newPostalAddress("Street", null, "Oxford", country2, state2);
    	
    }
    
    private ContactMechanismType createContactMechanismType(Class<? extends ContactMechanism> subclass) { 
    	ContactMechanismType contactMechanismType = newTransientInstance(ContactMechanismType.class);
    	contactMechanismType.setFullyQualifiedClassName(subclass.getName());
    	persist(contactMechanismType);
    	return contactMechanismType; 
    	}
    
    
    private PostalAddresses postalAddresses;
    
    public void setPostalAddressRepository(final PostalAddresses postalAddresses) {
        this.postalAddresses = postalAddresses;
    }
    
    private Countries countries;

    public void setCountryRepository(final Countries countries) {
        this.countries = countries;
    }

    private States states;

    public void setStateRepository(final States states) {
        this.states = states;
    }

    
}



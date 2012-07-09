package com.eurocommercialproperties.estatio.fixture.asset;

import org.apache.isis.applib.fixtures.AbstractFixture;
import org.apache.isis.applib.value.Date;

import com.eurocommercialproperties.estatio.dom.asset.Properties;
import com.eurocommercialproperties.estatio.dom.asset.Property;
import com.eurocommercialproperties.estatio.dom.asset.PropertyType;
import com.eurocommercialproperties.estatio.dom.communicationchannel.CommunicationChannels;
import com.eurocommercialproperties.estatio.dom.geography.Countries;
import com.eurocommercialproperties.estatio.dom.geography.States;
import com.eurocommercialproperties.estatio.dom.party.Owner;
import com.eurocommercialproperties.estatio.dom.party.Owners;

public class PropertiesAndUnitsFixture extends AbstractFixture {

    @Override
    public void install() {
        createPropertyAndUnits("KAL", "Kalvertoren", PropertyType.COMMERCIAL, 4, new Date(2003, 12, 1), new Date(2003, 12, 1), owners.findByReference("ACME"));
        Property prop1 = createPropertyAndUnits("OXF", "Oxford", PropertyType.COMMERCIAL, 3, new Date(1999, 1, 1), new Date(2008, 6, 1), owners.findByReference("HELLOWORLD"));
        prop1.addCommunicationChannel(communicationChannels.newPostalAddress("1 Market Street", null, "OX1 3HL", "Oxford", states.findByReference("GB-OXF"),countries.findByReference("GBR")));
        prop1.addCommunicationChannel(communicationChannels.newPhoneNumber("+46123456789"));
        prop1.addCommunicationChannel(communicationChannels.newFaxNumber("+46987654321"));
        prop1.addCommunicationChannel(communicationChannels.newEmailAddress("info@oxford.example.com"));
        
    }
        
    private Property createPropertyAndUnits(final String reference, String name, PropertyType type, int numberOfUnits, Date openingDate, Date acquireDate, Owner owner) {
        Property property = properties.newProperty(reference, name, type);
        property.setOpeningDate(openingDate);
        property.setAcquireDate(acquireDate);
        property.addOwner(owner);
        for (int i = 0; i < numberOfUnits; i++) {
            int unitNumber = i + 1;
            property.newUnit(String.format("%s-%03d", reference, unitNumber), "Unit " + unitNumber).setArea(new Double((i+1)*100));
        }
        return property;
    }

    private States states;

    public void setStateRepository(final States states) {
        this.states = states;
    }

    private Countries countries;

    public void setCountryRepository(final Countries countries) {
        this.countries = countries;
    }

    private Properties properties;

    public void setPropertyRepository(final Properties properties) {
        this.properties = properties;
    }
    
    private Owners owners;
    
    public void setOwnerRepository(final Owners owners){
        this.owners = owners;
    }
    
    private CommunicationChannels communicationChannels;
    
    public void setCommunicationChannelsRepository(final CommunicationChannels communicationChannels) {
        this.communicationChannels = communicationChannels;
    }
    
    
    
}

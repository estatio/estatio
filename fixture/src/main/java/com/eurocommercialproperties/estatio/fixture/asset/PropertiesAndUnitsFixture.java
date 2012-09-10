package com.eurocommercialproperties.estatio.fixture.asset;

import java.math.BigDecimal;

import org.apache.isis.applib.fixtures.AbstractFixture;
import org.joda.time.LocalDate;

import com.eurocommercialproperties.estatio.dom.asset.Properties;
import com.eurocommercialproperties.estatio.dom.asset.Property;
import com.eurocommercialproperties.estatio.dom.asset.PropertyActorType;
import com.eurocommercialproperties.estatio.dom.asset.PropertyType;
import com.eurocommercialproperties.estatio.dom.communicationchannel.CommunicationChannel;
import com.eurocommercialproperties.estatio.dom.communicationchannel.CommunicationChannels;
import com.eurocommercialproperties.estatio.dom.geography.Countries;
import com.eurocommercialproperties.estatio.dom.geography.Country;
import com.eurocommercialproperties.estatio.dom.geography.State;
import com.eurocommercialproperties.estatio.dom.geography.States;
import com.eurocommercialproperties.estatio.dom.party.Parties;
import com.eurocommercialproperties.estatio.dom.party.Party;

public class PropertiesAndUnitsFixture extends AbstractFixture {

    @Override
    public void install() {
        Property prop1 = createPropertyAndUnits("OXF", "Oxford Super Mall", PropertyType.SHOPPING_CENTER, 3, new LocalDate(1999, 1, 1), new LocalDate(2008, 6, 1), parties.findOrganisationByReference("HELLOWORLD"));
        State state = states.findByReference("GB-OXF");
        Country country = countries.findByReference("GBR");
        prop1.addCommunicationChannel((CommunicationChannel) communicationChannels.newPostalAddress("1 Market Street", null, "OX1 3HL", "Oxford", state, country));
        prop1.addCommunicationChannel((CommunicationChannel) communicationChannels.newPhoneNumber("+44 123 456789"));
        prop1.addCommunicationChannel((CommunicationChannel) communicationChannels.newFaxNumber("+44 987 654321"));
        prop1.addCommunicationChannel((CommunicationChannel) communicationChannels.newEmailAddress("info@oxford.example.com"));

        Property prop2 = createPropertyAndUnits("KAL", "Winkelcentrum Kalvertoren", PropertyType.SHOPPING_CENTER, 4, new LocalDate(2003, 12, 1), new LocalDate(2003, 12, 1), parties.findOrganisationByReference("ACME"));
        Country c2 = countries.findByReference("NLD");
        State s2 = states.findByReference("NL-NH");
        
        prop2.addCommunicationChannel((CommunicationChannel) communicationChannels.newPostalAddress("Kalverstraat 12", null, "1017 AA", "Amsterdam", s2, c2));
        prop2.addCommunicationChannel((CommunicationChannel) communicationChannels.newPhoneNumber("+31 123 456789"));
        prop2.addCommunicationChannel((CommunicationChannel) communicationChannels.newFaxNumber("+31 987 654321"));
        prop2.addCommunicationChannel((CommunicationChannel) communicationChannels.newEmailAddress("info@kalvertoren.example.com"));
    }

    private Property createPropertyAndUnits(final String reference, String name, PropertyType type, int numberOfUnits, LocalDate openingDate, LocalDate acquireDate, Party owner) {
        Property property = properties.newProperty(reference, name, type);
        property.setOpeningDate(openingDate);
        property.setAcquireDate(acquireDate);
        property.addActor(owner, PropertyActorType.PROPERTY_OWNER, new LocalDate(1999, 1, 1), new LocalDate(2000, 1, 1));
        for (int i = 0; i < numberOfUnits; i++) {
            int unitNumber = i + 1;
            property.newUnit(String.format("%s-%03d", reference, unitNumber), "Unit " + unitNumber).setArea(new BigDecimal((i + 1) * 100));
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

    private Parties parties;

    public void setpartyRepository(final Parties parties) {
        this.parties = parties;
    }

    private CommunicationChannels communicationChannels;

    public void setCommunicationChannelsRepository(final CommunicationChannels communicationChannels) {
        this.communicationChannels = communicationChannels;
    }

}

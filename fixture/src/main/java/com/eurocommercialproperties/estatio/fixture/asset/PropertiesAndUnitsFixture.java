package com.eurocommercialproperties.estatio.fixture.asset;

import com.eurocommercialproperties.estatio.dom.asset.Properties;
import com.eurocommercialproperties.estatio.dom.asset.Property;
import com.eurocommercialproperties.estatio.dom.asset.PropertyType;
import com.eurocommercialproperties.estatio.dom.communicationchannel.CommunicationChannels;
import com.eurocommercialproperties.estatio.dom.communicationchannel.PostalAddress;
import com.eurocommercialproperties.estatio.dom.geography.Countries;
import com.eurocommercialproperties.estatio.dom.geography.Country;
import com.eurocommercialproperties.estatio.dom.geography.State;
import com.eurocommercialproperties.estatio.dom.geography.States;
import com.eurocommercialproperties.estatio.dom.party.Owner;
import com.eurocommercialproperties.estatio.dom.party.Owners;

import org.apache.isis.applib.fixtures.AbstractFixture;
import org.apache.isis.applib.value.Date;

public class PropertiesAndUnitsFixture extends AbstractFixture {

    @Override
    public void install() {
        Property prop1 = createPropertyAndUnits("OXF", "Oxford Super Mall", PropertyType.COMMERCIAL, 3, new Date(1999,
                        1, 1), new Date(2008, 6, 1), owners.findByReference("HELLOWORLD"));
        State state = states.findByReference("GB-OXF");
		Country country = countries.findByReference("GBR");
		PostalAddress newPostalAddress = communicationChannels.newPostalAddress("1 Market Street", null, "OX1 3HL",
                        "Oxford", state, country);
		prop1.addCommunicationChannel(newPostalAddress);
        prop1.addCommunicationChannel(communicationChannels.newPhoneNumber("+44 123 456789"));
        prop1.addCommunicationChannel(communicationChannels.newFaxNumber("+44 987 654321"));
        prop1.addCommunicationChannel(communicationChannels.newEmailAddress("info@oxford.example.com"));

        Property prop2 = createPropertyAndUnits("KAL", "Winkelcentrum Kalvertoren", PropertyType.COMMERCIAL, 4,
                        new Date(2003, 12, 1), new Date(2003, 12, 1), owners.findByReference("ACME"));
        prop2.addCommunicationChannel(communicationChannels.newPostalAddress("Kalverstraat 12", null, "1017 AA",
                        "Amsterdam", states.findByReference("NL-NH"), countries.findByReference("NLD")));
        prop2.addCommunicationChannel(communicationChannels.newPhoneNumber("+31 123 456789"));
        prop2.addCommunicationChannel(communicationChannels.newFaxNumber("+31 987 654321"));
        prop2.addCommunicationChannel(communicationChannels.newEmailAddress("info@kalvertoren.example.com"));

    }

    private Property createPropertyAndUnits(final String reference, String name, PropertyType type, int numberOfUnits,
                    Date openingDate, Date acquireDate, Owner owner) {
        Property property = properties.newProperty(reference, name, type);
        property.setOpeningDate(openingDate);
        property.setAcquireDate(acquireDate);
        property.addOwner(owner);
        for (int i = 0; i < numberOfUnits; i++) {
            int unitNumber = i + 1;
            property.newUnit(String.format("%s-%03d", reference, unitNumber), "Unit " + unitNumber).setArea(
                            new Double((i + 1) * 100));
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

    public void setOwnerRepository(final Owners owners) {
        this.owners = owners;
    }

    private CommunicationChannels communicationChannels;

    public void setCommunicationChannelsRepository(final CommunicationChannels communicationChannels) {
        this.communicationChannels = communicationChannels;
    }

}

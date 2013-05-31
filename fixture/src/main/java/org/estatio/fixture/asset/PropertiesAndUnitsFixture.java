package org.estatio.fixture.asset;

import java.math.BigDecimal;

import com.danhaywood.isis.wicket.gmap3.applib.Location;

import org.apache.isis.applib.fixtures.AbstractFixture;
import org.estatio.dom.asset.Properties;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.FixedAssetRoleType;
import org.estatio.dom.asset.PropertyType;
import org.estatio.dom.communicationchannel.CommunicationChannel;
import org.estatio.dom.communicationchannel.CommunicationChannels;
import org.estatio.dom.geography.Countries;
import org.estatio.dom.geography.Country;
import org.estatio.dom.geography.State;
import org.estatio.dom.geography.States;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.joda.time.LocalDate;


public class PropertiesAndUnitsFixture extends AbstractFixture {

    @Override
    public void install() {
        Party owner1 = parties.findPartyByReference("HELLOWORLD");
        Party owner2 = parties.findPartyByReference("ACME");
        Party manager = parties.findPartyByReference("JDOE");
        
        Property prop1 = createPropertyAndUnits("OXF", "Oxford Super Mall", PropertyType.SHOPPING_CENTER, 25, new LocalDate(1999, 1, 1), new LocalDate(2008, 6, 1), owner1, manager, 51.74579, -1.24334);
        State state = states.findByReference("GB-OXF");
        Country country = countries.findByReference("GBR");
        
        prop1.getCommunicationChannels().add((CommunicationChannel) communicationChannels.newPostalAddress("1 Market Street", null, "OX1 3HL", "Oxford", state, country));
        prop1.getCommunicationChannels().add((CommunicationChannel) communicationChannels.newPhoneNumber("+44 123 456789"));
        prop1.getCommunicationChannels().add((CommunicationChannel) communicationChannels.newFaxNumber("+44 987 654321"));
        prop1.getCommunicationChannels().add((CommunicationChannel) communicationChannels.newEmailAddress("info@oxford.example.com"));

        Property prop2 = createPropertyAndUnits("KAL", "Winkelcentrum Kalvertoren", PropertyType.SHOPPING_CENTER, 40, new LocalDate(2003, 12, 1), new LocalDate(2003, 12, 1), owner2, manager, 52.37597, 4.90814);
        Country c2 = countries.findByReference("NLD");
        State s2 = states.findByReference("NL-NH");
        
        prop2.getCommunicationChannels().add((CommunicationChannel) communicationChannels.newPostalAddress("Kalverstraat 12", null, "1017 AA", "Amsterdam", s2, c2));
        prop2.getCommunicationChannels().add((CommunicationChannel) communicationChannels.newPhoneNumber("+31 123 456789"));
        prop2.getCommunicationChannels().add((CommunicationChannel) communicationChannels.newFaxNumber("+31 987 654321"));
        prop2.getCommunicationChannels().add((CommunicationChannel) communicationChannels.newEmailAddress("info@kalvertoren.example.com"));
    }

    private Property createPropertyAndUnits(final String reference, String name, PropertyType type, int numberOfUnits, LocalDate openingDate, LocalDate acquireDate, Party owner, Party manager, double lat, double lng) {
        Property property = properties.newProperty(reference, name, type);
        property.setOpeningDate(openingDate);
        property.setAcquireDate(acquireDate);
        property.addRole(owner, FixedAssetRoleType.PROPERTY_OWNER, new LocalDate(1999, 1, 1), new LocalDate(2000, 1, 1));
        property.addRole(manager, FixedAssetRoleType.ASSET_MANAGER, null, null);
        //property.setLocation(new Location(lat, lng));
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

    public void setPartyRepository(final Parties parties) {
        this.parties = parties;
    }

    private CommunicationChannels communicationChannels;

    public void setCommunicationChannelsRepository(final CommunicationChannels communicationChannels) {
        this.communicationChannels = communicationChannels;
    }

}

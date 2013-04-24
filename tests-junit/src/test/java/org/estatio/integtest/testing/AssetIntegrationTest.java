package org.estatio.integtest.testing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Set;

import org.apache.isis.core.integtestsupport.IsisSystemForTest;
import org.estatio.dom.asset.Properties;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyActor;
import org.estatio.dom.asset.PropertyActorType;
import org.estatio.dom.asset.PropertyActors;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.asset.Units;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.integtest.IntegrationSystemForTestRule;
import org.estatio.jdo.PartiesJdo;
import org.estatio.jdo.PropertiesJdo;
import org.estatio.jdo.PropertyActorsJdo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class AssetIntegrationTest {

    private Properties properties;
    private PropertyActors actors;
    private Parties parties;
    private Units units;
    
    @Rule
    public IntegrationSystemForTestRule webServerRule = new IntegrationSystemForTestRule();

    public IsisSystemForTest getIsft() {
        return webServerRule.getIsisSystemForTest();
    }

    @Before
    public void init() {
        properties = getIsft().getService(PropertiesJdo.class);
        actors = getIsft().getService(PropertyActorsJdo.class);
        parties = getIsft().getService(PartiesJdo.class);
        units = getIsft().getService(Units.class);
    }

    @Test
    public void propertyCanBeFound() throws Exception {
        assertNotNull(properties.findPropertiesByReference("OXF"));
    }

    @Test
    public void numberOfUnitsIs25() throws Exception {
        List<Property> allProperties = properties.allProperties();
        Property property = allProperties.get(0);
        Set<Unit> units = property.getUnits();
        assertThat(units.size(), is(25));
    }

    @Test
    public void propertyCannotNotNull() throws Exception {
        Assert.assertNotNull(properties.findPropertyByReference("OXF"));
    }

    @Test
    public void propertyActorCanBeFound() throws Exception {
        Party party = parties.findPartyByReference("HELLOWORLD");
        Property property = properties.findPropertyByReference("OXF");
        PropertyActor propertyActor = actors.findPropertyActor(property, party, PropertyActorType.PROPERTY_OWNER);
        Assert.assertNotNull(propertyActor);
    }

    @Test
    public void propertyActorWithoutStartDateCanBeFound() throws Exception {
        Party party = parties.findPartyByReference("HELLOWORLD");
        Property property = properties.findPropertyByReference("OXF");
        PropertyActor propertyActor = actors.findPropertyActor(property, party, PropertyActorType.PROPERTY_OWNER);
        Assert.assertNotNull(propertyActor);
    }

    @Test
    public void unitCanBeFound() throws Exception {
        Assert.assertEquals("OXF-001", units.findByReference("OXF-001").getReference());
    }

}

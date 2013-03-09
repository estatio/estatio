package org.estatio.integtest.testing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Set;

import junit.framework.Assert;

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
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.integtestsupport.IsisSystemForTest;


public class AssetIntegrationTest {


    @Rule
    public IntegrationSystemForTestRule webServerRule = new IntegrationSystemForTestRule();

    public IsisSystemForTest getIsft() {
        return webServerRule.getIsisSystemForTest();
    }


    @Test
    public void numberOfPropertiesIs2() throws Exception {
        Properties properties = getIsft().getService(Properties.class);
        assertThat(properties.allProperties().size(), is(2));
    }


    @Test
    public void numberOfUnitsIs25() throws Exception {
        Properties properties = getIsft().getService(Properties.class);
        List<Property> allProperties = properties.allProperties();
        Property property = allProperties.get(0);
        Set<Unit> units = property.getUnits();
        assertThat(units.size(), is(25));
    }

    @Test
    public void propertyCannotNotNull() throws Exception {
        Properties properties = getIsft().getService(PropertiesJdo.class);
        Assert.assertNotNull(properties.findPropertyByReference("OXF"));
    }

    @Test 
    public void propertyActorCanBeFound() throws Exception {
        PropertyActors actors = getIsft().getService(PropertyActorsJdo.class);
        Parties parties = getIsft().getService(PartiesJdo.class);
        Properties properties = getIsft().getService(PropertiesJdo.class);
        Party party = parties.findPartyByReference("HELLOWORLD");
        Property property = properties.findPropertyByReference("OXF");
        PropertyActor propertyActor = actors.findPropertyActor(property, party, PropertyActorType.PROPERTY_OWNER);
        Assert.assertNotNull(propertyActor);
    }

    @Test 
    public void propertyActorWithoutStartDateCanBeFound() throws Exception {
        PropertyActors actors = getIsft().getService(PropertyActorsJdo.class);
        Parties parties = getIsft().getService(PartiesJdo.class);
        Properties properties = getIsft().getService(PropertiesJdo.class);
        Party party = parties.findPartyByReference("HELLOWORLD");
        Property property = properties.findPropertyByReference("OXF");
        PropertyActor propertyActor = actors.findPropertyActor(property, party, PropertyActorType.PROPERTY_OWNER);
        Assert.assertNotNull(propertyActor);
    }

    @Test 
    public void unitCanBeFound() throws Exception {
        Units units = getIsft().getService(Units.class);
        Assert.assertEquals("OXF-001", units.findByReference("OXF-001").getReference());
    }

}

package org.estatio.integtest.testing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Set;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;

import org.estatio.dom.asset.FixedAssetRole;
import org.estatio.dom.asset.FixedAssetRoleType;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.party.Party;

public class AssetIntegrationTest extends AbstractEstatioIntegrationTest {

    @Test
    public void fixedAssetFindAssetsByReferenceOrName_ok() throws Exception {
        Assert.assertThat(fixedAssets.findAssetsByReferenceOrName("*mall*").size(), Is.is(1));
    }

    @Test
    public void fixedAssetAutoComplete_ok() throws Exception {
        Assert.assertThat(fixedAssets.autoComplete("mall").size(), Is.is(1));
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
        FixedAssetRole propertyActor = actors.findRole(property, party, FixedAssetRoleType.PROPERTY_OWNER);
        Assert.assertNotNull(propertyActor);
    }

    @Test
    public void propertyActorWithoutStartDateCanBeFound() throws Exception {
        Party party = parties.findPartyByReference("HELLOWORLD");
        Property property = properties.findPropertyByReference("OXF");
        FixedAssetRole propertyActor = actors.findRole(property, party, FixedAssetRoleType.PROPERTY_OWNER);
        Assert.assertNotNull(propertyActor);
    }

    @Test
    public void unitCanBeFound() throws Exception {
        Assert.assertEquals("OXF-001", units.findUnitByReference("OXF-001").getReference());
    }

}

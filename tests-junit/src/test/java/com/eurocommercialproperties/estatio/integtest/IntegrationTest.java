package com.eurocommercialproperties.estatio.integtest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import com.eurocommercialproperties.estatio.dom.asset.Properties;
import com.eurocommercialproperties.estatio.dom.asset.Property;
import com.eurocommercialproperties.estatio.dom.asset.PropertyActor;
import com.eurocommercialproperties.estatio.dom.asset.PropertyActorType;
import com.eurocommercialproperties.estatio.dom.asset.PropertyActors;
import com.eurocommercialproperties.estatio.dom.asset.Unit;
import com.eurocommercialproperties.estatio.dom.asset.Units;
import com.eurocommercialproperties.estatio.dom.geography.Countries;
import com.eurocommercialproperties.estatio.dom.invoice.Charge;
import com.eurocommercialproperties.estatio.dom.invoice.Charges;
import com.eurocommercialproperties.estatio.dom.lease.Lease;
import com.eurocommercialproperties.estatio.dom.lease.LeaseActor;
import com.eurocommercialproperties.estatio.dom.lease.LeaseActorType;
import com.eurocommercialproperties.estatio.dom.lease.LeaseItem;
import com.eurocommercialproperties.estatio.dom.lease.LeaseItemType;
import com.eurocommercialproperties.estatio.dom.lease.LeaseTerm;
import com.eurocommercialproperties.estatio.dom.lease.LeaseTerms;
import com.eurocommercialproperties.estatio.dom.lease.Leases;
import com.eurocommercialproperties.estatio.dom.party.Parties;
import com.eurocommercialproperties.estatio.dom.party.Party;
import com.eurocommercialproperties.estatio.fixture.EstatioFixture;
import com.eurocommercialproperties.estatio.jdo.ChargesJdo;
import com.eurocommercialproperties.estatio.jdo.PartiesJdo;
import com.eurocommercialproperties.estatio.jdo.PropertiesJdo;
import com.eurocommercialproperties.estatio.jdo.PropertyActorsJdo;

import org.joda.time.LocalDate;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import org.apache.isis.core.integtestsupport.IsisSystemForTest;

public class IntegrationTest {

    private static IsisSystemForTest isft;
    
     @BeforeClass
    public static void setupSystem() throws Exception {
        isft = EstatioIntegTestBuilder.builderWith(new EstatioFixture()).build().setUpSystem();
    }
    
    @AfterClass
    public static void tearDownSystem() throws Exception {
        isft.tearDownSystem();
    }
    
    @Test
    public void countryIsNL() throws Exception {
        Countries c = isft.getService(Countries.class);
        Assert.assertEquals("NLD", c.findByReference("NLD").getReference());
    }

    @Test
    public void numberOfPropertiesIs2() throws Exception {
        Properties properties = isft.getService(Properties.class);
        assertThat(properties.allProperties().size(), is(2));
    }
    
    @Test
    public void numberOfLeaseActorsIs3() throws Exception {
        Leases leases = isft.getService(Leases.class);
        Lease lease = leases.findByReference("OXF-TOPMODEL-001"); 
        assertThat(lease.getActors().size(), is(3));
    }
    
    @Test
    public void leaseActorCanBeFound() throws Exception {
        Leases leases = isft.getService(Leases.class);
        Lease lease = leases.findByReference("OXF-TOPMODEL-001");
        Parties parties = isft.getService(Parties.class);
        Party party = parties.findPartyByReference("TOPMODEL");
        LeaseActor la = lease.findActor(party, LeaseActorType.TENANT, null);
        Assert.assertNotNull(la);
    }
    
    @Test
    public void numberOfUnitsIs25() throws Exception {
        Properties properties = isft.getService(Properties.class);
        List<Property> allProperties = properties.allProperties();
        Property property = allProperties.get(0);
        Set<Unit> units = property.getUnits();
        assertThat(units.size(), is(25));
    }

    @Test
    public void indexationFrequencyCannotBeNull() throws Exception {
        LeaseTerms terms = isft.getService(LeaseTerms.class);
        List<LeaseTerm> alLeaseTerms = terms.allLeaseTerms();
        LeaseTerm term = alLeaseTerms.get(0);
        Assert.assertNotNull(term.getLeaseItem().getIndexationFrequency());
    }
    
    @Test
    public void nexDateCannotBeNull() throws Exception {
        LeaseTerms terms = isft.getService(LeaseTerms.class);
        List<LeaseTerm> alLeaseTerms = terms.allLeaseTerms();
        LeaseTerm term = alLeaseTerms.get(0);
        Assert.assertNotNull(term.getLeaseItem().getIndexationFrequency().nextDate(new LocalDate(2012,1,1)));
    }

    @Test
    public void propertyCannotNotNull() throws Exception {
        Properties properties = isft.getService(PropertiesJdo.class);
        Assert.assertNotNull(properties.findPropertyByReference("OXF"));
    }

    @Test
    public void numberOfChargesIsOne() throws Exception {
        Charges charges = isft.getService(ChargesJdo.class);
        assertThat(charges.allCharges().get(0).getReference(), is("RENT"));
        assertThat(charges.allCharges().size(), is(1));
    }
    
    @Test
    public void chargeCanBeFound() throws Exception {
        Charges charges = isft.getService(ChargesJdo.class);
        Charge charge = charges.findChargeByReference("RENT");
        Assert.assertEquals(charge.getReference(), "RENT");
    }
    
    @Test
    public void partyCanBeFound() throws Exception {
        Parties parties = isft.getService(PartiesJdo.class);
        Assert.assertEquals(parties.findPartyByReference("HELLOWORLD").getReference(), "HELLOWORLD");
    }
    
    @Test 
    public void propertyActorCanBeFound() throws Exception {
        PropertyActors actors = isft.getService(PropertyActorsJdo.class);
        Parties parties = isft.getService(PartiesJdo.class);
        Properties properties = isft.getService(PropertiesJdo.class);
        Party party = parties.findPartyByReference("HELLOWORLD");
        Property property = properties.findPropertyByReference("OXF");
        PropertyActor propertyActor = actors.findPropertyActor(property, party, PropertyActorType.PROPERTY_OWNER);
        Assert.assertNotNull(propertyActor);
    }

    @Test 
    public void propertyActorWithoutStartDateCanBeFound() throws Exception {
        PropertyActors actors = isft.getService(PropertyActorsJdo.class);
        Parties parties = isft.getService(PartiesJdo.class);
        Properties properties = isft.getService(PropertiesJdo.class);
        Party party = parties.findPartyByReference("HELLOWORLD");
        Property property = properties.findPropertyByReference("OXF");
        PropertyActor propertyActor = actors.findPropertyActor(property, party, PropertyActorType.PROPERTY_OWNER);
        Assert.assertNotNull(propertyActor);
    }

    @Test 
    public void leaseCanBeFound() throws Exception {
        Leases leases = isft.getService(Leases.class);
        Assert.assertEquals("OXF-TOPMODEL-001", leases.findByReference("OXF-TOPMODEL-001").getReference());
    }
    
    @Test 
    public void leasesCanBeFoundUsingWildcard() throws Exception {
        Leases leases = isft.getService(Leases.class);
        assertThat(leases.findLeasesByReference("OXF*").size(), is(1));
    }

    @Test 
    public void leaseHasXItems() throws Exception {
        Leases leases = isft.getService(Leases.class);
        Lease lease = leases.findByReference("OXF-TOPMODEL-001");
        assertThat(lease.getItems().size(), is(1));
    }
    
    @Test 
    public void leaseItemCanBeFound() throws Exception {
        Leases leases = isft.getService(Leases.class);
        Lease lease = leases.findByReference("OXF-TOPMODEL-001");
        Assert.assertNotNull(lease.findItem(LeaseItemType.RENT, new LocalDate(2010, 7, 15), BigInteger.valueOf(1)));
    }

    @Test 
    public void leaseTermCanBeFound() throws Exception {
        Leases leases = isft.getService(Leases.class);
        Lease lease = leases.findByReference("OXF-TOPMODEL-001");
        LeaseItem item = (LeaseItem) lease.getItems().toArray()[0];
        Assert.assertNotNull(item.findTerm(new LocalDate(2010, 7, 15)));
    }
    
    @Test 
    public void unitCanBeFound() throws Exception {
        Units units = isft.getService(Units.class);
        Assert.assertEquals("OXF-001", units.findByReference("OXF-001").getReference());
    }

}

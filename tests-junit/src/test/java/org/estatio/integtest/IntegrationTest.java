package org.estatio.integtest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;
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
import org.estatio.dom.geography.Countries;
import org.estatio.dom.geography.States;
import org.estatio.dom.invoice.Charge;
import org.estatio.dom.invoice.Charges;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseActor;
import org.estatio.dom.lease.LeaseActorType;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.lease.LeaseTermForIndexableRent;
import org.estatio.dom.lease.LeaseTerms;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.fixture.EstatioFixture;
import org.estatio.jdo.ChargesJdo;
import org.estatio.jdo.PartiesJdo;
import org.estatio.jdo.PropertiesJdo;
import org.estatio.jdo.PropertyActorsJdo;
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
    public void stateCanBeFound() throws Exception {
        States states = isft.getService(States.class);
        Countries countries = isft.getService(Countries.class);
        assertThat(states.findByCountry(countries.findByReference("NLD")).size(), is(1));
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
        assertThat(leases.findLeasesByReference("OXF*").size(), is(2));
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
        LeaseTermForIndexableRent findTerm = (LeaseTermForIndexableRent) item.findTerm(new LocalDate(2010, 7, 15));
        Assert.assertNotNull(findTerm);
        BigDecimal baseValue = findTerm.getBaseValue();
        Assert.assertEquals(new BigDecimal("20000.0000"), baseValue);        

    }
    
    @Test 
    public void unitCanBeFound() throws Exception {
        Units units = isft.getService(Units.class);
        Assert.assertEquals("OXF-001", units.findByReference("OXF-001").getReference());
    }
    
    @Test 
    public void leaseTermVerifiedCorrectly() throws Exception {
        Leases leases = isft.getService(Leases.class);
        Lease lease = leases.findByReference("OXF-TOPMODEL-001");
        LeaseItem item = (LeaseItem) lease.getItems().toArray()[0];
        LeaseTermForIndexableRent term = (LeaseTermForIndexableRent) item.getTerms().toArray()[0];

        Assert.assertNotNull(term);
        term.verify();
        assertThat(term.getBaseIndexValue(), is(BigDecimal.valueOf(137.6).setScale(4)));
        assertThat(term.getNextIndexValue(), is(BigDecimal.valueOf(101.2).setScale(4)));
        assertThat(term.getIndexationPercentage(), is(BigDecimal.valueOf(1).setScale(4)));
        assertThat(term.getIndexedValue(), is(BigDecimal.valueOf(20200).setScale(4)));
    }
    
//    @Test
//    public void indexRateIsADecimal() throws Exception {
//        Indices indices = isft.getService(Indices.class); 
//        indices.
//        
//    }


    
}

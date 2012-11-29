package com.eurocommercialproperties.estatio.integtest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import junit.framework.Assert;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import com.eurocommercialproperties.estatio.dom.asset.Properties;
import com.eurocommercialproperties.estatio.dom.asset.Property;
import com.eurocommercialproperties.estatio.dom.asset.PropertyActor;
import com.eurocommercialproperties.estatio.dom.asset.PropertyActorType;
import com.eurocommercialproperties.estatio.dom.asset.PropertyActors;
import com.eurocommercialproperties.estatio.dom.asset.Unit;
import com.eurocommercialproperties.estatio.dom.asset.Units;
import com.eurocommercialproperties.estatio.dom.geography.Countries;
import com.eurocommercialproperties.estatio.dom.lease.LeaseTerm;
import com.eurocommercialproperties.estatio.dom.lease.LeaseTerms;
import com.eurocommercialproperties.estatio.dom.lease.Leases;
import com.eurocommercialproperties.estatio.dom.party.Parties;
import com.eurocommercialproperties.estatio.dom.party.Party;
import com.eurocommercialproperties.estatio.jdo.PartiesJdo;
import com.eurocommercialproperties.estatio.jdo.PropertiesJdo;
import com.eurocommercialproperties.estatio.jdo.PropertyActorsJdo;

import org.apache.isis.runtimes.dflt.testsupport.IsisSystemForTest;

public class IntegrationTest {

//    static {
//        PropertyConfigurator.configure("C:\\dev\\estatio\\app\\tests-junit\\logging.properties");
//    }
    
    public TestTimer outer = new TestTimer("outer");

    public IsisSystemForTest isft = buildSystemOncePerTestClass();

    public TestTimer inner = new TestTimer("inner");

    private static IsisSystemForTest system;
    private static IsisSystemForTest buildSystemOncePerTestClass() {
        if(system == null) {
            system = EstatioIntegTestBuilder.builder().build();
        }
        return system;
    }

    @Rule
    public RuleChain chain = RuleChain.outerRule(outer).around(isft).around(inner);
    
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
    
//    @Test
//    public void propertyActorCannotBeNull() throws Exception {
//        Properties properties = isft.getService(Properties.class);
//        Parties parties = isft.getService(Parties.class);
//        PropertyActors pa = isft.getService(PropertyActors.class);
//        Assert.assertNotNull(pa.findPropertyActor(properties.findPropertyByReference("OXF"), parties.findPartyByReference("HELLOWORLD"), PropertyActorType.PROPERTY_OWNER));
//    }

    @Test
    public void numberOfUnitsIs25() throws Exception {
        Properties properties = isft.getService(Properties.class);
        List<Property> allProperties = properties.allProperties();
        Property property = allProperties.get(0);
        List<Unit> units = property.getUnits();
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
    public void partyCanBeFound() throws Exception {
        Parties parties = isft.getService(PartiesJdo.class);
        Assert.assertEquals(parties.findPartyByReference("HELLOWORLD").getReference(), "HELLOWORLD");
    }
    
    @Test 
    public void propertyActorCannotBeNull() throws Exception {
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
    public void unitCanBeFound() throws Exception {
        Units units = isft.getService(Units.class);
        Assert.assertEquals("OXF-001", units.findByReference("OXF-001").getReference());
    }

}

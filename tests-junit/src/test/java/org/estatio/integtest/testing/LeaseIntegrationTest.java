package org.estatio.integtest.testing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.apache.isis.core.integtestsupport.IsisSystemForTest;
import org.apache.log4j.PropertyConfigurator;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseActor;
import org.estatio.dom.lease.LeaseActorType;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.lease.LeaseTermForIndexableRent;
import org.estatio.dom.lease.LeaseTermStatus;
import org.estatio.dom.lease.LeaseTerms;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.integtest.IntegrationSystemForTestRule;
import org.estatio.jdo.LeaseTermsJdo;
import org.estatio.jdo.LeasesJdo;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.google.common.io.Resources;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LeaseIntegrationTest {

    private Leases leases;
    private LeaseTerms leaseTerms;
    
    @Rule
    public IntegrationSystemForTestRule isisSystemRule = new IntegrationSystemForTestRule();

    public IsisSystemForTest getIsft() {
        return isisSystemRule.getIsisSystemForTest();
    }

    @Before
    public void setup(){
        leases = getIsft().getService(LeasesJdo.class);
        leaseTerms = getIsft().getService(LeaseTermsJdo.class);
    }
    
    @BeforeClass
    public static void setUpLogging() throws Exception {
        PropertyConfigurator.configure(Resources.getResource(LeaseIntegrationTest.class, "logging.properties"));
    }
    
    
    @Test
    public void t01_numberOfLeaseActorsIs3() throws Exception {
        Leases leases = getIsft().getService(Leases.class);
        Lease lease = leases.findByReference("OXF-TOPMODEL-001");
        assertThat(lease.getActors().size(), is(3));
    }

    @Test
    public void t02_leaseActorCanBeFound() throws Exception {
        Leases leases = getIsft().getService(Leases.class);
        Lease lease = leases.findByReference("OXF-TOPMODEL-001");
        Parties parties = getIsft().getService(Parties.class);
        Party party = parties.findPartyByReference("TOPMODEL");
        LeaseActor la = lease.findActor(party, LeaseActorType.TENANT, null);
        Assert.assertNotNull(la);
    }
    
    @Test
    public void t02b_numberOfleaseActorsIs3() throws Exception {
        Leases leases = getIsft().getService(Leases.class);
        Lease lease = leases.findByReference("OXF-TOPMODEL-001");
        assertThat(lease.getActors().size(), is(3));
    }


    @Test
    public void t03_indexationFrequencyCannotBeNull() throws Exception {
        LeaseTerms terms = getIsft().getService(LeaseTerms.class);
        List<LeaseTerm> allLeaseTerms = terms.allLeaseTerms();
        LeaseTermForIndexableRent term = (LeaseTermForIndexableRent) allLeaseTerms.get(0);
        Assert.assertNotNull(term.getIndexationFrequency());
    }

    @Test
    public void t04_nextDateCannotBeNull() throws Exception {
        LeaseTerms terms = getIsft().getService(LeaseTerms.class);
        List<LeaseTerm> allLeaseTerms = terms.allLeaseTerms();
        LeaseTermForIndexableRent term = (LeaseTermForIndexableRent) allLeaseTerms.get(0);
        Assert.assertNotNull(term.getIndexationFrequency().nextDate(new LocalDate(2012, 1, 1)));
    }

    @Test
    public void t05_leaseCanBeFound() throws Exception {
        Leases leases = getIsft().getService(Leases.class);
        Assert.assertEquals("OXF-TOPMODEL-001", leases.findByReference("OXF-TOPMODEL-001").getReference());
    }

    @Test
    public void t06_leasesCanBeFoundUsingWildcard() throws Exception {
        Leases leases = getIsft().getService(Leases.class);
        assertThat(leases.findLeasesByReference("OXF*").size(), is(2));
    }

    @Test
    public void t07_leaseHasXItems() throws Exception {
        Leases leases = getIsft().getService(Leases.class);
        Lease lease = leases.findByReference("OXF-TOPMODEL-001");
        assertThat(lease.getItems().size(), is(2));
    }

    @Test
    public void t08_leaseItemCanBeFound() throws Exception {
        Leases leases = getIsft().getService(Leases.class);
        Lease lease = leases.findByReference("OXF-TOPMODEL-001");
        Assert.assertNotNull(lease.findItem(LeaseItemType.RENT, new LocalDate(2010, 7, 15), BigInteger.valueOf(1)));
    }

    @Test
    public void t09_leaseTermCanBeFound() throws Exception {
        Leases leases = getIsft().getService(Leases.class);
        Lease lease = leases.findByReference("OXF-TOPMODEL-001");
        LeaseItem item = (LeaseItem) lease.getItems().toArray()[0];
        LeaseTermForIndexableRent findTerm = (LeaseTermForIndexableRent) item.findTerm(new LocalDate(2010, 7, 15));
        Assert.assertNotNull(findTerm);
        BigDecimal baseValue = findTerm.getBaseValue();
        Assert.assertEquals(new BigDecimal("20000.0000"), baseValue);
    }

    @Test
    public void t10_leaseTermVerifiedCorrectly() throws Exception {
        Leases leases = getIsft().getService(Leases.class);
        Lease lease = leases.findByReference("OXF-TOPMODEL-001");
        //first item
        LeaseItem item = (LeaseItem) lease.getItems().toArray()[0];
        //first term
        LeaseTermForIndexableRent term = (LeaseTermForIndexableRent) item.getTerms().toArray()[0];
        Assert.assertNotNull(term);
        term.verify();
        assertThat(term.getBaseIndexValue(), is(BigDecimal.valueOf(137.6).setScale(4)));
        assertThat(term.getNextIndexValue(), is(BigDecimal.valueOf(101.2).setScale(4)));
        assertThat(term.getIndexationPercentage(), is(BigDecimal.valueOf(1).setScale(1)));
        assertThat(term.getIndexedValue(), is(BigDecimal.valueOf(20200).setScale(4)));
    }

    @Test
    public void t11_leaseTermApprovesWell() throws Exception {
        Leases leases = getIsft().getService(Leases.class);
        Lease lease = leases.findByReference("OXF-TOPMODEL-001");
        //first item
        LeaseItem item = (LeaseItem) lease.getItems().toArray()[0];
        //first term
        LeaseTermForIndexableRent term = (LeaseTermForIndexableRent) item.getTerms().toArray()[0];
        term.approve();
        assertThat(term.getStatus(), is(LeaseTermStatus.APPROVED));
        assertThat(term.getValue(), is(BigDecimal.valueOf(20200).setScale(4)));
    }

    @Test
    public void t12_leaseTermInvoiceItemCreated() throws Exception {
        Lease lease = leases.findByReference("OXF-TOPMODEL-001");
        //first item
        LeaseItem item = (LeaseItem) lease.getItems().toArray()[0];
        //first term
        LeaseTermForIndexableRent term = (LeaseTermForIndexableRent) item.getTerms().toArray()[0];
        term.calculate(new LocalDate(2012,1,1));
        assertThat(term.getInvoiceItems().size(), is(1));
        term.calculate(new LocalDate(2012,4,1));
        assertThat(term.getInvoiceItems().size(), is(2));
        term.calculate(new LocalDate(2012,4,1));
        assertThat(term.getInvoiceItems().size(), is(2));
        
        term.removeUnapprovedInvoiceItemsForDate(new LocalDate(2012,4,1));
        assertThat(term.getInvoiceItems().size(), is(1));
    }

    @Test
    public void t13_leaseVerifiesWell() throws Exception {
        Lease l = leases.findByReference("OXF-TOPMODEL-001");
        l.verify();
        assertThat(l.getItems().first().getTerms().size(), is(5));
    }

    @Test
    public void t14_leaseVerifiesWell() throws Exception {
        Lease l = leases.findByReference("OXF-MEDIAX-002");
        l.verify();
        assertThat(l.getItems().first().getTerms().size(), is(7));
    }
}

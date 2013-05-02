package org.estatio.integtest.testing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.apache.isis.core.integtestsupport.IsisSystemForTest;
import org.estatio.dom.agreement.AgreementRole;
import org.estatio.dom.agreement.AgreementRoleType;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.lease.LeaseTermForIndexableRent;
import org.estatio.dom.lease.LeaseTermForServiceCharge;
import org.estatio.dom.lease.LeaseTermStatus;
import org.estatio.dom.lease.LeaseTerms;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.integtest.IntegrationSystemForTestRule;
import org.estatio.jdo.LeaseTermsJdo;
import org.estatio.jdo.LeasesJdo;
import org.hamcrest.core.Is;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LeaseIntegrationTest {

    private Leases leases;
    private LeaseTerms leaseTerms;
    private Lease lease;
    private Parties parties;

    @Rule
    public IntegrationSystemForTestRule isisSystemRule = new IntegrationSystemForTestRule();

    public IsisSystemForTest getIsft() {
        return isisSystemRule.getIsisSystemForTest();
    }

    @Before
    public void setup() {
        leases = getIsft().getService(LeasesJdo.class);
        leaseTerms = getIsft().getService(LeaseTermsJdo.class);
        parties = getIsft().getService(Parties.class);
        lease = leases.findByReference("OXF-TOPMODEL-001");
    }

    @BeforeClass
    public static void setUpLogging() throws Exception {
        // PropertyConfigurator.configure(Resources.getResource(LeaseIntegrationTest.class,
        // "logging.properties"));
    }

    @Test
    public void t01_numberOfLeaseActorsIs3() throws Exception {
        assertThat(lease.getRoles().size(), is(3));
    }

    @Test
    public void t02_leaseActorCanBeFound() throws Exception {
        Party party = parties.findPartyByReference("TOPMODEL");
        AgreementRole la = lease.findRole(party, AgreementRoleType.TENANT, null);
        Assert.assertNotNull(la);
    }

    @Test
    public void t02b_numberOfleaseActorsIs3() throws Exception {
        assertThat(lease.getRoles().size(), is(3));
    }

    @Test
    public void t03_indexationFrequencyCannotBeNull() throws Exception {
        List<LeaseTerm> allLeaseTerms = leaseTerms.allLeaseTerms();
        LeaseTerm term = (LeaseTerm) allLeaseTerms.get(0);
        Assert.assertNotNull(term.getFrequency());
    }

    @Test
    public void t04_nextDateCannotBeNull() throws Exception {
        List<LeaseTerm> allLeaseTerms = leaseTerms.allLeaseTerms();
        LeaseTerm term = (LeaseTerm) allLeaseTerms.get(0);
        Assert.assertNotNull(term.getFrequency().nextDate(new LocalDate(2012, 1, 1)));
    }

    @Test
    public void t05_leaseCanBeFound() throws Exception {
        Assert.assertEquals("OXF-TOPMODEL-001", leases.findByReference("OXF-TOPMODEL-001").getReference());
    }

    @Test
    public void t06_leasesCanBeFoundUsingWildcard() throws Exception {
        assertThat(leases.findLeasesByReference("OXF*").size(), is(3));
    }

    @Test
    public void t07_leaseHasXItems() throws Exception {
        assertThat(lease.getItems().size(), is(2));
    }

    @Test
    public void t08_leaseItemCanBeFound() throws Exception {
        LeaseItem rentItem = lease.findItem(LeaseItemType.RENT, new LocalDate(2010, 7, 15), BigInteger.valueOf(1));
        Assert.assertNotNull(rentItem);
    }

    @Test
    public void t09_leaseTermForRentCanBeFound() throws Exception {
        LeaseItem item = lease.findItem(LeaseItemType.RENT, new LocalDate(2010, 7, 15), BigInteger.valueOf(1));
        LeaseTermForIndexableRent leaseTerm = (LeaseTermForIndexableRent) item.findTerm(new LocalDate(2010, 7, 15));
        Assert.assertNotNull(leaseTerm);
        BigDecimal baseValue = leaseTerm.getBaseValue();
        Assert.assertEquals(new BigDecimal("20000.0000"), baseValue);
    }

    @Test
    public void t09_leaseTermForServiceChargeCanBeFound() throws Exception {
        LeaseItem item = (LeaseItem) lease.findItem(LeaseItemType.SERVICE_CHARGE, new LocalDate(2010, 7, 15), BigInteger.valueOf(1));
        Assert.assertThat(item.getTerms().size(), Is.is(1));
        LeaseTermForServiceCharge leaseTerm = (LeaseTermForServiceCharge) item.findTerm(new LocalDate(2010, 7, 15));
        Assert.assertThat(leaseTerm.getBudgetedValue(), Is.is(new BigDecimal("6000.0000")));
    }

    @Test
    public void t10_leaseTermForIndexableRentVerifiedCorrectly() throws Exception {
        LeaseItem item = lease.findItem(LeaseItemType.RENT, new LocalDate(2010, 7, 15), BigInteger.valueOf(1));
        LeaseTermForIndexableRent term = (LeaseTermForIndexableRent) item.getTerms().first();
        term.verify();
        assertThat(term.getBaseIndexValue(), is(BigDecimal.valueOf(137.6).setScale(4)));
        assertThat(term.getNextIndexValue(), is(BigDecimal.valueOf(101.2).setScale(4)));
        assertThat(term.getIndexationPercentage(), is(BigDecimal.valueOf(1).setScale(1)));
        assertThat(term.getIndexedValue(), is(BigDecimal.valueOf(20200).setScale(4)));
    }

    @Test
    public void t10_leaseTermForServiceChargeVerifiedCorrectly() throws Exception {
        LeaseItem item = lease.findItem(LeaseItemType.SERVICE_CHARGE, new LocalDate(2010, 7, 15), BigInteger.valueOf(1));
        assertThat(item.getTerms().size(), is(1));
        LeaseTerm term = item.getTerms().first();
        term.verify();
        assertNotNull(item.findTerm(new LocalDate(2012, 7, 15)));
    }

    @Test
    public void t11_leaseVerifiesWell() throws Exception {
        lease.verify();
        LeaseItem item1 = lease.findItem(LeaseItemType.RENT, new LocalDate(2010, 7, 15), BigInteger.valueOf(1));
        assertNotNull(item1.findTerm(new LocalDate(2012, 7, 15)));
        LeaseItem item2 = lease.findItem(LeaseItemType.SERVICE_CHARGE, new LocalDate(2010, 7, 15), BigInteger.valueOf(1));
        item2.verify();
        assertNotNull(item2.findTerm(new LocalDate(2012, 7, 15)));
    }

    @Test
    public void t12_leaseVerifiesWell() throws Exception {
        Lease leaseMediax = leases.findByReference("OXF-MEDIAX-002");
        leaseMediax.verify();
        LeaseItem item = lease.findItem(LeaseItemType.SERVICE_CHARGE, new LocalDate(2010, 7, 15), BigInteger.valueOf(1));
        assertNotNull(item.findTerm(new LocalDate(2012, 7, 15)));
        assertThat(item.getTerms().last().getValue(), is(BigDecimal.valueOf(6000).setScale(4)));
    }

    @Test
    public void t12b_leaseVerifiesWell() throws Exception {
        Lease leaseMediax = leases.findByReference("OXF-POISON-003");
        leaseMediax.verify();
        LeaseItem item = lease.findItem(LeaseItemType.SERVICE_CHARGE, new LocalDate(2010, 7, 15), BigInteger.valueOf(1));
        assertNotNull(item.findTerm(new LocalDate(2012, 7, 15)));
        assertThat(item.getTerms().last().getValue(), is(BigDecimal.valueOf(6000).setScale(4)));

    }

    @Test
    public void t13_leaseTermApprovesWell() throws Exception {
        LeaseItem item = lease.findItem(LeaseItemType.RENT, new LocalDate(2010, 7, 15), BigInteger.valueOf(1));
        LeaseTerm term = (LeaseTerm) item.getTerms().toArray()[0];
        term.approve();
        assertThat(term.getStatus(), is(LeaseTermStatus.APPROVED));
        assertThat(term.getValue(), is(BigDecimal.valueOf(20200).setScale(4)));
    }

    @Test
    public void t14_invoiceItemsForRentCreated() throws Exception {
        LeaseItem item = lease.findItem(LeaseItemType.RENT, new LocalDate(2010, 7, 15), BigInteger.valueOf(1));
        // first term
        LeaseTerm term = (LeaseTerm) item.getTerms().toArray()[0];
        term.calculate(new LocalDate(2010, 7, 1), new LocalDate(2010, 6, 1));
        assertThat(term.getInvoiceItems().size(), is(1));
        term.calculate(new LocalDate(2010, 10, 1), new LocalDate(2010, 10, 1));
        assertThat(term.getInvoiceItems().size(), is(2));
        term.calculate(new LocalDate(2010, 10, 1), new LocalDate(2010, 10, 1));
        assertThat(term.getInvoiceItems().size(), is(2));
        term.removeUnapprovedInvoiceItemsForDate(new LocalDate(2010, 10, 1));
        assertThat(term.getInvoiceItems().size(), is(1));
    }

    @Test
    public void t14b_invoiceItemsNotCreated() throws Exception {
        Lease lease = leases.findByReference("OXF-POISON-003");
        LeaseItem item = lease.findItem(LeaseItemType.RENT, new LocalDate(2011, 1, 1), BigInteger.ONE);
        LeaseTerm term = item.getTerms().first();
        term.verify();
        term.calculate(new LocalDate(2011, 1, 2), new LocalDate(2011, 1, 1));
        assertThat(term.getInvoiceItems().size(), is(0));
    }

    @Test
    public void t15_invoiceItemsForServiceChargeCreated() throws Exception {
        LeaseItem item = lease.findItem(LeaseItemType.SERVICE_CHARGE, new LocalDate(2010, 7, 15), BigInteger.valueOf(1));
        LeaseTermForServiceCharge term = (LeaseTermForServiceCharge) item.getTerms().first();
        term.calculate(new LocalDate(2010, 7, 1), new LocalDate(2010, 6, 1));
        assertThat(term.getInvoiceItems().size(), is(1));
        term.calculate(new LocalDate(2010, 10, 1), new LocalDate(2010, 10, 1));
        assertThat(term.getInvoiceItems().size(), is(2));
        term.calculate(new LocalDate(2010, 10, 1), new LocalDate(2010, 10, 1));
        assertThat(term.getInvoiceItems().size(), is(2));
        term.removeUnapprovedInvoiceItemsForDate(new LocalDate(2010, 10, 1));
        assertThat(term.getInvoiceItems().size(), is(1));
    }

    @Test
    public void t16_bulkLeaseCalculate() throws Exception {
        LeaseItem item = lease.findItem(LeaseItemType.SERVICE_CHARGE, new LocalDate(2010, 7, 15), BigInteger.valueOf(1));
        LeaseTermForServiceCharge term = (LeaseTermForServiceCharge) item.getTerms().first();
        // call calulate on lease
        lease.calculate(new LocalDate(2010, 10, 1), new LocalDate(2010, 10, 1));
        assertThat(term.getInvoiceItems().size(), is(2)); // the previous test
                                                          // already supplied
                                                          // one
    }
}

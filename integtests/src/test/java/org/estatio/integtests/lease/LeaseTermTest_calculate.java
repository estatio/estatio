/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.integtests.lease;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;
import java.util.SortedSet;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.lease.*;
import org.estatio.dom.lease.invoicing.*;
import org.estatio.dom.valuetypes.LocalDateInterval;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertiesAndUnitsFixture;
import org.estatio.fixture.lease.LeasesAndLeaseUnitsAndLeaseItemsAndLeaseTermsAndTagsAndBreakOptionsFixture;
import org.estatio.fixture.party.PersonsAndOrganisationsAndCommunicationChannelsFixture;
import org.estatio.integtests.EstatioIntegrationTest;
import org.estatio.services.settings.EstatioSettingsService;
import org.hamcrest.core.Is;
import org.joda.time.LocalDate;
import org.junit.*;
import org.junit.runners.MethodSorters;
import org.apache.isis.applib.fixturescripts.CompositeFixtureScript;
import org.apache.isis.objectstore.jdo.applib.service.support.IsisJdoSupport;
import org.apache.isis.objectstore.jdo.datanucleus.service.support.IsisJdoSupportImpl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LeaseTermTest_calculate extends EstatioIntegrationTest {

    @BeforeClass
    public static void setupData() {
        scenarioExecution().install(new CompositeFixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                execute(new EstatioBaseLineFixture(), executionContext);
                execute("parties", new PersonsAndOrganisationsAndCommunicationChannelsFixture(), executionContext);
                execute("properties", new PropertiesAndUnitsFixture(), executionContext);
                execute("leases", new LeasesAndLeaseUnitsAndLeaseItemsAndLeaseTermsAndTagsAndBreakOptionsFixture(), executionContext);
            }
        });
    }

    private Leases leases;
    private LeaseTerms leaseTerms;
    private InvoiceItemsForLease invoiceItemsForLease;
    private EstatioSettingsService estatioSettingsService;

    private Lease lease;
    private LeaseItem leaseTopModelRentItem;
    private LeaseItem leaseTopModelServiceChargeItem;

    private InvoiceCalculationService invoiceCalculationService;

    protected IsisJdoSupport isisJdoSupport;

    @Before
    public void setup() {
        isisJdoSupport = service(IsisJdoSupportImpl.class);

        leases = service(Leases.class);
        leaseTerms = service(LeaseTerms.class);
        invoiceItemsForLease = service(InvoiceItemsForLease.class);
        estatioSettingsService = service(EstatioSettingsService.class);
        invoiceCalculationService = service(InvoiceCalculationService.class);

        lease = leases.findLeaseByReference("OXF-TOPMODEL-001");
        assertThat(lease.getItems().size(), is(3));

        leaseTopModelRentItem = lease.findItem(LeaseItemType.RENT, new LocalDate(2010, 7, 15), BigInteger.valueOf(1));
        leaseTopModelServiceChargeItem = lease.findItem(LeaseItemType.SERVICE_CHARGE, new LocalDate(2010, 7, 15), BigInteger.valueOf(1));

        Assert.assertNotNull(leaseTopModelRentItem);
        Assert.assertNotNull(leaseTopModelServiceChargeItem);
    }

    @Test
    public void t08_lease_findItem_whenRent_and_leaseItem_findTerm() throws Exception {

        final SortedSet<LeaseTerm> terms = leaseTopModelRentItem.getTerms();
        Assert.assertThat(terms.size(), is(1));
        final LeaseTerm term0 = terms.first();

        LeaseTermForIndexableRent leaseTopModelRentTerm = (LeaseTermForIndexableRent) leaseTopModelRentItem.findTerm(new LocalDate(2010, 7, 15));
        Assert.assertNotNull(leaseTopModelRentTerm);

        List<LeaseTerm> allLeaseTerms = leaseTerms.allLeaseTerms();
        LeaseTerm term = allLeaseTerms.get(0);

        assertThat(leaseTopModelRentTerm, is(term));
        assertThat(leaseTopModelRentTerm, is(term0));

        // given the first leaseTerm has non-null frequency
        Assert.assertNotNull(term.getFrequency());
        Assert.assertNotNull(term.getFrequency().nextDate(new LocalDate(2012, 1, 1)));

        BigDecimal baseValue = leaseTopModelRentTerm.getBaseValue();
        Assert.assertEquals(new BigDecimal("20000.00"), baseValue);
    }

    @Test
    public void t09_lease_findItem_whenServiceCharge_and_leaseItem_findTerm() throws Exception {

        final SortedSet<LeaseTerm> terms = leaseTopModelServiceChargeItem.getTerms();
        Assert.assertThat(terms.size(), Is.is(1));
        final LeaseTerm term0 = terms.first();

        LeaseTermForServiceCharge leaseTopModelServiceChargeTerm = (LeaseTermForServiceCharge) leaseTopModelServiceChargeItem.findTerm(new LocalDate(2010, 7, 15));

        assertThat(leaseTopModelServiceChargeTerm, is(term0));

        Assert.assertThat(leaseTopModelServiceChargeTerm.getBudgetedValue(), Is.is(new BigDecimal("6000.00")));
    }

    @Test
    public void t10_leaseTermRent_verify() throws Exception {
        // given
        LeaseTermForIndexableRent leaseTopModelRentTerm1 = (LeaseTermForIndexableRent) leaseTopModelRentItem.getTerms().first();

        // when
        leaseTopModelRentTerm1.verifyUntil(new LocalDate(2014, 1, 1));

        // then
        assertThat(leaseTopModelRentTerm1.getBaseIndexValue(), is(BigDecimal.valueOf(137.6).setScale(4)));
        assertThat(leaseTopModelRentTerm1.getNextIndexValue(), is(BigDecimal.valueOf(101.2).setScale(4)));
        assertThat(leaseTopModelRentTerm1.getIndexationPercentage(), is(BigDecimal.valueOf(1).setScale(1)));
        assertThat(leaseTopModelRentTerm1.getIndexedValue(), is(BigDecimal.valueOf(20200).setScale(2)));
    }

    @Test
    public void t10_leaseTermServiceCharge_verify() throws Exception {
        // given
        assertThat(leaseTopModelServiceChargeItem.getTerms().size(), is(1));

        // when
        leaseTopModelServiceChargeItem.getTerms().first().verifyUntil(new LocalDate(2014, 1, 1));

        // then
        SortedSet<LeaseTerm> terms = leaseTopModelServiceChargeItem.getTerms();
        assertNotNull(terms.toString(), leaseTopModelServiceChargeItem.findTerm(new LocalDate(2012, 7, 15)));
    }

    @Test
    public void t11_lease_verify() throws Exception {

        // when
        lease.verifyUntil(new LocalDate(2014, 1, 1));

        // then
        assertNotNull(leaseTopModelRentItem.findTerm(new LocalDate(2012, 7, 15)));

        // and when
        leaseTopModelServiceChargeItem.verify();

        // then
        assertNotNull(leaseTopModelServiceChargeItem.findTerm(new LocalDate(2012, 7, 15)));
    }

    @Test
    public void t13_leaseTerm_lock() throws Exception {
        // given
        LeaseTerm term = (LeaseTerm) leaseTopModelRentItem.getTerms().toArray()[0];

        // when
        term.approve();

        // then
        assertThat(term.getStatus(), is(LeaseTermStatus.APPROVED));
        assertThat(term.getEffectiveValue(), is(BigDecimal.valueOf(20200).setScale(2)));
    }

    @Test
    public void t141a_leaseTerm_verify_and_calculate() throws Exception {
        // given
        LeaseTerm leaseTopModelRentTerm = leaseTopModelRentItem.findTerm(new LocalDate(2010, 7, 15));

        // when
        leaseTopModelRentTerm.verifyUntil(new LocalDate(2014, 1, 1));
        // and when
        test(leaseTopModelRentTerm, "2010-07-01", "2010-10-01", "2010-07-01/2010-10-01", 4239.13, false);
    }

    // scenario: invoiceItemsForRentCreated
    @Ignore
    @Test
    public void t14b_invoiceItemsForRentCreated() throws Exception {

        estatioSettingsService.updateEpochDate(null);

        LeaseTerm leaseTopModelRentTerm0 = (LeaseTerm) leaseTopModelRentItem.getTerms().first();
        // full term
        test(leaseTopModelRentTerm0, "2010-10-01", "2010-10-02", "2010-10-01/2011-01-01", 5000.00, false);
        // invoice after effective date
        test(leaseTopModelRentTerm0, "2010-10-01", "2011-04-02", "2010-10-01/2011-01-01", 5050.00, false);
        // invoice after effective date with mock
        estatioSettingsService.updateEpochDate(new LocalDate(2011, 1, 1));

        test(leaseTopModelRentTerm0, "2010-10-01", "2011-04-2", "2010-10-01/2011-01-01", 50.00, true);

        estatioSettingsService.updateEpochDate(null);
        // TODO: without this code there will be 4 items in the set
        isisJdoSupport.refresh(leaseTopModelRentTerm0);
        SortedSet<InvoiceItemForLease> invoiceItems = leaseTopModelRentTerm0.getInvoiceItems();
        assertThat(invoiceItems.size(), is(3));
    }

    // scenario: invoiceItemsForServiceChargeCreated
    @Ignore
    @Test
    public void t15_invoiceItemsForServiceChargeCreated() throws Exception {

        estatioSettingsService.updateEpochDate(new LocalDate(1980, 1, 1));
        LeaseTermForServiceCharge leaseTopModelServiceChargeTerm0 = (LeaseTermForServiceCharge) leaseTopModelServiceChargeItem.getTerms().first();
        leaseTopModelServiceChargeTerm0.approve();
        // partial period
        test(leaseTopModelServiceChargeTerm0, "2010-07-01", "2010-10-01", "2010-07-01/2010-10-01", 1271.74, false);
        // full period

        test(leaseTopModelServiceChargeTerm0, "2010-01-10", "2010-10-02", "2010-10-01/2011-01-01", 1500.00, false);
        // reconcile with mock date
    }

    @Ignore
    @Test
    public void t15_invoiceItemsForServiceChargeCreatedWithEpochDate() throws Exception {
        estatioSettingsService.updateEpochDate(new LocalDate(2011, 1, 1));
        LeaseTermForServiceCharge leaseTopModelServiceChargeTerm0 = (LeaseTermForServiceCharge) leaseTopModelServiceChargeItem.getTerms().first();

        test(leaseTopModelServiceChargeTerm0, "2010-10-01", "2011-01-01", "2010-10-01/2011-01-01", 0.00, false);
        leaseTopModelServiceChargeTerm0.setAuditedValue(new BigDecimal(6600.00));
        leaseTopModelServiceChargeTerm0.verify();

        test(leaseTopModelServiceChargeTerm0, "2010-10-01", "2012-01-01", "2010-10-01/2011-01-01", 150.00, true);
        // reconcile without mock
        estatioSettingsService.updateEpochDate(new LocalDate(1980, 1, 1));
    }

    @Ignore
    @Test
    public void t16_bulkLeaseCalculate() throws Exception {
        leaseTopModelServiceChargeItem = lease.findItem(LeaseItemType.SERVICE_CHARGE, new LocalDate(2010, 7, 15), BigInteger.valueOf(1));
        LeaseTermForServiceCharge leaseTopModelServiceChargeTerm0 = (LeaseTermForServiceCharge) leaseTopModelServiceChargeItem.getTerms().first();
        // call calculate on leaseTopModel
        lease.calculate(InvoiceRunType.NORMAL_RUN, InvoiceCalculationSelection.RENT_AND_SERVICE_CHARGE, new LocalDate(2010, 10, 1), new LocalDate(2010, 10, 1), null);
        assertThat(leaseTopModelServiceChargeTerm0.getInvoiceItems().size(), is(2));
    }

    private void test(
            final LeaseTerm leaseTerm,
            final String startDueDateStr,
            final String nextDueDateStr,
            final String intervalStr,
            final Double value,
            final boolean expectedAdjustment) {
        final LocalDate startDueDate = LocalDate.parse(startDueDateStr);
        final LocalDate nextDueDate = LocalDate.parse(nextDueDateStr);
        final BigDecimal expected = value == null ? null : new BigDecimal(value).setScale(2, RoundingMode.HALF_UP);
        invoiceItemsForLease.removeUnapprovedInvoiceItems(leaseTerm, LocalDateInterval.parseString(intervalStr));
        isisJdoSupport.refresh(leaseTerm);
        InvoiceCalculationParameters parameters = new InvoiceCalculationParameters(
                leaseTerm,
                InvoiceRunType.NORMAL_RUN,
                startDueDate,
                startDueDate,
                nextDueDate);
        invoiceCalculationService.calculateAndInvoice(parameters);
        InvoiceItemForLease invoiceItem = invoiceItemsForLease.findUnapprovedInvoiceItem(leaseTerm, LocalDateInterval.parseString(intervalStr));
        isisJdoSupport.refresh(leaseTerm);
        BigDecimal netAmount = invoiceItem == null ? new BigDecimal("0.00") : invoiceItem.getNetAmount();
        Boolean adjustment = invoiceItem == null ? false : invoiceItem.isAdjustment();
        assertThat("size " + invoiceItemsForLease.findByLeaseTermAndInvoiceStatus(leaseTerm, InvoiceStatus.NEW).size(),
                netAmount, is(expected));
        assertThat(adjustment, is(expectedAdjustment));
    }

}

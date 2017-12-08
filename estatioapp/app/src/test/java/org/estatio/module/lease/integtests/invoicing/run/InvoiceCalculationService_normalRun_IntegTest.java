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
package org.estatio.module.lease.integtests.invoicing.run;

import java.math.BigDecimal;
import java.util.SortedSet;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.objectstore.jdo.applib.service.support.IsisJdoSupport;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;
import org.incode.module.base.integtests.VT;

import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.asset.fixtures.property.enums.PropertyAndUnitsAndOwnerAndManager_enum;
import org.estatio.module.invoice.dom.InvoiceRunType;
import org.estatio.module.invoice.dom.InvoiceStatus;
import org.estatio.module.lease.app.InvoiceServiceMenu;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.LeaseTerm;
import org.estatio.module.lease.dom.LeaseTermForServiceCharge;
import org.estatio.module.lease.dom.LeaseTermStatus;
import org.estatio.module.lease.dom.invoicing.InvoiceCalculationParameters;
import org.estatio.module.lease.dom.invoicing.InvoiceCalculationSelection;
import org.estatio.module.lease.dom.invoicing.InvoiceCalculationService;
import org.estatio.module.lease.dom.invoicing.InvoiceItemForLease;
import org.estatio.module.lease.dom.invoicing.InvoiceItemForLeaseRepository;
import org.estatio.module.lease.dom.settings.LeaseInvoicingSettingsService;
import org.estatio.module.lease.fixtures.breakoptions.personas.LeaseBreakOptionsForOxfMediax002Gb;
import org.estatio.module.lease.fixtures.breakoptions.personas.LeaseBreakOptionsForOxfPoison003Gb;
import org.estatio.module.lease.fixtures.breakoptions.personas.LeaseBreakOptionsForOxfTopModel001;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForDeposit_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForDiscount_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForPercentage_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForRent_enum;
import org.estatio.module.lease.fixtures.leaseitems.rent.personas.LeaseItemAndLeaseTermForRentForKalPoison001;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForServiceCharge_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForTurnoverRent_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class InvoiceCalculationService_normalRun_IntegTest extends LeaseModuleIntegTestAbstract {

    @Inject
    private LeaseRepository leaseRepository;

    @Inject
    private InvoiceItemForLeaseRepository invoiceItemForLeaseRepository;

    @Inject
    private LeaseInvoicingSettingsService leaseInvoicingSettingsService;

    @Inject
    private IsisJdoSupport isisJdoSupport;

    @Inject
    private InvoiceCalculationService invoiceCalculationService;

    @Inject
    private InvoiceServiceMenu invoiceService;

    private Lease lease;
    private LeaseItem leaseTopModelRentItem;
    private LeaseItem leaseTopModelServiceChargeItem;

    //    @BeforeClass
    //    public static void setupData() {
    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, Person_enum.LinusTorvaldsNl.builder());
                executionContext.executeChild(this, PropertyAndUnitsAndOwnerAndManager_enum.OxfGb.builder());
                executionContext.executeChild(this, PropertyAndUnitsAndOwnerAndManager_enum.KalNl.builder());
                executionContext.executeChild(this, new LeaseBreakOptionsForOxfTopModel001());
                executionContext.executeChild(this, new LeaseBreakOptionsForOxfMediax002Gb());
                executionContext.executeChild(this, new LeaseBreakOptionsForOxfPoison003Gb());
                executionContext.executeChild(this, LeaseItemForRent_enum.KalPoison001Nl.builder());
                executionContext.executeChild(this, Lease_enum.OxfPret004Gb.builder());

                executionContext.executeChild(this, LeaseItemForRent_enum.OxfMiracl005Gb.builder());
                executionContext.executeChild(this, LeaseItemForServiceCharge_enum.OxfMiracl005Gb.builder());
                executionContext.executeChild(this, LeaseItemForTurnoverRent_enum.OxfMiracl005Gb.builder());
                executionContext.executeChild(this, LeaseItemForDiscount_enum.OxfMiracle005bGb.builder());
                executionContext.executeChild(this, LeaseItemForPercentage_enum.OxfMiracl005Gb.builder());
                executionContext.executeChild(this, LeaseItemForDeposit_enum.OxfMiracle005bGb.builder());

            }
        });

        lease = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
        assertThat(lease.getItems().size(), is(10));

        leaseTopModelRentItem = lease.findItem(LeaseItemType.RENT, VT.ld(2010, 7, 15), VT.bi(1));
        leaseTopModelServiceChargeItem = lease.findItem(LeaseItemType.SERVICE_CHARGE, VT.ld(2010, 7, 15), VT.bi(1));

        assertNotNull(leaseTopModelRentItem);
        assertNotNull(leaseTopModelServiceChargeItem);
    }

    @Test
    public void normalRun() throws Exception {
        whenLeaseTermApproved();

        // TODO: to un-ignore
        // t14b_invoiceItemsForRentCreated();

        // TODO: to un-ignore
        // t15_invoiceItemsForServiceChargeCreated();

        // TODO: to un-ignore
        // t15_invoiceItemsForServiceChargeCreatedWithEpochDate();

        // TODO: to un-ignore
        // t16_bulkLeaseCalculate();
    }

    public void whenLeaseTermApproved() throws Exception {

        // given
        lease.verifyUntil(VT.ld(2014, 1, 1));

        LeaseTerm leaseTopModelRentTerm = leaseTopModelRentItem.findTerm(VT.ld(2010, 7, 15));
        leaseTopModelRentTerm.approve();

        assertThat(leaseTopModelRentTerm.getStatus(), is(LeaseTermStatus.APPROVED));
        assertThat(leaseTopModelRentTerm.getEffectiveValue(), is(VT.bd2(20200)));

        // when, then

        calculateNormalRunAndAssert(
                leaseTopModelRentTerm, VT.ld(2010,7,1), VT.ld(2010,10,01), VT.ldi("2010-07-01/2010-10-01"),
                4239.13, false
        );
    }

    // scenario: invoiceItemsForRentCreated
    public void t14b_invoiceItemsForRentCreated() throws Exception {

        leaseInvoicingSettingsService.updateEpochDate(null);

        LeaseTerm leaseTopModelRentTerm0 = (LeaseTerm) leaseTopModelRentItem.getTerms().first();
        // full term

        calculateNormalRunAndAssert(
                leaseTopModelRentTerm0, VT.ld(2010,10,1), VT.ld(2010,10,2), VT.ldi("2010-10-01/2011-01-01"),
                5000.00, false
        );
        // invoice after effective date

        calculateNormalRunAndAssert(
                leaseTopModelRentTerm0, VT.ld(2010,10,1), VT.ld(2011,4,2), VT.ldi("2010-10-01/2011-01-01"),
                5050.00, false
        );
        // invoice after effective date with mock
        leaseInvoicingSettingsService.updateEpochDate(VT.ld(2011, 1, 1));

        calculateNormalRunAndAssert(
                leaseTopModelRentTerm0, VT.ld(2010,10,1), VT.ld(2011,4,2), VT.ldi("2010-10-01/2011-01-01"), 50.00,
                true
        );

        leaseInvoicingSettingsService.updateEpochDate(null);

        // TODO: without this code there will be 4 items in the set
        isisJdoSupport.refresh(leaseTopModelRentTerm0);
        SortedSet<InvoiceItemForLease> invoiceItems = leaseTopModelRentTerm0.getInvoiceItems();
        assertThat(invoiceItems.size(), is(3));
    }

    // scenario: invoiceItemsForServiceChargeCreated
    public void t15_invoiceItemsForServiceChargeCreated() throws Exception {

        leaseInvoicingSettingsService.updateEpochDate(VT.ld(1980, 1, 1));

        LeaseTermForServiceCharge leaseTopModelServiceChargeTerm0 = (LeaseTermForServiceCharge) leaseTopModelServiceChargeItem.getTerms().first();
        leaseTopModelServiceChargeTerm0.approve();

        // partial period

        calculateNormalRunAndAssert(leaseTopModelServiceChargeTerm0, VT.ld(2010,7,1), VT.ld(2010,10,1), VT.ldi(
                "2010-07-01/2010-10-01"), 1271.74, false
        );
        // full period

        calculateNormalRunAndAssert(leaseTopModelServiceChargeTerm0, VT.ld(2010,1,10), VT.ld(2010,10,2), VT.ldi(
                "2010-10-01/2011-01-01"), 1500.00, false
        );
        // reconcile with mock date
    }

    public void t15_invoiceItemsForServiceChargeCreatedWithEpochDate() throws Exception {
        leaseInvoicingSettingsService.updateEpochDate(VT.ld(2011, 1, 1));
        LeaseTermForServiceCharge leaseTopModelServiceChargeTerm0 = (LeaseTermForServiceCharge) leaseTopModelServiceChargeItem.getTerms().first();

        calculateNormalRunAndAssert(leaseTopModelServiceChargeTerm0, VT.ld(2010,10,1), VT.ld(2011,1,1), VT.ldi(
                "2010-10-01/2011-01-01"), 0.00, false
        );
        leaseTopModelServiceChargeTerm0.setAuditedValue(VT.bd(6600.00));
        leaseTopModelServiceChargeTerm0.verify();

        calculateNormalRunAndAssert(leaseTopModelServiceChargeTerm0, VT.ld(2010,10,1), VT.ld(2012,1,1), VT.ldi(
                "2010-10-01/2011-01-01"), 150.00, true
        );
        // reconcile without mock
        leaseInvoicingSettingsService.updateEpochDate(VT.ld(1980, 1, 1));
    }

    public void t16_bulkLeaseCalculate() throws Exception {
        leaseTopModelServiceChargeItem = lease.findItem(LeaseItemType.SERVICE_CHARGE, VT.ld(2010, 7, 15), VT.bi(1));
        LeaseTermForServiceCharge leaseTopModelServiceChargeTerm0 = (LeaseTermForServiceCharge) leaseTopModelServiceChargeItem.getTerms().first();
        // call findOrCreateValues on leaseTopModel
        invoiceService.calculateLegacy(lease, InvoiceRunType.NORMAL_RUN, InvoiceCalculationSelection.ALL_RENT_AND_SERVICE_CHARGE, VT.ld(2010, 10, 1), VT.ld(2010, 10, 1), null);
        assertThat(leaseTopModelServiceChargeTerm0.getInvoiceItems().size(), is(2));
    }

    private void calculateNormalRunAndAssert(
            final LeaseTerm leaseTerm,
            final LocalDate startDueDate,
            final LocalDate nextDueDate,
            final LocalDateInterval interval,
            final Double expected,
            final boolean expectedAdjustment) {
        invoiceItemForLeaseRepository.removeUnapprovedInvoiceItems(leaseTerm, interval);

        transactionService.nextTransaction();
        isisJdoSupport.refresh(leaseTerm);

        InvoiceCalculationParameters parameters = InvoiceCalculationParameters.builder()
                .leaseTerm(leaseTerm)
                .invoiceRunType(InvoiceRunType.NORMAL_RUN)
                .invoiceDueDate(startDueDate)
                .startDueDate(startDueDate)
                .nextDueDate(nextDueDate).build();
        invoiceCalculationService.calculateAndInvoice(parameters);

        InvoiceItemForLease invoiceItem = invoiceItemForLeaseRepository.findUnapprovedInvoiceItem(leaseTerm,
                interval);
        isisJdoSupport.refresh(leaseTerm);

        BigDecimal netAmount = invoiceItem == null ? VT.bd2(0) : invoiceItem.getNetAmount();
        final String reason = "size " + invoiceItemForLeaseRepository.findByLeaseTermAndInvoiceStatus(leaseTerm, InvoiceStatus.NEW).size();
        assertThat(reason, netAmount, is(VT.bd2hup(expected)));

        Boolean adjustment = invoiceItem == null ? false : invoiceItem.getAdjustment();
        assertThat(adjustment, is(expectedAdjustment));
    }

}

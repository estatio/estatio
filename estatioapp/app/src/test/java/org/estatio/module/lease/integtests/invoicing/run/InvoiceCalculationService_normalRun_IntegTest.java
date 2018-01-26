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
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseTerm;
import org.estatio.module.lease.dom.LeaseTermForServiceCharge;
import org.estatio.module.lease.dom.LeaseTermStatus;
import org.estatio.module.lease.dom.invoicing.InvoiceCalculationParameters;
import org.estatio.module.lease.dom.invoicing.InvoiceCalculationService;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;
import org.estatio.module.lease.dom.invoicing.InvoiceForLeaseRepository;
import org.estatio.module.lease.dom.invoicing.InvoiceItemForLease;
import org.estatio.module.lease.dom.invoicing.InvoiceItemForLeaseRepository;
import org.estatio.module.lease.dom.settings.LeaseInvoicingSettingsService;
import org.estatio.module.lease.fixtures.breakoptions.enums.BreakOption_enum;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForDeposit_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForDiscount_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForEntryFee_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForMarketing_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForRent_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForServiceChargeBudgeted_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForServiceCharge_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForTax_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForTurnoverRent_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;

import static org.assertj.core.api.Assertions.assertThat;

public class InvoiceCalculationService_normalRun_IntegTest extends LeaseModuleIntegTestAbstract {


    @Inject
    private InvoiceItemForLeaseRepository invoiceItemForLeaseRepository;

    @Inject
    private LeaseInvoicingSettingsService leaseInvoicingSettingsService;

    @Inject
    private IsisJdoSupport isisJdoSupport;

    @Inject
    private InvoiceCalculationService invoiceCalculationService;


    @Inject
    private InvoiceForLeaseRepository invoiceForLeaseRepository;

    private Lease lease;
    private LeaseItem leaseTopModelRentItem;
    private LeaseItem leaseTopModelServiceChargeItem;

    //    @BeforeClass
    //    public static void setupData() {
    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext ec) {

                ec.executeChildren(this,
                        Person_enum.LinusTorvaldsNl,
                        PropertyAndUnitsAndOwnerAndManager_enum.OxfGb,
                        PropertyAndUnitsAndOwnerAndManager_enum.KalNl,

                        LeaseItemForRent_enum.OxfMediaX002Gb,
                        LeaseItemForServiceCharge_enum.OxfMediaX002Gb,
                        LeaseItemForTurnoverRent_enum.OxfMediaX002Gb,

                        LeaseItemForRent_enum.OxfPoison003Gb,
                        LeaseItemForServiceCharge_enum.OxfPoison003Gb,
                        LeaseItemForTurnoverRent_enum.OxfPoison003Gb,

                        LeaseItemForRent_enum.OxfTopModel001Gb,
                        LeaseItemForServiceCharge_enum.OxfTopModel001Gb,
                        LeaseItemForServiceCharge_enum.OxfTopModel001Gb_TA,
                        LeaseItemForServiceChargeBudgeted_enum.OxfTopModel001Gb,
                        LeaseItemForTurnoverRent_enum.OxfTopModel001Gb,
                        LeaseItemForDiscount_enum.OxfTopModel001Gb,
                        LeaseItemForEntryFee_enum.OxfTopModel001Gb,
                        LeaseItemForTax_enum.OxfTopModel001Gb,
                        LeaseItemForDeposit_enum.OxfTopModel001Gb,
                        LeaseItemForMarketing_enum.OxfTopModel001Gb,

                        BreakOption_enum.OxfPoison003Gb_FIXED,
                        BreakOption_enum.OxfPoison003Gb_ROLLING,
                        BreakOption_enum.OxfPoison003Gb_FIXED,
                        BreakOption_enum.OxfPoison003Gb_ROLLING,
                        BreakOption_enum.OxfTopModel001Gb_FIXED,
                        BreakOption_enum.OxfTopModel001Gb_ROLLING,

                        LeaseItemForRent_enum.KalPoison001Nl,

                        Lease_enum.OxfPret004Gb,
                        LeaseItemForRent_enum.OxfMiracl005Gb,
                        LeaseItemForServiceCharge_enum.OxfMiracl005Gb,
                        LeaseItemForTurnoverRent_enum.OxfMiracl005Gb,
                        LeaseItemForDiscount_enum.OxfMiracle005bGb,
                        LeaseItemForDeposit_enum.OxfMiracle005bGb);
            }
        });

        lease = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
        assertThat(lease.getItems().size()).isEqualTo(9);

        leaseTopModelRentItem = lease.findItem(LeaseItemType.RENT, VT.ld(2010, 7, 15), VT.bi(1));
        leaseTopModelServiceChargeItem = lease.findItem(LeaseItemType.SERVICE_CHARGE, VT.ld(2010, 7, 15), VT.bi(1));

        assertThat(leaseTopModelRentItem).isNotNull();
        assertThat(leaseTopModelServiceChargeItem).isNotNull();
    }

    @Test
    public void whenLeaseTermApproved() throws Exception {

        // given
        lease.verifyUntil(VT.ld(2014, 1, 1));

        LeaseTerm leaseTopModelRentTerm = leaseTopModelRentItem.findTerm(VT.ld(2010, 7, 15));
        leaseTopModelRentTerm.approve();

        assertThat(leaseTopModelRentTerm.getStatus()).isEqualTo(LeaseTermStatus.APPROVED);
        assertThat(leaseTopModelRentTerm.getEffectiveValue()).isEqualTo(VT.bd2(20200));

        // when, then
        calculateNormalRunAndAssert(
                leaseTopModelRentTerm,
                VT.ld(2010,7,1),
                VT.ld(2010,10,01),
                VT.ldi("2010-07-01/2010-10-01"),
                4239.13,
                false,
                VT.ldi("2010-07-15/2010-10-01")
                );
    }

    @Test
    public void invoiceItemsForRentCreated() throws Exception {

        leaseInvoicingSettingsService.updateEpochDate(null);

        LeaseTerm leaseTopModelRentTerm0 = (LeaseTerm) leaseTopModelRentItem.getTerms().first();
        // full term

        calculateNormalRunAndAssert(
                leaseTopModelRentTerm0,
                VT.ld(2010,10,1),
                VT.ld(2010,10,2),
                VT.ldi("2010-10-01/2011-01-01"),
                5000.00, false,
                null);
        // invoice after effective date

        calculateNormalRunAndAssert(
                leaseTopModelRentTerm0, VT.ld(2010,10,1), VT.ld(2011,4,2), VT.ldi("2010-10-01/2011-01-01"),
                5050.00, false,
                null);

    }

    @Test
    public void invoiceItemsForServiceChargeCreated() throws Exception {

        leaseInvoicingSettingsService.updateEpochDate(VT.ld(1980, 1, 1));

        LeaseTermForServiceCharge leaseTopModelServiceChargeTerm0 = (LeaseTermForServiceCharge) leaseTopModelServiceChargeItem.getTerms().first();
        leaseTopModelServiceChargeTerm0.approve();

        // partial period

        calculateNormalRunAndAssert(leaseTopModelServiceChargeTerm0, VT.ld(2010,7,1), VT.ld(2010,10,1), VT.ldi(
                "2010-07-01/2010-10-01"), 1271.74, false,
                null);
        // full period

        calculateNormalRunAndAssert(leaseTopModelServiceChargeTerm0, VT.ld(2010,1,10), VT.ld(2010,10,2), VT.ldi(
                "2010-10-01/2011-01-01"), 1500.00, false,
                null);
    }

    @Test
    public void effective_interval_is_swapped() throws Exception {

        // given
        lease.verifyUntil(VT.ld(2014, 1, 1));

        LeaseTerm leaseTopModelRentTerm = leaseTopModelRentItem.findTerm(VT.ld(2010, 7, 15));
        leaseTopModelRentTerm.approve();

        assertThat(leaseTopModelRentTerm.getStatus()).isEqualTo(LeaseTermStatus.APPROVED);
        assertThat(leaseTopModelRentTerm.getEffectiveValue()).isEqualTo(VT.bd2(20200));

        final String runId = calculateNormalRunAndAssert(
                leaseTopModelRentTerm,
                VT.ld(2010, 7, 1),
                VT.ld(2010, 10, 01),
                VT.ldi("2010-07-01/2010-10-01"),
                4239.13,
                false,
                VT.ldi("2010-07-15/2010-10-01")
        );

        final InvoiceForLease invoiceForLease = invoiceForLeaseRepository.findInvoicesByRunId(runId).stream().findFirst().get();
        wrap(mixin(InvoiceForLease._approve.class, invoiceForLease)).$$();

        // when
        lease.terminate(VT.ld(2010,8,31));

        // then
        final String runId2 = calculateNormalRunAndAssert(
                leaseTopModelRentTerm,
                VT.ld(2010, 7, 1),
                VT.ld(2010, 10, 01),
                VT.ldi("2010-07-01/2010-10-01"),
                -1630.43,
                false,
                VT.ldi("2010-09-01/2010-10-01")
        );


        //Was VT.ldi("2010-07-15/2010-10-01")
        //Terminated VT.ldi("2010-07-15/2010-09-01")

        //Delta VT.ldi("2010-09-01/2010-10-01")




        //        wrap(new InvoiceForLease._approve(invoiceForLease)).$$();


    }


    private String calculateNormalRunAndAssert(
            final LeaseTerm leaseTerm,
            final LocalDate startDueDate,
            final LocalDate nextDueDate,
            final LocalDateInterval interval,
            final Double expected,
            final boolean expectedAdjustment,
            final LocalDateInterval expectedEffectiveInterval) {

        transactionService.nextTransaction();
        isisJdoSupport.refresh(leaseTerm);

        InvoiceCalculationParameters parameters = InvoiceCalculationParameters.builder()
                .leaseTerm(leaseTerm)
                .invoiceRunType(InvoiceRunType.NORMAL_RUN)
                .invoiceDueDate(startDueDate)
                .startDueDate(startDueDate)
                .nextDueDate(nextDueDate).build();
        final String runId = invoiceCalculationService.calculateAndInvoice(parameters);

        InvoiceItemForLease invoiceItem = invoiceItemForLeaseRepository.findUnapprovedInvoiceItem(leaseTerm,
                interval);
        isisJdoSupport.refresh(leaseTerm);

        BigDecimal netAmount = invoiceItem == null ? VT.bd2(0) : invoiceItem.getNetAmount();
        final String reason = "size " + invoiceItemForLeaseRepository.findByLeaseTermAndInvoiceStatus(leaseTerm, InvoiceStatus.NEW).size();
        assertThat(netAmount).isEqualTo(VT.bd2hup(expected)).as(reason);

        Boolean adjustment = invoiceItem == null ? false : invoiceItem.getAdjustment();
        assertThat(adjustment).isEqualTo(expectedAdjustment);

        if (expectedEffectiveInterval != null){
            assertThat(invoiceItem.getEffectiveInterval()).isEqualTo(expectedEffectiveInterval);

        }

        return runId;

    }

}

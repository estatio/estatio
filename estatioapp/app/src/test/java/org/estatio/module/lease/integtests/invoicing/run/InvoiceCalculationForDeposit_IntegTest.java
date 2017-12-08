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
import java.util.Arrays;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.incode.module.base.integtests.VT;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.fixtures.charges.enums.Charge_enum;
import org.estatio.module.index.fixtures.enums.Index_enum;
import org.estatio.module.invoice.dom.InvoiceRunType;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.lease.contributions.Property_calculateInvoices;
import org.estatio.module.lease.dom.Fraction;
import org.estatio.module.lease.dom.InvoicingFrequency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseTermForDeposit;
import org.estatio.module.lease.dom.LeaseTermForIndexable;
import org.estatio.module.lease.dom.indexation.IndexationMethod;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;
import org.estatio.module.lease.dom.invoicing.InvoiceForLeaseRepository;
import org.estatio.module.lease.dom.invoicing.InvoiceItemForLease;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;

import static org.assertj.core.api.Assertions.assertThat;

public class InvoiceCalculationForDeposit_IntegTest extends LeaseModuleIntegTestAbstract {

    @Inject
    InvoiceForLeaseRepository invoiceForLeaseRepository;

    @Before
    public void setUp() throws Exception {

        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {

                executionContext.executeChild(this, Lease_enum.OxfPoison010ADVANCEGb.builder());
                executionContext.executeChild(this, Lease_enum.OxfPoison011ARREARSGb.builder());
                executionContext.executeChild(this, Charge_enum.GbRent.builder());

            }
        });
    }

    Charge chargeForRent;
    Charge chargeForDeposit;
    Lease poisonLease010Advance;
    Lease poisonLease011Arrears;
    LeaseItem rentItem010;
    LeaseItem rentItem011;
    LeaseItem depositItem010InAdvance;
    LeaseItem depositItem011InArrears;

    final LocalDate startDateForItems = new LocalDate(2011,3,1);

    // final LocalDate effectiveIndexationDateFor010Rent = VT.ld(2012, 3, 1);
    // WE DO NOT SUPPORT THIS SCENARIO: the effective indexation date should be in line with the invoice frequency, otherwise...
    // .. the user would expect that the indexation on 1-3-2012 - which is in the quarter being invoiced - is taken into account
    // .. we need pro-rata calculation for rent
    final LocalDate effectiveIndexationDateFor010Rent = VT.ld(2012, 1, 1);
    final LocalDate effectiveIndexationDateFor011Rent = VT.ld(2012, 1, 1);

    final BigDecimal quarterlyRentAfterIndexation = VT.bd("2501.35");
    final BigDecimal depositValueBeforeIndexation = VT.bd("5001.60");
    final BigDecimal depositValueAfterIndexation = VT.bd("5002.70");

    final LocalDate startOf2011 = VT.ld(2011,1,1);
    final LocalDate endOf2011 = VT.ld(2011,12,31);
    final LocalDate startOf2012 = VT.ld(2012,1,1);


    @Test
    public void in_arrears_and_in_advance_in_one_normal_run_works() {

        // given
        setUpTheGiven();
        sessionManagementService.nextSession(); // EST-1750: to mimick behaviour through UI

        // when
        LocalDate invoiceDueDate = VT.ld(2012,1,1);
        LocalDate startDueDate = VT.ld(2011,12,31); // NOTE: the user has to realize that the due date for invoices in arrears is the last date of the period
        LocalDate nextDueDate = VT.ld(2012,1,2);

        Property oxford = Property_enum.OxfGb.findUsing(serviceRegistry);
        final LocalDate dateOfInvoiceRun = VT.ld(2011, 12, 1);
        setFixtureClockDate(dateOfInvoiceRun);

        mixin(Property_calculateInvoices.class, oxford).exec(
                InvoiceRunType.NORMAL_RUN,
                Arrays.asList(LeaseItemType.RENT, LeaseItemType.DEPOSIT),
                invoiceDueDate,
                startDueDate,
                nextDueDate
        );

        transactionService.nextTransaction(); // EST-1750: to mimick behaviour through UI
        reloadObjectsForNewSession(); //TODO: manage extra boilerplate because of new session -> to be handled in new programming model?
        assertThat(rentItem010.getTerms().size()).isEqualTo(2);
        assertThat(rentItem011.getTerms().size()).isEqualTo(2);

        // then
        InvoiceForLease invoiceFor010Advance = invoiceForLeaseRepository.findByLease(poisonLease010Advance).get(0);
        assertThat(invoiceFor010Advance.getItems().size()).isEqualTo(2);

        InvoiceItemForLease rentItem010 = (InvoiceItemForLease) invoiceFor010Advance.findFirstItemWithCharge(chargeForRent);
        assertThat(rentItem010.getNetAmount()).isEqualTo(quarterlyRentAfterIndexation);

        InvoiceItemForLease depositItem010Advance = (InvoiceItemForLease) invoiceFor010Advance.findFirstItemWithCharge(chargeForDeposit);
        assertThat(depositItem010Advance.getNetAmount()).isEqualTo(depositValueAfterIndexation);

        InvoiceForLease invoiceFor011Arrears = invoiceForLeaseRepository.findByLease(poisonLease011Arrears).get(0);
        assertThat(invoiceFor011Arrears.getItems().size()).isEqualTo(2);

        InvoiceItemForLease rentItem011 = (InvoiceItemForLease) invoiceFor011Arrears.findFirstItemWithCharge(chargeForRent);
        assertThat(rentItem011.getNetAmount()).isEqualTo(quarterlyRentAfterIndexation);

        InvoiceItemForLease depositItem011Arrears = (InvoiceItemForLease) invoiceFor011Arrears.findFirstItemWithCharge(chargeForDeposit);
        assertThat(depositItem011Arrears.getNetAmount()).isEqualTo(depositValueBeforeIndexation); // The user expects that the indexation on 1-1-2012 - which is outside the quarter being invoiced in arrears - is NOT taken into account
    }

    @Test
    public void just_in_advance_in_one_normal_run_works() {

        // given
        setUpTheGiven();
        sessionManagementService.nextSession(); // EST-1750: to mimick behaviour through UI

        // when
        LocalDate invoiceDueDate = VT.ld(2012,1,1);
        LocalDate startDueDate = VT.ld(2012,1,1); // NOTE: NOW it matters that the due date of the deposit term is in the past
        LocalDate nextDueDate = VT.ld(2012,1,2);

        Property oxford = Property_enum.OxfGb.findUsing(serviceRegistry);
        final LocalDate dateOfInvoiceRun = VT.ld(2011, 12, 1);
        setFixtureClockDate(dateOfInvoiceRun);

        mixin(Property_calculateInvoices.class, oxford).exec(
                InvoiceRunType.NORMAL_RUN,
                Arrays.asList(LeaseItemType.RENT, LeaseItemType.DEPOSIT),
                invoiceDueDate,
                startDueDate,
                nextDueDate
        );

        transactionService.nextTransaction(); // EST-1750: to mimick behaviour through UI
        reloadObjectsForNewSession(); //TODO: manage extra boilerplate because of new session -> to be handled in new programming model?
        assertThat(rentItem010.getTerms().size()).isEqualTo(2);
        assertThat(rentItem011.getTerms().size()).isEqualTo(2);

        // then
        InvoiceForLease invoiceFor010Advance = invoiceForLeaseRepository.findByLease(poisonLease010Advance).get(0);
        assertThat(invoiceFor010Advance.getItems().size()).isEqualTo(2);

        InvoiceItemForLease rentItem010 = (InvoiceItemForLease) invoiceFor010Advance.findFirstItemWithCharge(chargeForRent);
        assertThat(rentItem010.getNetAmount()).isEqualTo(quarterlyRentAfterIndexation);

        InvoiceItemForLease depositItem010Advance = (InvoiceItemForLease) invoiceFor010Advance.findFirstItemWithCharge(chargeForDeposit);
        assertThat(depositItem010Advance.getNetAmount()).isEqualTo(depositValueAfterIndexation);

        InvoiceForLease invoiceFor011Arrears = invoiceForLeaseRepository.findByLease(poisonLease011Arrears).get(0);
        assertThat(invoiceFor011Arrears.getItems().size()).isEqualTo(1);

        InvoiceItemForLease rentItem011 = (InvoiceItemForLease) invoiceFor011Arrears.findFirstItemWithCharge(chargeForRent);
        assertThat(rentItem011.getNetAmount()).isEqualTo(quarterlyRentAfterIndexation);

    }


    @Test
    public void in_arrears_and_in_advance_in_one_retro_run_works() {

        // given
        setUpTheGiven();
        sessionManagementService.nextSession(); // EST-1750: to mimick behaviour through UI

        // when
        LocalDate invoiceDueDate = VT.ld(2012,1,1);
        LocalDate startDueDate = VT.ld(2011,12,31); // NOTE: the user has to realize that the due date for invoices in arrears is the last date of the period
        LocalDate nextDueDate = VT.ld(2012,1,2);

        Property oxford = Property_enum.OxfGb.findUsing(serviceRegistry);
        final LocalDate dateOfInvoiceRun = VT.ld(2011, 12, 1);
        setFixtureClockDate(dateOfInvoiceRun);

        mixin(Property_calculateInvoices.class, oxford).exec(
                InvoiceRunType.RETRO_RUN,
                Arrays.asList(LeaseItemType.RENT, LeaseItemType.DEPOSIT),
                invoiceDueDate,
                startDueDate,
                nextDueDate
        );

        transactionService.nextTransaction(); // EST-1750: to mimick behaviour through UI
        reloadObjectsForNewSession(); //TODO: manage extra boilerplate because of new session -> to be handled in new programming model?

        assertThat(rentItem010.getTerms().size()).isEqualTo(2);
        assertThat(rentItem011.getTerms().size()).isEqualTo(2);

        // then
        InvoiceForLease invoiceFor010Advance = invoiceForLeaseRepository.findByLease(poisonLease010Advance).get(0);
        assertThat(invoiceFor010Advance.getItems().size()).isEqualTo(6);

        InvoiceItemForLease depositItem010Advance = (InvoiceItemForLease) invoiceFor010Advance.findFirstItemWithCharge(chargeForDeposit);
        assertThat(depositItem010Advance.getNetAmount()).isEqualTo(depositValueAfterIndexation);

        InvoiceForLease invoiceFor011Arrears = invoiceForLeaseRepository.findByLease(poisonLease011Arrears).get(0);
        assertThat(invoiceFor011Arrears.getItems().size()).isEqualTo(6);

        InvoiceItemForLease depositItem011Arrears = (InvoiceItemForLease) invoiceFor011Arrears.findFirstItemWithCharge(chargeForDeposit);
        assertThat(depositItem011Arrears.getNetAmount()).isEqualTo(depositValueBeforeIndexation); // The user expects that the indexation on 1-1-2012 - which is outside the quarter being invoiced in arrears - is NOT taken into account

    }

    @Test
    public void with_due_date_deposit_before_period_retro_run_yields_same_results() {

        // given
        setUpTheGiven();
        sessionManagementService.nextSession(); // EST-1750: to mimick behaviour through UI

        // when
        LocalDate invoiceDueDate = VT.ld(2012,1,1);
        LocalDate startDueDate = VT.ld(2012,1,1); // NOT: NOW it does NOT matter that the due date of the deposit term is in the past
        LocalDate nextDueDate = VT.ld(2012,1,2);

        Property oxford = Property_enum.OxfGb.findUsing(serviceRegistry);
        final LocalDate dateOfInvoiceRun = VT.ld(2011, 12, 1);
        setFixtureClockDate(dateOfInvoiceRun);

        mixin(Property_calculateInvoices.class, oxford).exec(
                InvoiceRunType.RETRO_RUN,
                Arrays.asList(LeaseItemType.RENT, LeaseItemType.DEPOSIT),
                invoiceDueDate,
                startDueDate,
                nextDueDate
        );

        transactionService.nextTransaction(); // EST-1750: to mimick behaviour through UI
        reloadObjectsForNewSession(); //TODO: manage extra boilerplate because of new session -> to be handled in new programming model?

        assertThat(rentItem010.getTerms().size()).isEqualTo(2);
        assertThat(rentItem011.getTerms().size()).isEqualTo(2);

        // then
        InvoiceForLease invoiceFor010Advance = invoiceForLeaseRepository.findByLease(poisonLease010Advance).get(0);
        assertThat(invoiceFor010Advance.getItems().size()).isEqualTo(6);

        InvoiceItemForLease depositItem010Advance = (InvoiceItemForLease) invoiceFor010Advance.findFirstItemWithCharge(chargeForDeposit);
        assertThat(depositItem010Advance.getNetAmount()).isEqualTo(depositValueAfterIndexation);

        InvoiceForLease invoiceFor011Arrears = invoiceForLeaseRepository.findByLease(poisonLease011Arrears).get(0);
        assertThat(invoiceFor011Arrears.getItems().size()).isEqualTo(6);

        InvoiceItemForLease depositItem011Arrears = (InvoiceItemForLease) invoiceFor011Arrears.findFirstItemWithCharge(chargeForDeposit);
        assertThat(depositItem011Arrears.getNetAmount()).isEqualTo(depositValueBeforeIndexation); // The user expects that the indexation on 1-1-2012 - which is outside the quarter being invoiced in arrears - is NOT taken into account

    }

    private void setUpTheGiven(){

        chargeForRent = Charge_enum.GbRent.findUsing(serviceRegistry);
        chargeForDeposit = Charge_enum.GbDeposit.findUsing(serviceRegistry);

        poisonLease010Advance = Lease_enum.OxfPoison010ADVANCEGb.findUsing(serviceRegistry);
        rentItem010 = setUpRentItem(poisonLease010Advance, effectiveIndexationDateFor010Rent.minusYears(1));
        depositItem010InAdvance = setUpDepositItem(poisonLease010Advance, rentItem010, InvoicingFrequency.QUARTERLY_IN_ADVANCE);

        poisonLease011Arrears = Lease_enum.OxfPoison011ARREARSGb.findUsing(serviceRegistry);
        rentItem011 = setUpRentItem(poisonLease011Arrears, effectiveIndexationDateFor011Rent.minusYears(1));
        depositItem011InArrears= setUpDepositItem(poisonLease011Arrears, rentItem011, InvoicingFrequency.QUARTERLY_IN_ARREARS);

    }

    private LeaseItem setUpRentItem(final Lease lease, final LocalDate effectiveIndexationDate){

        LeaseItem leaseItem = wrap(lease).newItem(
                LeaseItemType.RENT,
                LeaseAgreementRoleTypeEnum.LANDLORD,
                chargeForRent,
                InvoicingFrequency.QUARTERLY_IN_ADVANCE,
                PaymentMethod.DIRECT_DEBIT,
                effectiveIndexationDate);
        LeaseTermForIndexable rentTerm1 = (LeaseTermForIndexable) wrap(leaseItem).newTerm(startDateForItems, endOf2011);
        wrap(rentTerm1).changeValues(
                VT.bd("10000.00"),
                null);
        wrap(rentTerm1).changeParameters(
                IndexationMethod.LAST_KNOWN_INDEX,
                Index_enum.IStatFoi.findUsing(serviceRegistry),
                startOf2011,
                startOf2012,
                BigDecimal.ONE,
                effectiveIndexationDate);
        return leaseItem;
    }

    private LeaseItem setUpDepositItem(final Lease lease, final LeaseItem source, final InvoicingFrequency invoicingFrequency){

        LeaseItem depositItem = wrap(lease).newItem(
                LeaseItemType.DEPOSIT,
                LeaseAgreementRoleTypeEnum.LANDLORD,
                chargeForDeposit,
                invoicingFrequency,
                PaymentMethod.DIRECT_DEBIT,
                startDateForItems);
        wrap(depositItem).newSourceItem(source);
        LeaseTermForDeposit term = (LeaseTermForDeposit) wrap(depositItem).newTerm(startDateForItems, null);
        wrap(term).changeParameters(Fraction.M6, null, false);
        return depositItem;
    }

    private void reloadObjectsForNewSession(){
        chargeForRent = Charge_enum.GbRent.findUsing(serviceRegistry);
        chargeForDeposit = Charge_enum.GbDeposit.findUsing(serviceRegistry);

        poisonLease010Advance = Lease_enum.OxfPoison010ADVANCEGb.findUsing(serviceRegistry);
        rentItem010 = poisonLease010Advance.findFirstItemOfType(LeaseItemType.RENT);
        depositItem010InAdvance = poisonLease010Advance.findFirstItemOfType(LeaseItemType.DEPOSIT);

        poisonLease011Arrears = Lease_enum.OxfPoison011ARREARSGb.findUsing(serviceRegistry);
        rentItem011 = poisonLease011Arrears.findFirstItemOfType(LeaseItemType.RENT);
        depositItem011InArrears= poisonLease011Arrears.findFirstItemOfType(LeaseItemType.DEPOSIT);
    }

}
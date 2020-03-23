package org.estatio.module.application.integtests.admin;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.application.app.Lease_closeOldAndOpenNewLeaseItem;
import org.estatio.module.application.integtests.ApplicationModuleIntegTestAbstract;
import org.estatio.module.invoice.dom.InvoiceRepository;
import org.estatio.module.invoice.dom.InvoiceRunType;
import org.estatio.module.lease.app.InvoiceMenu;
import org.estatio.module.lease.contributions.Lease_calculate;
import org.estatio.module.lease.dom.InvoicingFrequency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseTerm;
import org.estatio.module.lease.dom.LeaseTermForIndexable;
import org.estatio.module.lease.dom.LeaseTermForServiceCharge;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;
import org.estatio.module.lease.dom.invoicing.InvoiceForLeaseRepository;
import org.estatio.module.lease.fixtures.docfrag.enums.DocFragment_demo_enum;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForIndexableServiceCharge_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForRent_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForServiceCharge_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForTurnoverRent_enum;

public class Lease_closeOldAndOpenNewLeaseItem_IntegTest extends ApplicationModuleIntegTestAbstract {

    @Before
    public void setup() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChildren(this,
                        DocFragment_demo_enum.InvoicePreliminaryLetterDescription_DemoGbr,
                        DocFragment_demo_enum.InvoiceDescription_DemoGbr,
                        DocFragment_demo_enum.InvoiceItemDescription_DemoGbr
                );
                executionContext.executeChild(this, LeaseItemForRent_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, LeaseItemForServiceCharge_enum.OxfTopModel001Gb);
                executionContext.executeChild(this, LeaseItemForTurnoverRent_enum.OxfTopModel001Gb);
            }
        });
    }

    @Test
    public void happyCase() throws Exception {

        final LocalDate newItemStartDate = new LocalDate(2020, 4, 1);

        // given
        final Lease topmodelLease = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
        final LocalDate startOfTheYear = new LocalDate(2020, 1, 1);
        final Object exec = mixin(Lease_calculate.class, topmodelLease).exec(
                InvoiceRunType.NORMAL_RUN,
                Arrays.asList(LeaseItemType.RENT, LeaseItemType.SERVICE_CHARGE),
                startOfTheYear,
                startOfTheYear,
                new LocalDate(2020, 1, 2));
        invoiceMenu.allNewInvoices().stream().filter(i->i.getDueDate().equals(startOfTheYear)).forEach(i->i.approveAll());
        invoiceMenu.allApprovedInvoices().stream().filter(i->i.getDueDate().equals(startOfTheYear)).forEach(i->i.invoiceAll(
                startOfTheYear, true));
        mixin(Lease_calculate.class, topmodelLease).exec(
                InvoiceRunType.NORMAL_RUN,
                Arrays.asList(LeaseItemType.RENT, LeaseItemType.SERVICE_CHARGE),
                newItemStartDate,
                newItemStartDate,
                new LocalDate(2020, 4,2));
        invoiceMenu.allNewInvoices().stream().filter(i->i.getDueDate().equals(newItemStartDate)).forEach(i->i.approveAll());
        invoiceMenu.allApprovedInvoices().stream().filter(i->i.getDueDate().equals(newItemStartDate)).forEach(i->i.invoiceAll(newItemStartDate, true));
        final LeaseItem rentItemOld = LeaseItemForRent_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
        LeaseTermForIndexable currentTermOnRentItemOld = (LeaseTermForIndexable) rentItemOld.currentTerm(newItemStartDate);

        Assertions.assertThat(currentTermOnRentItemOld.getInvoiceItems()).hasSize(2);
        final LeaseItem scItemOld = LeaseItemForServiceCharge_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
        final LeaseTermForServiceCharge currentTermOnSCItemOld = (LeaseTermForServiceCharge) scItemOld.currentTerm(newItemStartDate);
        Assertions.assertThat(currentTermOnSCItemOld.getInvoiceItems()).hasSize(2);
        final LeaseItem turnoverRentItem = LeaseItemForTurnoverRent_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
        Assertions.assertThat(turnoverRentItem.getSourceItems()).hasSize(1);
        Assertions.assertThat(turnoverRentItem.getSourceItems().get(0).getSourceItem()).isEqualTo(rentItemOld);

        // when
        transactionService.nextTransaction();
        mixin(Lease_closeOldAndOpenNewLeaseItem.class, topmodelLease).act(newItemStartDate, LeaseItemType.RENT, InvoicingFrequency.QUARTERLY_IN_ADVANCE, InvoicingFrequency.MONTHLY_IN_ADVANCE, true);
        transactionService.nextTransaction();

        // then
        final LeaseItem rentItemNew = topmodelLease
                .findItem(LeaseItemType.RENT, newItemStartDate, LeaseAgreementRoleTypeEnum.LANDLORD);
        final LeaseTermForIndexable currentTermOnRentItemNew = (LeaseTermForIndexable) rentItemNew.currentTerm(newItemStartDate);
        Assertions.assertThat(rentItemNew.getStartDate()).isEqualTo(newItemStartDate);
        Assertions.assertThat(rentItemNew.getInvoicingFrequency()).isEqualTo(InvoicingFrequency.MONTHLY_IN_ADVANCE);
        Assertions.assertThat(rentItemNew.getInvoicedBy()).isEqualTo(LeaseAgreementRoleTypeEnum.LANDLORD);

        Assertions.assertThat(currentTermOnRentItemNew.getBaseValue()).isEqualTo(currentTermOnRentItemOld.getBaseValue());
        Assertions.assertThat(currentTermOnRentItemNew.getIndexedValue()).isEqualTo(currentTermOnRentItemOld.getIndexedValue());
        Assertions.assertThat(currentTermOnRentItemNew.getIndex()).isEqualTo(currentTermOnRentItemOld.getIndex());
        // etc...

        Assertions.assertThat(rentItemOld.getEndDate()).isEqualTo(newItemStartDate.minusDays(1));
        final List<InvoiceForLease> topmodelInvoices = invoiceRepository.findByLease(topmodelLease);
        Assertions.assertThat(topmodelInvoices).hasSize(1);
        Assertions.assertThat(topmodelInvoices.get(0).getDueDate()).isEqualTo(startOfTheYear);
        Assertions.assertThat(currentTermOnSCItemOld.getInvoiceItems()).hasSize(1);

        Assertions.assertThat(turnoverRentItem.getSourceItems()).hasSize(2);
        Assertions.assertThat(turnoverRentItem.getSourceItems().get(1).getSourceItem()).isEqualTo(rentItemNew);

        // and when
        mixin(Lease_closeOldAndOpenNewLeaseItem.class, topmodelLease).act(newItemStartDate, LeaseItemType.SERVICE_CHARGE, InvoicingFrequency.QUARTERLY_IN_ADVANCE, InvoicingFrequency.MONTHLY_IN_ADVANCE, true);
        transactionService.nextTransaction();
        // then
        final LeaseItem scItemNew = topmodelLease
                .findItem(LeaseItemType.SERVICE_CHARGE, newItemStartDate, LeaseAgreementRoleTypeEnum.LANDLORD);
        final LeaseTermForServiceCharge currentTermOnSCItemNew = (LeaseTermForServiceCharge) scItemNew.currentTerm(newItemStartDate);
        Assertions.assertThat(scItemOld.getEndDate()).isEqualTo(newItemStartDate.minusDays(1));
        Assertions.assertThat(scItemNew.getStartDate()).isEqualTo(newItemStartDate);
        Assertions.assertThat(scItemNew.getInvoicingFrequency()).isEqualTo(InvoicingFrequency.MONTHLY_IN_ADVANCE);
        Assertions.assertThat(scItemNew.getInvoicedBy()).isEqualTo(LeaseAgreementRoleTypeEnum.LANDLORD);

        Assertions.assertThat(currentTermOnSCItemNew.getBudgetedValue()).isEqualTo(currentTermOnSCItemOld.getBudgetedValue());
        //        etc..
    }

    @Inject InvoiceMenu invoiceMenu;

    @Inject InvoiceForLeaseRepository invoiceRepository;



}

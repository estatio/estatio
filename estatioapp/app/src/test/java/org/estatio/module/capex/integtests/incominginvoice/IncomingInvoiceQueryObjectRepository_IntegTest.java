package org.estatio.module.capex.integtests.incominginvoice;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.fixtures.property.enums.PropertyAndUnitsAndOwnerAndManager_enum;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.assetfinancial.fixtures.enums.BankAccountFaFa_enum;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItemRepository;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceQueryObjectRepo;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.financial.fixtures.bankaccount.enums.BankAccount_enum;
import org.estatio.module.invoice.dom.InvoiceStatus;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.fixtures.orgcomms.enums.OrganisationAndComms_enum;

import static org.assertj.core.api.Assertions.assertThat;

public class IncomingInvoiceQueryObjectRepository_IntegTest extends CapexModuleIntegTestAbstract {

    @Inject
    IncomingInvoiceRepository incomingInvoiceRepository;
    private IncomingInvoiceApprovalState approvalStateIfAny;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {

                executionContext.executeChild(this, OrganisationAndComms_enum.TopModelGb.builder());
                executionContext.executeChild(this, OrganisationAndComms_enum.HelloWorldGb.builder());
                executionContext.executeChild(this, PropertyAndUnitsAndOwnerAndManager_enum.OxfGb.builder());
                executionContext.executeChild(this, BankAccount_enum.HelloWorldNl.builder());
                executionContext.executeChild(this, BankAccountFaFa_enum.HelloWorldNl.builder());
            }
        });
    }

    Party seller;
    Party buyer;
    String invoiceNumber;
    String atPath;
    LocalDate invoiceDate;
    LocalDate dueDate;
    PaymentMethod paymentMethod;
    InvoiceStatus invoiceStatus;
    Property property;

    @Test
    public void find_by_reported_date_works() throws Exception {
        // given
        final IncomingInvoice invoice1 = createIncomingInvoice();
        final IncomingInvoiceItem item1OnInv1 = incomingInvoiceItemRepository
                .addItem(invoice1, IncomingInvoiceType.CAPEX, null, null, null, null, null, null, null, null, null,
                        null, null);
        final IncomingInvoiceItem item2OnInv1 = incomingInvoiceItemRepository
                .addItem(invoice1, IncomingInvoiceType.CAPEX, null, null, null, null, null, null, null, null, null,
                        null, null);
        final IncomingInvoiceItem item3OnInv1 = incomingInvoiceItemRepository
                .addItem(invoice1, IncomingInvoiceType.CAPEX, null, null, null, null, null, null, null, null, null,
                        null, null);

        final IncomingInvoice invoice2 = createIncomingInvoice();
        final IncomingInvoiceItem item1OnInv2 = incomingInvoiceItemRepository
                .addItem(invoice2, IncomingInvoiceType.CAPEX, null, null, null, null, null, null, null, null, null,
                        null, null);

        // when, then
        assertThat(incomingInvoiceQueryObjectRepo.findByInvoiceItemReportedDate(null)).hasSize(4);

        // and when
        final LocalDate reportedDate = new LocalDate(2020, 1, 1);
        item1OnInv1.setReportedDate(reportedDate);

        // then
        assertThat(incomingInvoiceQueryObjectRepo.findByInvoiceItemReportedDate(reportedDate)).hasSize(1);
        assertThat(incomingInvoiceQueryObjectRepo.findByInvoiceItemReportedDate(null)).hasSize(3);

    }

    @Test
    public void findByFixedAssetAndTypeAndReportedDateAndBuyerAtPath_works() throws Exception {
        // given
        final IncomingInvoice invoice1 = createIncomingInvoice();
        final IncomingInvoiceItem item1OnInv1 = incomingInvoiceItemRepository
                .addItem(invoice1, IncomingInvoiceType.CAPEX, null, null, null, null, null, null, null, null, Property_enum.OxfGb.findUsing(serviceRegistry),
                        null, null);
        final IncomingInvoiceItem item2OnInv1 = incomingInvoiceItemRepository
                .addItem(invoice1, IncomingInvoiceType.CAPEX, null, null, null, null, null, null, null, null, null,
                        null, null);

        // when, then
        assertThat(incomingInvoiceQueryObjectRepo.findByFixedAssetReferenceAndItemTypeAndReportedDateAndBuyerAtPath(
                Property_enum.OxfGb.findUsing(serviceRegistry).getReference(),
                IncomingInvoiceType.CAPEX,
                null,
                OrganisationAndComms_enum.HelloWorldGb.findUsing(serviceRegistry).getAtPath()

        )).hasSize(1);
        assertThat(incomingInvoiceQueryObjectRepo.findByFixedAssetReferenceAndItemTypeAndReportedDateAndBuyerAtPath(
                null,
                IncomingInvoiceType.CAPEX,
                null,
                OrganisationAndComms_enum.HelloWorldGb.findUsing(serviceRegistry).getAtPath()

        )).hasSize(1);
        assertThat(incomingInvoiceQueryObjectRepo.findByFixedAssetReferenceAndItemTypeAndReportedDateAndBuyerAtPath(
                null,
                null,
                null,
                OrganisationAndComms_enum.HelloWorldGb.findUsing(serviceRegistry).getAtPath()

        )).hasSize(0);
        assertThat(incomingInvoiceQueryObjectRepo.findByFixedAssetReferenceAndItemTypeAndReportedDateAndBuyerAtPath(
                null,
                IncomingInvoiceType.INTERCOMPANY,
                null,
                OrganisationAndComms_enum.HelloWorldGb.findUsing(serviceRegistry).getAtPath()

        )).hasSize(0);
        assertThat(incomingInvoiceQueryObjectRepo.findByFixedAssetReferenceAndItemTypeAndReportedDateAndBuyerAtPath(
                null,
                IncomingInvoiceType.CAPEX,
                null,
                null

        )).hasSize(0);
        assertThat(incomingInvoiceQueryObjectRepo.findByFixedAssetReferenceAndItemTypeAndReportedDateAndBuyerAtPath(
                null,
                IncomingInvoiceType.CAPEX,
                null,
                "/XXX"

        )).hasSize(0);

        // and when
        LocalDate reportedDate = new LocalDate(2020,1,1);
        item1OnInv1.setReportedDate(reportedDate);

        // then
        assertThat(incomingInvoiceQueryObjectRepo.findByFixedAssetReferenceAndItemTypeAndReportedDateAndBuyerAtPath(
                Property_enum.OxfGb.findUsing(serviceRegistry).getReference(),
                IncomingInvoiceType.CAPEX,
                null,
                OrganisationAndComms_enum.HelloWorldGb.findUsing(serviceRegistry).getAtPath()

        )).hasSize(0);
        assertThat(incomingInvoiceQueryObjectRepo.findByFixedAssetReferenceAndItemTypeAndReportedDateAndBuyerAtPath(
                Property_enum.OxfGb.findUsing(serviceRegistry).getReference(),
                IncomingInvoiceType.CAPEX,
                reportedDate,
                OrganisationAndComms_enum.HelloWorldGb.findUsing(serviceRegistry).getAtPath()

        )).hasSize(1);

    }

    @Test
    public void findByTypeAndReportedDateAndBuyerAtPath_works() throws Exception {
        // given
        final IncomingInvoice invoice1 = createIncomingInvoice();
        final IncomingInvoiceItem item1OnInv1 = incomingInvoiceItemRepository
                .addItem(invoice1, IncomingInvoiceType.CAPEX, null, null, null, null, null, null, null, null, Property_enum.OxfGb.findUsing(serviceRegistry),
                        null, null);
        final IncomingInvoiceItem item2OnInv1 = incomingInvoiceItemRepository
                .addItem(invoice1, IncomingInvoiceType.SERVICE_CHARGES, null, null, null, null, null, null, null, null, null,
                        null, null);

        // when, then
        assertThat(incomingInvoiceQueryObjectRepo.findByItemTypeAndReportedDateAndBuyerAtPath(
                IncomingInvoiceType.CAPEX,
                null,
                OrganisationAndComms_enum.HelloWorldGb.findUsing(serviceRegistry).getAtPath()

        )).hasSize(1);
        assertThat(incomingInvoiceQueryObjectRepo.findByItemTypeAndReportedDateAndBuyerAtPath(
                IncomingInvoiceType.SERVICE_CHARGES,
                null,
                OrganisationAndComms_enum.HelloWorldGb.findUsing(serviceRegistry).getAtPath()

        )).hasSize(1);
        assertThat(incomingInvoiceQueryObjectRepo.findByItemTypeAndReportedDateAndBuyerAtPath(
                null,
                null,
                OrganisationAndComms_enum.HelloWorldGb.findUsing(serviceRegistry).getAtPath()

        )).hasSize(0);
        assertThat(incomingInvoiceQueryObjectRepo.findByItemTypeAndReportedDateAndBuyerAtPath(
                IncomingInvoiceType.INTERCOMPANY,
                null,
                OrganisationAndComms_enum.HelloWorldGb.findUsing(serviceRegistry).getAtPath()

        )).hasSize(0);
        assertThat(incomingInvoiceQueryObjectRepo.findByItemTypeAndReportedDateAndBuyerAtPath(
                IncomingInvoiceType.CAPEX,
                null,
                null

        )).hasSize(0);
        assertThat(incomingInvoiceQueryObjectRepo.findByItemTypeAndReportedDateAndBuyerAtPath(
                IncomingInvoiceType.CAPEX,
                null,
                "/XXX"

        )).hasSize(0);

        // and when
        LocalDate reportedDate = new LocalDate(2020,1,1);
        item1OnInv1.setReportedDate(reportedDate);

        // then
        assertThat(incomingInvoiceQueryObjectRepo.findByItemTypeAndReportedDateAndBuyerAtPath(
                IncomingInvoiceType.CAPEX,
                null,
                OrganisationAndComms_enum.HelloWorldGb.findUsing(serviceRegistry).getAtPath()

        )).hasSize(0);
        assertThat(incomingInvoiceQueryObjectRepo.findByItemTypeAndReportedDateAndBuyerAtPath(
                IncomingInvoiceType.SERVICE_CHARGES,
                null,
                OrganisationAndComms_enum.HelloWorldGb.findUsing(serviceRegistry).getAtPath()

        )).hasSize(1);
        assertThat(incomingInvoiceQueryObjectRepo.findByItemTypeAndReportedDateAndBuyerAtPath(
                IncomingInvoiceType.CAPEX,
                reportedDate,
                OrganisationAndComms_enum.HelloWorldGb.findUsing(serviceRegistry).getAtPath()

        )).hasSize(1);
        assertThat(incomingInvoiceQueryObjectRepo.findByItemTypeAndReportedDateAndBuyerAtPath(
                IncomingInvoiceType.SERVICE_CHARGES,
                reportedDate,
                OrganisationAndComms_enum.HelloWorldGb.findUsing(serviceRegistry).getAtPath()

        )).hasSize(0);

    }

    @Test
    public void findByFixedAssetReferenceAndReportedDateAndBuyerAtPath_works() throws Exception {
        // given
        final IncomingInvoice invoice1 = createIncomingInvoice();
        final IncomingInvoiceItem item1OnInv1 = incomingInvoiceItemRepository
                .addItem(invoice1, IncomingInvoiceType.CAPEX, null, null, null, null, null, null, null, null, Property_enum.OxfGb.findUsing(serviceRegistry),
                        null, null);
        final IncomingInvoiceItem item2OnInv1 = incomingInvoiceItemRepository
                .addItem(invoice1, IncomingInvoiceType.SERVICE_CHARGES, null, null, null, null, null, null, null, null, null,
                        null, null);

        // when, then
        assertThat(incomingInvoiceQueryObjectRepo.findByFixedAssetReferenceAndReportedDateAndBuyerAtPath(
                Property_enum.OxfGb.findUsing(serviceRegistry).getReference(),
                null,
                OrganisationAndComms_enum.HelloWorldGb.findUsing(serviceRegistry).getAtPath()

        )).hasSize(1);
        assertThat(incomingInvoiceQueryObjectRepo.findByFixedAssetReferenceAndReportedDateAndBuyerAtPath(
                null,
                null,
                OrganisationAndComms_enum.HelloWorldGb.findUsing(serviceRegistry).getAtPath()

        )).hasSize(1);
        assertThat(incomingInvoiceQueryObjectRepo.findByFixedAssetReferenceAndReportedDateAndBuyerAtPath(
                "XXX",
                null,
                OrganisationAndComms_enum.HelloWorldGb.findUsing(serviceRegistry).getAtPath()

        )).hasSize(0);
        assertThat(incomingInvoiceQueryObjectRepo.findByFixedAssetReferenceAndReportedDateAndBuyerAtPath(
                null,
                null,
                null

        )).hasSize(0);
        assertThat(incomingInvoiceQueryObjectRepo.findByFixedAssetReferenceAndReportedDateAndBuyerAtPath(
                null,
                null,
                "/XXX"

        )).hasSize(0);

        // and when
        LocalDate reportedDate = new LocalDate(2020,1,1);
        item1OnInv1.setReportedDate(reportedDate);

        // then
        assertThat(incomingInvoiceQueryObjectRepo.findByFixedAssetReferenceAndReportedDateAndBuyerAtPath(
                Property_enum.OxfGb.findUsing(serviceRegistry).getReference(),
                null,
                OrganisationAndComms_enum.HelloWorldGb.findUsing(serviceRegistry).getAtPath()

        )).hasSize(0);
        assertThat(incomingInvoiceQueryObjectRepo.findByFixedAssetReferenceAndReportedDateAndBuyerAtPath(
                Property_enum.OxfGb.findUsing(serviceRegistry).getReference(),
                reportedDate,
                OrganisationAndComms_enum.HelloWorldGb.findUsing(serviceRegistry).getAtPath()

        )).hasSize(1);

    }

    private IncomingInvoice createIncomingInvoice() {
        seller = OrganisationAndComms_enum.TopModelGb.findUsing(serviceRegistry);
        buyer = OrganisationAndComms_enum.HelloWorldGb.findUsing(serviceRegistry);
        property = Property_enum.OxfGb.findUsing(serviceRegistry);
        invoiceNumber = "123";
        invoiceDate = new LocalDate(2017, 1, 1);
        dueDate = invoiceDate.minusMonths(1);
        final LocalDate vatRegistrationDate = null;
        paymentMethod = PaymentMethod.BANK_TRANSFER;
        invoiceStatus = InvoiceStatus.NEW;
        atPath = "/GBR";
        approvalStateIfAny = IncomingInvoiceApprovalState.PAID;

        final LocalDate dateReceived = null;
        final BankAccount bankAccount = null;
        final boolean postedToCodaBooks = false;
        final LocalDate paidDate = null;

        return incomingInvoiceRepository.create(IncomingInvoiceType.CAPEX, invoiceNumber, property, atPath, buyer, seller, invoiceDate, dueDate,
                vatRegistrationDate, paymentMethod, invoiceStatus, dateReceived, bankAccount,
                approvalStateIfAny, postedToCodaBooks, paidDate);
    }

    @Inject
    IncomingInvoiceQueryObjectRepo incomingInvoiceQueryObjectRepo;

    @Inject IncomingInvoiceItemRepository incomingInvoiceItemRepository;

}

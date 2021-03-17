package org.estatio.module.capex.integtests.invoice;

import java.math.BigInteger;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.fixtures.enums.Country_enum;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.role.FixedAssetRoleTypeEnum;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.capex.app.invoice.IncomingInvoiceTemplateViewModel;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItemRepository;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_complete;
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_discard;
import org.estatio.module.capex.fixtures.incominginvoice.enums.IncomingInvoice_enum;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.capex.seed.DocumentTypesAndTemplatesForCapexFixture;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.charge.fixtures.incoming.builders.IncomingChargesFraXlsxFixture;
import org.estatio.module.charge.fixtures.incoming.enums.IncomingCharge_enum;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.financial.dom.BankAccountRepository;
import org.estatio.module.financial.fixtures.bankaccount.enums.BankAccount_enum;
import org.estatio.module.invoice.dom.InvoiceStatus;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.role.PartyRoleTypeRepository;
import org.estatio.module.party.fixtures.orgcomms.enums.OrganisationAndComms_enum;

import static org.assertj.core.api.Assertions.assertThat;

public class IncomingInvoice_IntegTest extends CapexModuleIntegTestAbstract {

    Property propertyForViv;
    Party buyer;
    Party seller;

    Country france;
    Charge charge_for_marketing;
    Charge charge_for_other;

    IncomingInvoice incomingInvoice;

    BankAccount bankAccount;

    @Before
    public void setupData() {

        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext ec) {
                ec.executeChildren(this,
                        new DocumentTypesAndTemplatesForCapexFixture(),
                        new IncomingChargesFraXlsxFixture());

                ec.executeChildren(this,
                        IncomingInvoice_enum.fakeInvoice2Pdf,
                        BankAccount_enum.TopModelFr);
            }
        });
    }

    @Test
    public void when_discarded_reported_items_are_reversed_while_no_correction_item_is_added() {

        // given
        incomingInvoiceSetup();
        incomingInvoiceIsCompleted();
        IncomingInvoiceItem reportedItem = firstItemIsReported();

        // when
        invoiceIsDiscarded();

        // then
        assertThat(incomingInvoice.getItems().size()).isEqualTo(3);
        IncomingInvoiceItem reversal = (IncomingInvoiceItem) incomingInvoice.getItems().first();
        assertThat(reversal.getReversalOf()).isEqualTo(reportedItem);
        assertThat(reversal.getNetAmount()).isEqualTo(reportedItem.getNetAmount().negate());
        assertThat(reversal.getReportedDate()).isNull();

    }

    private void incomingInvoiceSetup() {
        propertyForViv = Property_enum.VivFr.findUsing(serviceRegistry);

        buyer = OrganisationAndComms_enum.HelloWorldFr.findUsing(serviceRegistry);
        seller = OrganisationAndComms_enum.TopModelFr.findUsing(serviceRegistry);

        france = Country_enum.FRA.findUsing(serviceRegistry);
        charge_for_marketing = chargeRepository.findByReference(IncomingCharge_enum.FrMarketing.getReference());
        charge_for_other = chargeRepository.findByReference(IncomingCharge_enum.FrOther.getReference());

        bankAccount = bankAccountRepository.findBankAccountByReference(seller, BankAccount_enum.TopModelFr.getIban());

        incomingInvoice = incomingInvoiceRepository.findByInvoiceNumberAndSellerAndInvoiceDate("65432", seller, new LocalDate(2014, 5, 13));
        incomingInvoice.setBankAccount(bankAccount);

        assertThat(incomingInvoice).isNotNull();
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.NEW);
        assertThat(incomingInvoice.getItems().size()).isEqualTo(2);

    }

    private void incomingInvoiceIsCompleted() {
        mixin(IncomingInvoice_complete.class, incomingInvoice).act(FixedAssetRoleTypeEnum.PROPERTY_MANAGER.findUsing(partyRoleTypeRepository), null, null);
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.COMPLETED);
    }

    private IncomingInvoiceItem firstItemIsReported() {
        IncomingInvoiceItem reportedItem = (IncomingInvoiceItem) incomingInvoice.getItems().first();
        reportedItem.setReportedDate(LocalDate.now());
        return reportedItem;
    }

    private void invoiceIsDiscarded() {
        mixin(IncomingInvoice_discard.class, incomingInvoice).act("some reason");
    }

    @Test
    public void when_incoming_invoice_type_is_changed_reported_items_are_reversed() {

        // given
        incomingInvoiceSetup();
        incomingInvoiceIsCompleted();
        IncomingInvoiceItem reportedItem = firstItemIsReported();
        assertThat(reportedItem.getCharge()).isEqualTo(charge_for_marketing);
        IncomingInvoiceItem unreportedItem = (IncomingInvoiceItem) incomingInvoice.getItems().last();
        assertThat(unreportedItem.getCharge()).isEqualTo(charge_for_other);

        assertThat(incomingInvoice.getType()).isEqualTo(IncomingInvoiceType.CAPEX);
        assertThat(reportedItem.getIncomingInvoiceType()).isEqualTo(IncomingInvoiceType.CAPEX);
        assertThat(unreportedItem.getIncomingInvoiceType()).isEqualTo(IncomingInvoiceType.PROPERTY_EXPENSES);

        // when
        incomingInvoice.editType(IncomingInvoiceType.SERVICE_CHARGES, true);

        // then
        assertThat(incomingInvoice.getType()).isEqualTo(IncomingInvoiceType.SERVICE_CHARGES);

        assertThat(incomingInvoice.getItems().size()).isEqualTo(4);
        assertThat(unreportedItem.getIncomingInvoiceType()).isEqualTo(IncomingInvoiceType.SERVICE_CHARGES);
        assertThat(reportedItem.getIncomingInvoiceType()).isEqualTo(IncomingInvoiceType.CAPEX);

        IncomingInvoiceItem reversalOfReportedItem = incomingInvoiceItemRepository.findByInvoiceAndChargeAndSequence(incomingInvoice, charge_for_marketing, BigInteger.valueOf(3));
        assertThat(reversalOfReportedItem.getCharge()).isEqualTo(charge_for_marketing);
        assertThat(reversalOfReportedItem.getIncomingInvoiceType()).isEqualTo(IncomingInvoiceType.CAPEX);

        IncomingInvoiceItem correctionOfReportedItem = incomingInvoiceItemRepository.findByInvoiceAndChargeAndSequence(incomingInvoice, charge_for_marketing, BigInteger.valueOf(4));
        assertThat(reversalOfReportedItem.getCharge()).isEqualTo(charge_for_marketing);
        assertThat(correctionOfReportedItem.getIncomingInvoiceType()).isEqualTo(IncomingInvoiceType.SERVICE_CHARGES);

        // and when changing type again
        incomingInvoice.editType(IncomingInvoiceType.PROPERTY_EXPENSES, true);

        // then still
        assertThat(incomingInvoice.getItems().size()).isEqualTo(4);
        assertThat(incomingInvoice.getType()).isEqualTo(IncomingInvoiceType.PROPERTY_EXPENSES);

    }

    @Test
    public void complete_using_template() throws Exception {
        // given
        incomingInvoiceSetup();
        incomingInvoice.setUseAsTemplate(Boolean.TRUE);

        final IncomingInvoice newInvoice = incomingInvoiceRepository.create(
                null,
                null,
                null,
                "/FRA",
                incomingInvoice.getBuyer(),
                null,
                null,
                null,
                null,
                null,
                InvoiceStatus.NEW,
                null,
                null,
                null,
                false,
                null);

        // when
        final List<IncomingInvoiceTemplateViewModel> templates = newInvoice.choices1CompleteUsingTemplate(incomingInvoice.getSeller());
        assertThat(templates).hasSize(1);

        IncomingInvoiceTemplateViewModel template = templates.get(0);
        final LocalDate newDateReceived = LocalDate.parse("2019-01-01");
        final LocalDate newInvoiceDate = LocalDate.parse("2019-01-01");
        final LocalDate newDueDate = LocalDate.parse("2019-01-02");
        newInvoice.completeUsingTemplate(
                template.getSupplier(),
                template,
                newDateReceived,
                newInvoiceDate,
                newDueDate,
                "New invoice number",
                null,
                "F2019");

        final IncomingInvoiceItem invoiceItem = (IncomingInvoiceItem) incomingInvoice.getItems().first();
        final IncomingInvoiceItem newItem = (IncomingInvoiceItem) newInvoice.getItems().first();

        // then assert the same
        assertThat(newInvoice.getType()).isEqualTo(incomingInvoice.getType());
        assertThat(newInvoice.getSeller()).isEqualTo(incomingInvoice.getSeller());
        assertThat(newInvoice.getBankAccount()).isEqualTo(incomingInvoice.getBankAccount());
        assertThat(newInvoice.getPaymentMethod()).isEqualTo(incomingInvoice.getPaymentMethod());
        assertThat(newInvoice.getProperty()).isEqualTo(incomingInvoice.getProperty());
        assertThat(newInvoice.getNetAmount()).isEqualTo(incomingInvoice.getNetAmount());
        assertThat(newInvoice.getGrossAmount()).isEqualTo(incomingInvoice.getGrossAmount());
        assertThat(newInvoice.getCurrency()).isEqualTo(incomingInvoice.getCurrency());

        assertThat(newItem.getDescription()).isEqualTo(invoiceItem.getDescription());
        assertThat(newItem.getCharge()).isEqualTo(invoiceItem.getCharge());
        assertThat(newItem.getNetAmount()).isEqualTo(invoiceItem.getNetAmount());
        assertThat(newItem.getGrossAmount()).isEqualTo(invoiceItem.getGrossAmount());
        assertThat(newItem.getVatAmount()).isEqualTo(invoiceItem.getVatAmount());
        assertThat(newItem.getTax()).isEqualTo(invoiceItem.getTax());
        assertThat(newItem.getFixedAsset()).isEqualTo(invoiceItem.getFixedAsset());

        // and assert not the same
        assertThat(newInvoice.getDateReceived()).isEqualTo(newDateReceived);
        assertThat(newInvoice.getInvoiceDate()).isEqualTo(newInvoiceDate);
        assertThat(newInvoice.getDueDate()).isEqualTo(newDueDate);
        assertThat(newInvoice.getInvoiceNumber()).isEqualTo("New invoice number");

        assertThat(newItem.getDueDate()).isEqualTo(newDueDate);
        assertThat(newItem.getPeriod()).isEqualTo("F2019");
        assertThat(newItem.getStartDate()).isEqualTo(LocalDate.parse("2018-07-01"));
        assertThat(newItem.getEndDate()).isEqualTo(LocalDate.parse("2019-06-30"));
        assertThat(newItem.getBudgetItem()).isEqualTo(null);
    }

    @Inject
    IncomingInvoiceItemRepository incomingInvoiceItemRepository;

    @Inject
    IncomingInvoiceRepository incomingInvoiceRepository;

    @Inject
    ChargeRepository chargeRepository;

    @Inject
    BankAccountRepository bankAccountRepository;

    @Inject PartyRoleTypeRepository partyRoleTypeRepository;

}


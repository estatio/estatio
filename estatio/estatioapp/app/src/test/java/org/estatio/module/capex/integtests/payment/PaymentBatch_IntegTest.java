package org.estatio.module.capex.integtests.payment;

import java.util.Arrays;

import javax.inject.Inject;

import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.sudo.SudoService;
import org.estatio.module.asset.dom.role.FixedAssetRoleTypeEnum;
import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_approve;
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_complete;
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_markAsPaidByIbp;
import org.estatio.module.financial.dom.bankaccount.verification.BankAccountVerificationState;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.party.dom.role.PartyRoleType;
import org.estatio.module.party.dom.role.PartyRoleTypeEnum;
import org.estatio.module.party.dom.role.PartyRoleTypeRepository;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.wrapper.DisabledException;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.payment.PaymentBatch;
import org.estatio.module.capex.dom.payment.PaymentBatchRepository;
import org.estatio.module.capex.dom.payment.PaymentLine;
import org.estatio.module.capex.dom.payment.approval.PaymentBatchApprovalState;
import org.estatio.module.capex.dom.payment.approval.triggers.PaymentBatch_complete;
import org.estatio.module.capex.fixtures.incominginvoice.enums.IncomingInvoice_enum;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.capex.seed.DocumentTypesAndTemplatesForCapexFixture;
import org.estatio.module.charge.fixtures.incoming.builders.IncomingChargesFraXlsxFixture;
import org.estatio.module.currency.dom.Currency;
import org.estatio.module.currency.fixtures.enums.Currency_enum;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.financial.fixtures.bankaccount.enums.BankAccount_enum;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.fixtures.organisation.enums.Organisation_enum;

import static org.assertj.core.api.Assertions.assertThat;

public class PaymentBatch_IntegTest extends CapexModuleIntegTestAbstract {

    PaymentBatch paymentBatch;
    BankAccount debtorBankAccount;
    BankAccount sellerBankAccount;
    Organisation sellerParty;
    Currency currency;
    IncomingInvoice invoice1;
    IncomingInvoice invoice2;

    @Before
    public void setUp() throws Exception {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChildren(this,
                        new DocumentTypesAndTemplatesForCapexFixture(),
                        new IncomingChargesFraXlsxFixture());

                executionContext.executeChildren(this,
                        BankAccount_enum.TopModelFr,
                        BankAccount_enum.TopSellerFr,
                        Currency_enum.EUR,
                        IncomingInvoice_enum.fakeInvoice2Pdf,
                        IncomingInvoice_enum.fakeInvoice3Pdf,
                        Person_enum.BrunoTreasurerFr,
                        Person_enum.BertrandIncomingInvoiceManagerFr);
            }
        });

        currency = Currency_enum.EUR.findUsing(serviceRegistry);
        debtorBankAccount = BankAccount_enum.TopModelFr.findUsing(serviceRegistry);
        sellerBankAccount = BankAccount_enum.TopSellerFr.findUsing(serviceRegistry);
        sellerParty = Organisation_enum.TopSellerFr.findUsing(serviceRegistry);
        invoice1 = IncomingInvoice_enum.fakeInvoice2Pdf.findUsing(serviceRegistry);
        invoice2 = IncomingInvoice_enum.fakeInvoice3Pdf.findUsing(serviceRegistry);

        paymentBatch = paymentBatchRepository.create(DateTime.parse("2018-01-01T12:00:00"), debtorBankAccount, PaymentBatchApprovalState.NEW);
    }

    @Test
    public void createAndCompleteUrgentPaymentBatch_works() throws Exception {
        // given
        final DateTime creationDateTime = DateTime.parse("2018-02-01T12:00:00");

        paymentBatch.addLineIfRequired(invoice1); // sequence = 1
        paymentBatch.addLineIfRequired(invoice2); // sequence = 2
        assertThat(paymentBatch.getLines()).hasSize(2);

        // when
        final PaymentBatch urgentPaymentBatch = paymentBatch.createAndCompleteUrgentPaymentBatch(Arrays.asList(invoice2), creationDateTime.plusHours(2), "Urgent payment");

        // then
        assertThat(paymentBatch.getLines()).hasSize(1);
        assertThat(paymentBatch.getLines())
                .extracting(PaymentLine::getInvoice)
                .containsExactly(invoice1);
        assertThat(paymentBatch.getApprovalState()).isEqualTo(PaymentBatchApprovalState.NEW);

        assertThat(urgentPaymentBatch.getLines()).hasSize(1);
        assertThat(urgentPaymentBatch.getLines())
                .extracting(PaymentLine::getInvoice)
                .containsExactly(invoice2);
        assertThat(urgentPaymentBatch.getApprovalState()).isEqualTo(PaymentBatchApprovalState.COMPLETED);
    }

    @Test
    public void reasonGuardNotSatisfied_works() throws Exception {
        // given
        ((Organisation) invoice1.getSeller()).setChamberOfCommerceCode(null);
        ((Organisation) invoice2.getSeller()).setChamberOfCommerceCode(null);
        paymentBatch.addLineIfRequired(invoice1); // sequence = 1
        paymentBatch.addLineIfRequired(invoice2); // sequence = 2
        final PaymentBatch_complete mixin = factoryService.mixin(PaymentBatch_complete.class, paymentBatch);

        transactionService.nextTransaction();

        // expected
        expectedExceptions.expect(DisabledException.class);
        expectedExceptions.expectMessage("Cannot complete payment batch because the following organisations are missing a chamber of commerce code: Topmodel Fashion [TOPMODEL_FR], Topseller goods [TOPSELLER_FR]");

        // when
        wrap(mixin).act(mixin.default0Act(), "Complete");

        // and given
        ((Organisation) invoice1.getSeller()).setChamberOfCommerceCode("Code");

        // then expected
        expectedExceptions.expect(DisabledException.class);
        expectedExceptions.expectMessage("Cannot complete payment batch because the following organisations are missing a chamber of commerce code: Topseller goods [TOPSELLER_FR]");

        // when
        wrap(mixin).act(mixin.default0Act(), "Complete");

        // and given
        ((Organisation) invoice2.getSeller()).setChamberOfCommerceCode("Code");

        // when
        wrap(mixin).act(mixin.default0Act(), "Complete");

        // then
        assertThat(paymentBatch.getApprovalState()).isEqualTo(PaymentBatchApprovalState.COMPLETED);

    }

    @Test
    public void paymentBatch_is_set_to_paid_automatically() throws Exception {
        // given
        paymentBatch.addLineIfRequired(invoice2);
        wrap(mixin(PaymentBatch_complete.class, paymentBatch)).act(DateTime.parse("2018-02-01T12:00:00"), null);
        assertThat(paymentBatch.getApprovalState()).isEqualTo(PaymentBatchApprovalState.COMPLETED);
        assertThat(paymentBatch.getLines()).hasSize(1);

        PaymentBatch paymentBatch2 = paymentBatchRepository.create(DateTime.parse("2018-01-01T12:00:00"), debtorBankAccount, PaymentBatchApprovalState.NEW);
        paymentBatch2.addLineIfRequired(invoice1);
        paymentBatch2.addLineIfRequired(invoice2);
        wrap(mixin(PaymentBatch_complete.class, paymentBatch2)).act(DateTime.parse("2018-02-01T12:00:00"), null);
        assertThat(paymentBatch2.getApprovalState()).isEqualTo(PaymentBatchApprovalState.COMPLETED);
        assertThat(paymentBatch2.getLines()).hasSize(2);

        assertThat(invoice2.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.PAYABLE);

        // when
        wrap(mixin(IncomingInvoice_markAsPaidByIbp.class, invoice2)).act();
        assertThat(invoice2.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.PAID);

        // then
        assertThat(paymentBatch.getApprovalState()).isEqualTo(PaymentBatchApprovalState.PAID);
        assertThat(paymentBatch2.getApprovalState()).isNotEqualTo(PaymentBatchApprovalState.PAID);
    }


    @Inject
    PaymentBatchRepository paymentBatchRepository;

    @Inject
    FactoryService factoryService;

    @Inject
    QueryResultsCache queryResultsCache;

    @Inject
    PartyRoleTypeRepository partyRoleTypeRepository;

    @Inject
    SudoService sudoService;

}

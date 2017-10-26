package org.estatio.capex.dom.payment.manager;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.assertj.core.util.Lists;
import org.joda.time.DateTime;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.CommandReification;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.jdosupport.IsisJdoSupport;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.wrapper.WrapperFactory;
import org.apache.isis.applib.services.xactn.TransactionService;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.applib.value.Clob;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.capex.dom.payment.CreditTransfer;
import org.estatio.capex.dom.payment.PaymentBatch;
import org.estatio.capex.dom.payment.PaymentBatchRepository;
import org.estatio.capex.dom.payment.PaymentLine;
import org.estatio.capex.dom.payment.approval.triggers.PaymentBatch_complete;
import org.estatio.dom.asset.Property;
import org.estatio.dom.assetfinancial.FixedAssetFinancialAccount;
import org.estatio.dom.assetfinancial.FixedAssetFinancialAccountRepository;
import org.estatio.dom.financial.FinancialAccount;
import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.financial.bankaccount.BankAccountRepository;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.party.Party;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@DomainObject(
        objectType = "org.estatio.capex.dom.payment.PaymentBatchManager"
        ,nature = Nature.VIEW_MODEL
)
public class PaymentBatchManager {

    public PaymentBatchManager() {
        this(null);
    }

    public PaymentBatchManager(final Integer selectedBatchIdx) {
        this.selectedBatchIdx = selectedBatchIdx;
    }

    public String title() {
        return "Payment Batch Manager";
    }


    @Getter @Setter
    private Integer selectedBatchIdx;


    public PaymentBatch getSelectedBatch() {
        final Integer selectedBatchIdx = getSelectedBatchIdx();
        if(selectedBatchIdx == null) {
            return null;
        }
        final List<PaymentBatch> newBatches = getNewBatches();
        if (newBatches.size() == 0) {
            return null;
        }

        return newBatches.get(selectedBatchIdx);
    }


    public List<CreditTransfer> getSelectedBatchTransfers(){
        final PaymentBatch selectedBatch = getSelectedBatch();
        if (selectedBatch == null) {
            return Collections.emptyList();
        }
        return Lists.newArrayList(selectedBatch.getTransfers());
    }

    public List<PaymentLine> getSelectedBatchPaymentLines(){
        final PaymentBatch selectedBatch = getSelectedBatch();
        if (selectedBatch == null) {
            return Collections.emptyList();
        }
        return Lists.newArrayList(selectedBatch.getLines());
    }


    public List<PaymentBatch> getNewBatches() {
        return paymentBatchRepository.findNewBatches();
    }


    public List<PaymentBatch> getCompletedBatches() {
        return paymentBatchRepository.findCompletedBatches();
    }


    public List<IncomingInvoice> getPayableInvoicesNotInAnyBatch() {
        return incomingInvoiceRepository.findNotInAnyPaymentBatchByApprovalStateAndPaymentMethod(
                IncomingInvoiceApprovalState.PAYABLE,
                PaymentMethod.BANK_TRANSFER);
    }



    public static enum Selection {
        ALL_POSSIBLE,
        HAND_PICK
    }

    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            command = CommandReification.DISABLED,
            publishing = Publishing.DISABLED
    )
    public PaymentBatchManager autoCreateBatches(
            final Selection selection,
            @Nullable final List<IncomingInvoice> payableInvoices) {

        final List<IncomingInvoice> invoicesToPay =
                selection == Selection.ALL_POSSIBLE
                        ? getPayableInvoicesNotInAnyBatchWithBankAccountAndBic()
                        : payableInvoices; // ie, HAND_PICK (as provided as parameter)

        for (final IncomingInvoice payableInvoice : invoicesToPay) {
            final BankAccount uniqueBankAccountIfAny = uniqueDebtorAccountToPay(payableInvoice);
            if (uniqueBankAccountIfAny != null && uniqueBankAccountIfAny.getBic() != null) {
                // should be true, because those that don't pass this are filtered out in choicesXxx anyway.
                PaymentBatch paymentBatch = paymentBatchRepository.findOrCreateNewByDebtorBankAccount(uniqueBankAccountIfAny);
                paymentBatch.addLineIfRequired(payableInvoice);
            }
        }

        final List<PaymentBatch> newBatches = getNewBatches();
        for (final PaymentBatch paymentBatch : newBatches) {
            removeNegativeTransfers(paymentBatch);
        }

        return new PaymentBatchManager(newBatches.isEmpty()? null: 0);
    }

    /**
     * Removes (deletes) all {@link PaymentLine line}s that correspond to a transfer which is a net negative.
     */
    private void removeNegativeTransfers(PaymentBatch paymentBatch) {

        final List<PaymentLine> paymentLines = paymentBatch.getTransfers()
                .stream()
                .filter(x -> x.getAmount().compareTo(BigDecimal.ZERO) <= 0)
                .map(CreditTransfer::getLines)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        // seems to be necessary to flush everything through both before and after, presumably to deal with the
        // dependent collection (else get DN error about having added a deleted object to the batch's lines collection)
        flushTransaction();

        // this is sufficient, because PaymentBatch#lines is a dependent collection
        for (PaymentLine paymentLine : paymentLines){
            paymentBatch.getLines().remove(paymentLine);
        }

        // see discussion above.
        flushTransaction();

        if(paymentBatch.getLines().isEmpty()) {
            paymentBatch.remove();
        }
    }

    public Selection default0AutoCreateBatches() {
        return Selection.ALL_POSSIBLE;
    }

    public List<IncomingInvoice> choices1AutoCreateBatches(final Selection selection) {
        List<IncomingInvoice> choices = Lists.newArrayList();

        final List<IncomingInvoice> invoices = getPayableInvoicesNotInAnyBatchWithBankAccountAndBic();
        for (final IncomingInvoice payableInvoice : invoices) {
            final BankAccount uniqueBankAccountIfAny = uniqueDebtorAccountToPay(payableInvoice);
            if (uniqueBankAccountIfAny != null && uniqueBankAccountIfAny.getBic() != null) {
                choices.add(payableInvoice);
            }
        }

        return choices;
    }

    public String validateAutoCreateBatches(
            final Selection selection,
            final List<IncomingInvoice> payableInvoices) {
        if (selection == Selection.HAND_PICK && payableInvoices.isEmpty()) {
            return "Select one or more invoices";
        }
        if (selection == Selection.ALL_POSSIBLE && !payableInvoices.isEmpty()) {
            return "No need to select any invoices, all (possible) will be added";
        }
        return null;
    }

    private List<IncomingInvoice> getPayableInvoicesNotInAnyBatchWithBankAccountAndBic() {
        return getPayableInvoicesNotInAnyBatch().stream()
                .filter(pi -> pi.getBankAccount() != null && pi.getBankAccount().getBic() != null)
                .collect(Collectors.toList());
    }

    private BankAccount uniqueDebtorAccountToPay(final IncomingInvoice invoice) {
        final Party buyer = invoice.getBuyer();
        List<BankAccount> bankAccountsForBuyer = bankAccountRepository.findBankAccountsByOwner(buyer);

        final Property propertyIfAny = invoice.getProperty();
        if (propertyIfAny != null) {
            List<FixedAssetFinancialAccount> fafrList = fixedAssetFinancialAccountRepository.findByFixedAsset(propertyIfAny);
            List<FinancialAccount> bankAccountsForProperty = fafrList.stream()
                    .map(FixedAssetFinancialAccount::getFinancialAccount)
                    .filter(BankAccount.class::isInstance)
                    .map(BankAccount.class::cast)
                    .collect(Collectors.toList());

            bankAccountsForBuyer.retainAll(bankAccountsForProperty);
        }

        // original implementation ... see if we already have a unique bank account
        int numBankAccounts = bankAccountsForBuyer.size();
        switch (numBankAccounts) {
        case 0:
            return null;
        case 1:
            return bankAccountsForBuyer.get(0);
        default:
            // otherwise, non-unique, so fall through
        }

        // see if removing non-preferred helps
        bankAccountsForBuyer.removeIf(x -> (x.getPreferred() == null || !x.getPreferred()));

        numBankAccounts = bankAccountsForBuyer.size();
        switch (numBankAccounts) {
        case 0:
            return null;
        case 1:
            return bankAccountsForBuyer.get(0);
        default:
            // give up, still non-duplicate
            return null;
        }
    }





    @MemberOrder(name = "newBatches", sequence = "1")
    public PaymentBatchManager removeAll() {
        for (PaymentBatch paymentBatch : getNewBatches()) {
            paymentBatch.clearLines();
            paymentBatch.remove();
        }
        return new PaymentBatchManager();
    }




    @Action(
            semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE,
            command = CommandReification.DISABLED,
            publishing = Publishing.DISABLED
    )
    public PaymentBatchManager reset() {

        final List<PaymentBatch> newBatches = this.getNewBatches();
        for (PaymentBatch newBatch : newBatches) {
            newBatch.clearLines();
        }
        return new PaymentBatchManager();
    }

    public String disableReset() {
        final List<PaymentBatch> newBatches = this.getNewBatches();
        for (PaymentBatch newBatch : newBatches) {
            if (!newBatch.getLines().isEmpty()) {
                return null;
            }
        }
        return "No payment batches to reset";
    }





    @Action(
            semantics = SemanticsOf.SAFE,
            command = CommandReification.DISABLED,
            publishing = Publishing.DISABLED
    )
    public PaymentBatchManager nextBatch() {
        int selectedBatchIdx = this.getSelectedBatchIdx();
        if (selectedBatchIdx >= 0) {
            int nextBatchIdx = ++selectedBatchIdx;
            if (nextBatchIdx < this.getNewBatches().size()) {
                return new PaymentBatchManager(nextBatchIdx);
            }
        }
        return this;
    }

    public String disableNextBatch() {
        if(this.getSelectedBatchIdx() == null) {
            return "No batch selected";
        }
        int selectedBatchIdx = this.getSelectedBatchIdx();
        if (selectedBatchIdx >= 0) {
            int nextBatchIdx = ++selectedBatchIdx;
            if (nextBatchIdx >= this.getNewBatches().size()) {
                return "No more batches";
            }
        }
        return null;
    }




    @Action(
            semantics = SemanticsOf.SAFE,
            command = CommandReification.DISABLED,
            publishing = Publishing.DISABLED
    )
    public PaymentBatchManager previousBatch() {
        int selectedBatchIdx = this.getSelectedBatchIdx();
        if (selectedBatchIdx >= 0) {
            int previousBatchIdx = --selectedBatchIdx;
            if (previousBatchIdx >= 0) {
                return new PaymentBatchManager(previousBatchIdx);
            }
        }
        return this;
    }

    public String disablePreviousBatch() {
        if (this.getNewBatches().isEmpty()) {
            return "No batches";
        }
        if(this.getSelectedBatchIdx() == null) {
            return "No batch selected";
        }
        int selectedBatchIdx = this.getSelectedBatchIdx();
        if (selectedBatchIdx >= 0) {
            int previousBatchIdx = --selectedBatchIdx;
            if (previousBatchIdx < 0) {
                return "No more batches";
            }
        }
        return null;
    }




    @Action(
            semantics = SemanticsOf.SAFE,
            command = CommandReification.DISABLED,
            publishing = Publishing.DISABLED
    )
    public PaymentBatchManager selectBatch(final PaymentBatch paymentBatch) {
        final int selectedBatchIdx = getNewBatches().indexOf(paymentBatch);
        return new PaymentBatchManager(selectedBatchIdx);
    }

    public List<PaymentBatch> choices0SelectBatch() {
        return this.getNewBatches();
    }

    public PaymentBatch default0SelectBatch() {
        return this.getSelectedBatch();
    }

    public String disableSelectBatch() {
        if (this.getNewBatches().isEmpty()) {
            return "No new batches";
        }
        return null;
    }





    @Action(
            semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE,
            command = CommandReification.DISABLED,
            publishing = Publishing.DISABLED
    )
    public PaymentBatchManager completeBatch(
            final DateTime requestedExecutionDate,
            @Nullable final String comment) {
        // use the wrapper factory to generate events
        wrapperFactory.wrap(PaymentBatch_complete()).act(requestedExecutionDate, comment);

        // rather than return this manager, create a new one (force page reload)
        final PaymentBatchManager paymentBatchManager = new PaymentBatchManager();
        serviceRegistry2.injectServicesInto(paymentBatchManager);
        return new PaymentBatchManager(paymentBatchManager.getSelectedBatchIdx());

    }

    public DateTime default0CompleteBatch() {
        return PaymentBatch_complete().default0Act();
    }

    public String validate0CompleteBatch(DateTime proposed) {
        return PaymentBatch_complete().validate0Act(proposed);
    }

    public boolean hideCompleteBatch() {
        return this.getSelectedBatch() == null || PaymentBatch_complete().hideAct();
    }

    public String disableCompleteBatch() {
        if (this.getSelectedBatch() == null) {
            return "No batch selected";
        }
        return PaymentBatch_complete().disableAct();
    }




    @Action(
            semantics = SemanticsOf.SAFE,
            command = CommandReification.DISABLED,
            publishing = Publishing.DISABLED
    )
    public Blob downloadReviewPdf(
            final PaymentBatch paymentBatch,
            @Nullable final String documentName,
            @ParameterLayout(named = "How many first pages of each invoice's PDF?")
            final Integer numFirstPages,
            @ParameterLayout(named = "How many final pages of each invoice's PDF?")
            final Integer numLastPages) throws IOException {
        final String documentNameToUse = documentName != null ? documentName : paymentBatch.fileNameWithSuffix("pdf");
        return paymentBatch.downloadReviewPdf(documentNameToUse, numFirstPages, numLastPages);
    }

    public List<PaymentBatch> choices0DownloadReviewPdf() {
        return this.getCompletedBatches();
    }

    public Integer default2DownloadReviewPdf() {
        return 1;
    }
    public Integer default3DownloadReviewPdf() {
        return 0;
    }

    public String disableDownloadReviewPdf() {
        if (this.getCompletedBatches().isEmpty()) {
            return "No completed batches";
        }
        return null;
    }





    @Action(
            semantics = SemanticsOf.SAFE,
            command = CommandReification.DISABLED,
            publishing = Publishing.DISABLED
    )
    public Clob downloadPaymentFile(
            final PaymentBatch paymentBatch,
            @Nullable final String documentName) {
        final String documentNameToUse = documentName != null ? documentName : paymentBatch.fileNameWithSuffix("xml");
        return factoryService.mixin(PaymentBatch.downloadPaymentFile.class, paymentBatch).act(documentNameToUse);
    }

    public List<PaymentBatch> choices0DownloadPaymentFile() {
        return this.getCompletedBatches();
    }

    public String disableDownloadPaymentFile() {
        if (this.getCompletedBatches().isEmpty()) {
            return "No completed batches";
        }
        return null;
    }





    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            command = CommandReification.DISABLED,
            publishing = Publishing.DISABLED
    )
    public PaymentBatchManager addInvoiceToPayByBankAccount(
            final IncomingInvoice incomingInvoice,
            final BankAccount debtorBankAccount) {

        PaymentBatch paymentBatch = paymentBatchRepository.findOrCreateNewByDebtorBankAccount(debtorBankAccount);
        paymentBatch.addLineIfRequired(incomingInvoice);

        return new PaymentBatchManager();
    }

    public List<IncomingInvoice> choices0AddInvoiceToPayByBankAccount() {
        return this.getPayableInvoicesNotInAnyBatch();
    }

    public List<BankAccount> choices1AddInvoiceToPayByBankAccount(final IncomingInvoice incomingInvoice) {
        if (incomingInvoice == null) {
            return Lists.newArrayList();
        }
        return bankAccountRepository.findBankAccountsByOwner(incomingInvoice.getBuyer());
    }

    public String validateAddInvoiceToPayByBankAccount(
            final IncomingInvoice incomingInvoice,
            final BankAccount debtorBankAccount) {
        if (incomingInvoice.getBankAccount() == null) {
            return "No creditor bank account on invoice";
        }
        if (incomingInvoice.getBankAccount().getBic() == null) {
            return "Creditor bank account has no BIC";
        }
        if (debtorBankAccount == null) {
            return null;
        }
        if (debtorBankAccount.getBic() == null) {
            return "Bank account has no BIC";
        }
        return null;
    }





    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            command = CommandReification.DISABLED,
            publishing = Publishing.DISABLED
    )
    public PaymentBatchManager removeInvoice(
            final List<IncomingInvoice> incomingInvoices,
            @ParameterLayout(describedAs = "Whether the removed invoices should also be rejected")
            final boolean rejectAlso,
            @ParameterLayout(describedAs = "If rejecting, then explain why so that the error can be fixed")
            @Nullable final String rejectionReason) {
        PaymentBatch_removeInvoice().act(incomingInvoices, rejectAlso, rejectionReason);
        return new PaymentBatchManager();
    }

    public String validateRemoveInvoice(
            final List<IncomingInvoice> incomingInvoices,
            final boolean rejectAlso,
            final String rejectionReason
    ) {
        return PaymentBatch_removeInvoice().validateAct(incomingInvoices, rejectAlso, rejectionReason);
    }

    public List<IncomingInvoice> choices0RemoveInvoice() {
        return PaymentBatch_removeInvoice().choices0Act();
    }

    public String disableRemoveInvoice() {
        if (this.getSelectedBatch() == null) {
            return "No batch selected";
        }
        return PaymentBatch_removeInvoice().disableAct();
    }





    private PaymentBatch.removeInvoice PaymentBatch_removeInvoice() {
        return factoryService.mixin(PaymentBatch.removeInvoice.class, this.getSelectedBatch());
    }

    private PaymentBatch_complete PaymentBatch_complete() {
        return factoryService.mixin(PaymentBatch_complete.class, this.getSelectedBatch());
    }





    private void flushTransaction() {
        transactionService.flushTransaction();
        isisJdoSupport.getJdoPersistenceManager().flush();
    }

    @Inject
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    IncomingInvoiceRepository incomingInvoiceRepository;

    @Inject
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    PaymentBatchRepository paymentBatchRepository;

    @Inject
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    FixedAssetFinancialAccountRepository fixedAssetFinancialAccountRepository;

    @Inject
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    BankAccountRepository bankAccountRepository;


    @Inject
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    TransactionService transactionService;
    @Inject
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    IsisJdoSupport isisJdoSupport;

    @Inject
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    WrapperFactory wrapperFactory;

    @Inject
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    FactoryService factoryService;

    @Inject
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    ServiceRegistry2 serviceRegistry2;

}

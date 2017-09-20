package org.estatio.capex.dom.payment.manager;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.xml.bind.annotation.XmlTransient;

import org.assertj.core.util.Lists;
import org.joda.time.DateTime;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CommandReification;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.wrapper.WrapperFactory;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.applib.value.Clob;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
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
import lombok.NoArgsConstructor;
import lombok.Setter;

@DomainObject(
        objectType = "org.estatio.capex.dom.payment.PaymentBatchManager"
        ,nature = Nature.VIEW_MODEL
)
@NoArgsConstructor
public class PaymentBatchManager {

    public PaymentBatchManager(final int selectedBatchIdx) {
        this.selectedBatchIdx = selectedBatchIdx;
    }
    public String title() {
        return "Payment Batch Manager";
    }

    @Getter @Setter
    private int selectedBatchIdx;

    public PaymentBatch getSelectedBatch(){
        final List<PaymentBatch> newBatches = getNewBatches();
        return newBatches.size() == 0 ? null : newBatches.get(getSelectedBatchIdx());
    }

    public List<PaymentLine> getSelectedBatchPaymentLines(){
        return getSelectedBatch().getLines().stream().collect(Collectors.toList());
    }

    private void selectBatch(final PaymentBatch selected) {
        setSelectedBatchIdx(getNewBatches().indexOf(selected));
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
    @ActionLayout(cssClassFa = "fa-plus")
    public PaymentBatchManager autoCreateBatches(
            final Selection selection,
            @Nullable final List<IncomingInvoice> payableInvoices) {

        final List<IncomingInvoice> invoicesToPay;
        if (selection == Selection.ALL_POSSIBLE) {
            invoicesToPay = getPayableInvoicesNotInAnyBatchWithBankAccountAndBic();
        } else /* if (selection == Selection.HAND_PICK) */ {
            invoicesToPay = payableInvoices;
        }

        for (final IncomingInvoice payableInvoice : invoicesToPay) {
            final BankAccount uniqueBankAccountIfAny = uniqueDebtorAccountToPay(payableInvoice);
            if (uniqueBankAccountIfAny != null && uniqueBankAccountIfAny.getBic() != null) {
                // should be true, because those that don't pass this are filtered out in choicesXxx anyway.
                PaymentBatch paymentBatch = paymentBatchRepository.findOrCreateNewByDebtorBankAccount(uniqueBankAccountIfAny);
                paymentBatch.addLineIfRequired(payableInvoice);
            }
        }
        for (PaymentBatch paymentBatch : getNewBatches()) {
            paymentBatch.removeNegativeTransfers();
        }

        return this;

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
                .filter(payableInvoice -> payableInvoice.getBankAccount() != null
                        && payableInvoice.getBankAccount().getBic() != null).collect(Collectors.toList());
    }

    private BankAccount uniqueDebtorAccountToPay(final IncomingInvoice invoice) {
        Party buyer = invoice.getBuyer();
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

    @Inject
    FixedAssetFinancialAccountRepository fixedAssetFinancialAccountRepository;

    @Inject
    BankAccountRepository bankAccountRepository;

    @MemberOrder(name = "newBatches", sequence = "1")
    public PaymentBatchManager removeAll() {
        for (PaymentBatch paymentBatch : getNewBatches()) {
            paymentBatch.clearLines();
            paymentBatch.remove();
        }
        return new PaymentBatchManager();
    }

/**
 * TODO: inline this mixin
 */
@Mixin(method = "act")
public static class reset {

    private final PaymentBatchManager paymentBatchManager;

    public reset(final PaymentBatchManager paymentBatchManager) {
        this.paymentBatchManager = paymentBatchManager;
    }

    @Action(
            semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE,
            command = CommandReification.DISABLED,
            publishing = Publishing.DISABLED
    )
    @ActionLayout(cssClassFa = "mail-reply", cssClass = "btn-warning")
    public PaymentBatchManager act() {

        final List<PaymentBatch> newBatches = paymentBatchManager.getNewBatches();
        for (PaymentBatch newBatch : newBatches) {
            newBatch.clearLines();
        }
        return new PaymentBatchManager();
    }

    public String disableAct() {
        final List<PaymentBatch> newBatches = paymentBatchManager.getNewBatches();
        for (PaymentBatch newBatch : newBatches) {
            if (!newBatch.getLines().isEmpty()) {
                return null;
            }
        }
        return "No payment batches to reset";
    }
}

///////////////////////

/**
 * TODO: inline this mixin
 */
@Mixin(method = "act")
public static class nextBatch {
    private final PaymentBatchManager paymentBatchManager;

    public nextBatch(final PaymentBatchManager paymentBatchManager) {
        this.paymentBatchManager = paymentBatchManager;
    }

    @Action(
            semantics = SemanticsOf.SAFE,
            command = CommandReification.DISABLED,
            publishing = Publishing.DISABLED
    )
    @ActionLayout(cssClassFa = "fa-step-forward")
    public PaymentBatchManager act() {
        int selectedBatchIdx = paymentBatchManager.getSelectedBatchIdx();
        if (selectedBatchIdx >= 0) {
            int nextBatchIdx = ++selectedBatchIdx;
            if (nextBatchIdx < paymentBatchManager.getNewBatches().size()) {
                paymentBatchManager.selectBatch(paymentBatchManager.getNewBatches().get(nextBatchIdx));
            }
        }
        return paymentBatchManager;
    }

    public String disableAct() {
        if (paymentBatchManager.getNewBatches().isEmpty()) {
            return "No batches";
        }
        int selectedBatchIdx = paymentBatchManager.getSelectedBatchIdx();
        if (selectedBatchIdx >= 0) {
            int nextBatchIdx = ++selectedBatchIdx;
            if (nextBatchIdx >= paymentBatchManager.getNewBatches().size()) {
                return "No more batches";
            }
        }
        return null;
    }
}

/**
 * TODO: inline this mixin
 */
@Mixin(method = "act")
public static class previousBatch {
    private final PaymentBatchManager paymentBatchManager;

    public previousBatch(final PaymentBatchManager paymentBatchManager) {
        this.paymentBatchManager = paymentBatchManager;
    }

    @Action(
            semantics = SemanticsOf.SAFE,
            command = CommandReification.DISABLED,
            publishing = Publishing.DISABLED
    )
    @ActionLayout(cssClassFa = "fa-step-backward")
    public PaymentBatchManager act() {
        int selectedBatchIdx = paymentBatchManager.getSelectedBatchIdx();
        if (selectedBatchIdx >= 0) {
            int previousBatchIdx = --selectedBatchIdx;
            if (previousBatchIdx >= 0) {
                paymentBatchManager.selectBatch(paymentBatchManager.getNewBatches().get(previousBatchIdx));
            }
        }
        return paymentBatchManager;
    }

    public String disableAct() {
        if (paymentBatchManager.getNewBatches().isEmpty()) {
            return "No batches";
        }
        int selectedBatchIdx = paymentBatchManager.getSelectedBatchIdx();
        if (selectedBatchIdx >= 0) {
            int previousBatchIdx = --selectedBatchIdx;
            if (previousBatchIdx < 0) {
                return "No more batches";
            }
        }
        return null;
    }
}

/**
 * TODO: inline this mixin
 */
@Mixin(method = "act")
public static class selectBatch {
    private final PaymentBatchManager paymentBatchManager;

    public selectBatch(final PaymentBatchManager paymentBatchManager) {
        this.paymentBatchManager = paymentBatchManager;
    }

    @Action(
            semantics = SemanticsOf.SAFE,
            command = CommandReification.DISABLED,
            publishing = Publishing.DISABLED
    )
    @ActionLayout(cssClassFa = "fa-hand-o-right")
    public PaymentBatchManager act(final PaymentBatch paymentBatch) {
        paymentBatchManager.selectBatch(paymentBatch);
        return paymentBatchManager;
    }

    public List<PaymentBatch> choices0Act() {
        return paymentBatchManager.getNewBatches();
    }

    public PaymentBatch default0Act() {
        return paymentBatchManager.getSelectedBatch();
    }

    public String disableAct() {
        if (paymentBatchManager.getNewBatches().isEmpty()) {
            return "No new batches";
        }
        return null;
    }
}

/**
 * TODO: inline this mixin
 */
@Mixin(method = "act")
public static class completeBatch {
    private final PaymentBatchManager paymentBatchManager;

    public completeBatch(final PaymentBatchManager paymentBatchManager) {
        this.paymentBatchManager = paymentBatchManager;
    }

    private PaymentBatch_complete mixin() {
        return factoryService.mixin(PaymentBatch_complete.class, paymentBatchManager.getSelectedBatch());
    }

    @Action(
            semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE,
            command = CommandReification.DISABLED,
            publishing = Publishing.DISABLED
    )
    @ActionLayout(cssClassFa = "fa-flag-checkered")
    public PaymentBatchManager act(
            final DateTime requestedExecutionDate,
            @Nullable final String comment) {
        // use the wrapper factory to generate events
        wrapperFactory.wrap(mixin()).act(requestedExecutionDate, comment);

        // rather than return this manager, create a new one (force page reload)
        final PaymentBatchManager paymentBatchManager = new PaymentBatchManager();
        serviceRegistry2.injectServicesInto(paymentBatchManager);
        return new PaymentBatchManager(paymentBatchManager.getSelectedBatchIdx());

    }

    public DateTime default0Act() {
        return mixin().default0Act();
    }

    public String validate0Act(DateTime proposed) {
        return mixin().validate0Act(proposed);
    }

    public boolean hideAct() {
        return paymentBatchManager.getSelectedBatch() == null || mixin().hideAct();
    }

    public String disableAct() {
        if (paymentBatchManager.getSelectedBatch() == null) {
            return "No batch selected";
        }
        return mixin().disableAct();
    }

    @Inject
    WrapperFactory wrapperFactory;

    @Inject
    FactoryService factoryService;

    @Inject
    ServiceRegistry2 serviceRegistry2;
}

///////////////////////

/**
 * TODO: inline this mixin
 */
@Mixin(method = "act")
public static class downloadReviewPdf {
    private final PaymentBatchManager paymentBatchManager;

    public downloadReviewPdf(final PaymentBatchManager paymentBatchManager) {
        this.paymentBatchManager = paymentBatchManager;
    }

    @Action(
            semantics = SemanticsOf.SAFE,
            command = CommandReification.DISABLED,
            publishing = Publishing.DISABLED
    )
    public Blob act(
            final PaymentBatch paymentBatch,
            @Nullable final String documentName) throws IOException {
        final String documentNameToUse = documentName != null ? documentName : paymentBatch.fileNameWithSuffix("pdf");
        return factoryService.mixin(PaymentBatch.downloadReviewPdf.class, paymentBatch).act(documentNameToUse);
    }

    public List<PaymentBatch> choices0Act() {
        return paymentBatchManager.getCompletedBatches();
    }

    public String disableAct() {
        if (paymentBatchManager.getCompletedBatches().isEmpty()) {
            return "No completed batches";
        }
        return null;
    }

    @Inject
    FactoryService factoryService;
}

/**
 * TODO: inline this mixin
 */
@Mixin(method = "act")
public static class downloadPaymentFile {
    private final PaymentBatchManager paymentBatchManager;

    public downloadPaymentFile(final PaymentBatchManager paymentBatchManager) {
        this.paymentBatchManager = paymentBatchManager;
    }

    @Action(
            semantics = SemanticsOf.SAFE,
            command = CommandReification.DISABLED,
            publishing = Publishing.DISABLED
    )
    public Clob act(
            final PaymentBatch paymentBatch,
            @Nullable final String documentName) {
        final String documentNameToUse = documentName != null ? documentName : paymentBatch.fileNameWithSuffix("xml");
        return factoryService.mixin(PaymentBatch.downloadPaymentFile.class, paymentBatch).act(documentNameToUse);
    }

    public List<PaymentBatch> choices0Act() {
        return paymentBatchManager.getCompletedBatches();
    }

    public String disableAct() {
        if (paymentBatchManager.getCompletedBatches().isEmpty()) {
            return "No completed batches";
        }
        return null;
    }

    @Inject
    FactoryService factoryService;
}

///////////////////////

/**
 * TODO: inline this mixin
 */
@Mixin(method = "act")
public static class addInvoiceToPayByBankAccount {
    private final PaymentBatchManager paymentBatchManager;

    public addInvoiceToPayByBankAccount(final PaymentBatchManager paymentBatchManager) {
        this.paymentBatchManager = paymentBatchManager;
    }

    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            command = CommandReification.DISABLED,
            publishing = Publishing.DISABLED
    )
    public PaymentBatchManager act(
            final IncomingInvoice incomingInvoice,
            final BankAccount debtorBankAccount) {

        PaymentBatch paymentBatch = paymentBatchRepository.findOrCreateNewByDebtorBankAccount(debtorBankAccount);
        paymentBatch.addLineIfRequired(incomingInvoice);

        return new PaymentBatchManager();
    }

    public List<IncomingInvoice> choices0Act() {
        return paymentBatchManager.getPayableInvoicesNotInAnyBatch();
    }

    public List<BankAccount> choices1Act(final IncomingInvoice incomingInvoice) {
        if (incomingInvoice == null) {
            return Lists.newArrayList();
        }
        return bankAccountRepository.findBankAccountsByOwner(incomingInvoice.getBuyer());
    }

    public String validateAct(
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

    @Inject
    PaymentBatchRepository paymentBatchRepository;
    @Inject
    BankAccountRepository bankAccountRepository;
}

/**
 * TODO: inline this mixin
 */
@Mixin(method = "act")
public static class removeInvoice {
    private final PaymentBatchManager paymentBatchManager;

    public removeInvoice(final PaymentBatchManager paymentBatchManager) {
        this.paymentBatchManager = paymentBatchManager;
    }

    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            command = CommandReification.DISABLED,
            publishing = Publishing.DISABLED
    )
    public PaymentBatchManager act(
            final List<IncomingInvoice> incomingInvoices,
            @ParameterLayout(describedAs = "Whether the removed invoices should also be rejected") final boolean rejectAlso,
            @ParameterLayout(describedAs = "If rejecting, then explain why so that the error can be fixed")
            @Nullable final String rejectionReason) {
        mixin().act(incomingInvoices, rejectAlso, rejectionReason);
        return new PaymentBatchManager();
    }

    public String validateAct(
            final List<IncomingInvoice> incomingInvoices,
            final boolean rejectAlso,
            final String rejectionReason
    ) {
        return mixin().validateAct(incomingInvoices, rejectAlso, rejectionReason);
    }

    public List<IncomingInvoice> choices0Act() {
        return mixin().choices0Act();
    }

    public String disableAct() {
        if (paymentBatchManager.getSelectedBatch() == null) {
            return "No batch selected";
        }
        return mixin().disableAct();
    }

    private PaymentBatch.removeInvoice mixin() {
        return factoryService.mixin(PaymentBatch.removeInvoice.class, paymentBatchManager.getSelectedBatch());
    }

    @Inject
    FactoryService factoryService;

}

    ///////////////////////

    @Inject
    @XmlTransient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    IncomingInvoiceRepository incomingInvoiceRepository;

    @Inject
    @XmlTransient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    PaymentBatchRepository paymentBatchRepository;

}

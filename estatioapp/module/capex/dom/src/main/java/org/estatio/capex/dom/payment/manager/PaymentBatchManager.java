package org.estatio.capex.dom.payment.manager;

import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.assertj.core.util.Lists;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CommandReification;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.capex.dom.payment.PaymentBatch;
import org.estatio.capex.dom.payment.PaymentBatchRepository;
import org.estatio.capex.dom.payment.PaymentLine;
import org.estatio.dom.asset.Property;
import org.estatio.dom.assetfinancial.FixedAssetFinancialAccount;
import org.estatio.dom.assetfinancial.FixedAssetFinancialAccountRepository;
import org.estatio.dom.financial.FinancialAccount;
import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.financial.bankaccount.BankAccountRepository;
import org.estatio.dom.party.Party;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@DomainObject(
        // WORKAROUND: using fqcn as objectType because Isis' invalidation of cache in prototyping mode causing NPEs in some situations
        objectType = "org.estatio.capex.dom.payment.PaymentBatchManager"
)
@XmlRootElement(name = "paymentBatchManager")
@XmlType(
        propOrder = {
        }
)
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentBatchManager {


    public PaymentBatchManager() {
    }


    public String title() {
        return "Payment Batch Manager";
    }

    ///////////////////////


    @XmlElementWrapper
    @XmlElement(name = "payableInvoicesNotInAnyBatch")
    @Getter @Setter
    private List<IncomingInvoice> payableInvoicesNotInAnyBatch = Lists.newArrayList();

    @XmlElementWrapper
    @XmlElement(name = "currentBatches")
    @Getter @Setter
    private List<PaymentBatch> currentBatches = Lists.newArrayList();

    ///////////////////////

    @XmlElement(name = "selectedBatch")
    @Getter @Setter
    private PaymentBatch selectedBatch;

    private int getSelectedBatchIdx() {
        return currentBatches.indexOf(selectedBatch);
    }

    @XmlElementWrapper
    @XmlElement(name = "selectedBatchPaymentLines")
    @Getter @Setter
    private List<PaymentLine> selectedBatchPaymentLines = Lists.newArrayList();


    private void selectBatch(final PaymentBatch selected) {
        selectedBatch = selected;
        selectedBatchPaymentLines.clear();
        selectedBatchPaymentLines.addAll(selectedBatch.getLines());
    }

    ///////////////////////

    @Programmatic
    public PaymentBatchManager init(){
        return update();
    }

    private PaymentBatchManager update() {

        // current batches
        currentBatches.clear();
        currentBatches.addAll(paymentBatchRepository.findCurrentBatches());
        if(!currentBatches.isEmpty()) {
            selectBatch(currentBatches.get(0));
        }

        // payable invoices
        List<IncomingInvoice> payableInvoices =
                incomingInvoiceRepository.findNotInAnyPaymentBatchByApprovalState(IncomingInvoiceApprovalState.PAYABLE);

        Collections.sort(payableInvoices);
        payableInvoicesNotInAnyBatch.clear();
        payableInvoicesNotInAnyBatch.addAll(payableInvoices);

        return this;
    }


    ///////////////////////

    @Mixin(method="act")
    public static class autoCreateBatches {
        private final PaymentBatchManager paymentBatchManager;
        public autoCreateBatches(final PaymentBatchManager paymentBatchManager) {
            this.paymentBatchManager = paymentBatchManager;
        }

        @Action(
                semantics = SemanticsOf.IDEMPOTENT,
                command = CommandReification.DISABLED,
                publishing = Publishing.DISABLED
        )
        @ActionLayout(cssClassFa = "fa-plus")
        public PaymentBatchManager act() {
            for (final IncomingInvoice payableInvoice : paymentBatchManager.payableInvoicesNotInAnyBatch) {
                final BankAccount uniqueBankAccountIfAny = uniqueAccountToPay(payableInvoice);
                if(uniqueBankAccountIfAny != null) {
                    PaymentBatch paymentBatch = paymentBatchRepository.findOrCreateBatchFor(uniqueBankAccountIfAny);
                    paymentBatch.addLineIfRequired(payableInvoice);
                }
            }

            paymentBatchManager.update();
            return paymentBatchManager;
        }

        private BankAccount uniqueAccountToPay(final IncomingInvoice invoice) {
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

            if (bankAccountsForBuyer.size() != 1) {
                return null;
            }
            return bankAccountsForBuyer.get(0);
        }


        @Inject
        FixedAssetFinancialAccountRepository fixedAssetFinancialAccountRepository;

        @Inject
        BankAccountRepository bankAccountRepository;

        @Inject
        PaymentBatchRepository paymentBatchRepository;
    }

    ///////////////////////

    @Mixin(method="act")
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
            if(selectedBatchIdx >= 0) {
                int nextBatchIdx = ++selectedBatchIdx;
                if(nextBatchIdx < paymentBatchManager.currentBatches.size()) {
                    paymentBatchManager.selectBatch(paymentBatchManager.currentBatches.get(nextBatchIdx));
                }
            }
            return paymentBatchManager;
        }

        public String disableAct() {
            if (paymentBatchManager.getCurrentBatches().isEmpty()) {
                return "No batches";
            }
            int selectedBatchIdx = paymentBatchManager.getSelectedBatchIdx();
            if(selectedBatchIdx >= 0) {
                int nextBatchIdx = ++selectedBatchIdx;
                if (nextBatchIdx >= paymentBatchManager.currentBatches.size()) {
                    return "No more batches";
                }
            }
            return null;
        }
    }

    @Mixin(method="act")
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
            if(selectedBatchIdx >= 0) {
                int previousBatchIdx = --selectedBatchIdx;
                if(previousBatchIdx >= 0) {
                    paymentBatchManager.selectBatch(paymentBatchManager.currentBatches.get(previousBatchIdx));
                }
            }
            return paymentBatchManager;
        }

        public String disableAct() {
            if (paymentBatchManager.getCurrentBatches().isEmpty()) {
                return "No batches";
            }
            int selectedBatchIdx = paymentBatchManager.getSelectedBatchIdx();
            if(selectedBatchIdx >= 0) {
                int previousBatchIdx = --selectedBatchIdx;
                if(previousBatchIdx < 0) {
                    return "No more batches";
                }
            }
            return null;
        }
    }

    @Mixin(method="act")
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
            return paymentBatchManager.getCurrentBatches();
        }

        public String disableAct() {
            if (paymentBatchManager.getCurrentBatches().isEmpty()) {
                return "No batches";
            }
            return null;
        }
    }



    ///////////////////////

    @Mixin(method="act")
    public static class addInvoice {
        private final PaymentBatchManager paymentBatchManager;
        public addInvoice(final PaymentBatchManager paymentBatchManager) {
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

            PaymentBatch paymentBatch = paymentBatchRepository.findOrCreateCurrentBatch(debtorBankAccount);
            paymentBatch.addLineIfRequired(incomingInvoice);

            return paymentBatchManager.update();
        }

        public List<IncomingInvoice> choices0Act() {
            return paymentBatchManager.payableInvoicesNotInAnyBatch;
        }

        public List<BankAccount> choices1Act(final IncomingInvoice incomingInvoice) {
            if(incomingInvoice == null) {
                return Lists.newArrayList();
            }
            return bankAccountRepository.findBankAccountsByOwner(incomingInvoice.getBuyer());
        }

        @Inject
        PaymentBatchRepository paymentBatchRepository;
        @Inject
        BankAccountRepository bankAccountRepository;
    }

    @Mixin(method="act")
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
        public PaymentBatchManager act(final List<IncomingInvoice> incomingInvoices) {
            for (IncomingInvoice incomingInvoice : incomingInvoices) {
                paymentBatchManager.selectedBatch.removeLineFor(incomingInvoice);
            }
            return paymentBatchManager.update();
        }

        public List<IncomingInvoice> choices0Act() {
            List<PaymentLine> selectedBatchPaymentLines = paymentBatchManager.selectedBatchPaymentLines;
            return selectedBatchPaymentLines.stream().map(PaymentLine::getInvoice).collect(Collectors.toList());
        }
        public String disableAct() {
            if(paymentBatchManager.selectedBatch == null) {
                return "No batch selected";
            }
            return null;
        }
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

package org.estatio.capex.dom.payment.manager;

import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.assertj.core.util.Lists;
import org.joda.time.DateTime;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.factory.FactoryService;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.capex.dom.payment.PaymentBatch;
import org.estatio.capex.dom.payment.PaymentBatchRepository;
import org.estatio.capex.dom.payment.PaymentLine;
import org.estatio.capex.dom.payment.approval.PaymentBatchApprovalState;
import org.estatio.capex.dom.payment.approval.triggers.PaymentBatch_complete;
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
        List<IncomingInvoice> payableInvoices = incomingInvoiceRepository
                .findByApprovalState(IncomingInvoiceApprovalState.PAYABLE);

        for (PaymentBatch currentBatch : currentBatches) {
            SortedSet<PaymentLine> lines = currentBatch.getLines();
            for (PaymentLine line : lines) {
                payableInvoices.remove(line.getInvoice());
            }
        }

        Collections.sort(payableInvoices);
        payableInvoicesNotInAnyBatch.clear();
        payableInvoicesNotInAnyBatch.addAll(payableInvoices);

        return this;
    }


    ///////////////////////

    @Mixin(method="act")
    public static class matchInvoices {
        private final PaymentBatchManager paymentBatchManager;
        public matchInvoices(final PaymentBatchManager paymentBatchManager) {
            this.paymentBatchManager = paymentBatchManager;
        }

        @Action(semantics = SemanticsOf.IDEMPOTENT)
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
        @Action(semantics = SemanticsOf.IDEMPOTENT)
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
        @Action(semantics = SemanticsOf.IDEMPOTENT)
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
    public static class completeBatches {
        private final PaymentBatchManager paymentBatchManager;
        public completeBatches(final PaymentBatchManager paymentBatchManager) {
            this.paymentBatchManager = paymentBatchManager;
        }
        @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
        @ActionLayout(cssClassFa = "fa-flag-checkered")
        @MemberOrder(name = "currentBatches", sequence = "4")
        public PaymentBatchManager act(
                DateTime requestedExecutionDate,
                @Nullable String comment) {

            List<PaymentBatch> currentBatches = paymentBatchManager.currentBatches;
            for (PaymentBatch paymentBatch : currentBatches) {
                if(paymentBatch.getApprovalState() == PaymentBatchApprovalState.NEW) {
                    factoryService.mixin(PaymentBatch_complete.class, paymentBatch).act(requestedExecutionDate, comment);
                }
            }

            return paymentBatchManager;
        }
        public String disableAct() {
            return paymentBatchManager.currentBatches.isEmpty() ? "No batches to complete" : null;
        }

        @Inject
        FactoryService factoryService;
    }


    ///////////////////////

    @Mixin(method="act")
    public static class moveInvoice {
        private final PaymentBatchManager paymentBatchManager;
        public moveInvoice(final PaymentBatchManager paymentBatchManager) {
            this.paymentBatchManager = paymentBatchManager;
        }
        @Action(semantics = SemanticsOf.IDEMPOTENT)
        @ActionLayout(cssClassFa = "fa-step-forward")
        public PaymentBatchManager act(final List<IncomingInvoice> incomingInvoices, PaymentBatch moveTo) {
            for (IncomingInvoice incomingInvoice : incomingInvoices) {
                paymentBatchManager.selectedBatch.removeLineFor(incomingInvoice);
                moveTo.addLineIfRequired(incomingInvoice);
            }
            return paymentBatchManager.update();
        }

        public List<IncomingInvoice> choices0Act() {
            List<PaymentLine> selectedBatchPaymentLines = paymentBatchManager.selectedBatchPaymentLines;
            return selectedBatchPaymentLines.stream().map(x -> x.getInvoice()).collect(Collectors.toList());
        }

        public List<PaymentBatch> choices1Act() {
            List<PaymentBatch> paymentBatches = Lists.newArrayList(paymentBatchManager.currentBatches);
            paymentBatches.remove(paymentBatchManager.selectedBatch);
            return paymentBatches;
        }
        public String disableAct() {
            if(paymentBatchManager.selectedBatch == null) {
                return "No batch selected";
            }
            return choices1Act().isEmpty() ? "No other batches" :  null;
        }
    }

    @Mixin(method="act")
    public static class addInvoice {
        private final PaymentBatchManager paymentBatchManager;
        public addInvoice(final PaymentBatchManager paymentBatchManager) {
            this.paymentBatchManager = paymentBatchManager;
        }
        @Action(semantics = SemanticsOf.IDEMPOTENT)
        public PaymentBatchManager act(final List<IncomingInvoice> incomingInvoices, PaymentBatch addTo) {
            for (IncomingInvoice incomingInvoice : incomingInvoices) {
                addTo.addLineIfRequired(incomingInvoice);
            }
            return paymentBatchManager.update();
        }

        public List<IncomingInvoice> choices0Act() {
            List<PaymentLine> selectedBatchPaymentLines = paymentBatchManager.selectedBatchPaymentLines;
            return selectedBatchPaymentLines.stream().map(PaymentLine::getInvoice).collect(Collectors.toList());
        }

        public List<PaymentBatch> choices1Act() {
            return paymentBatchManager.currentBatches;
        }
        public String disableAct() {
            return choices1Act().isEmpty() ? "No batches" :  null;
        }
    }

    @Mixin(method="act")
    public static class removeInvoice {
        private final PaymentBatchManager paymentBatchManager;
        public removeInvoice(final PaymentBatchManager paymentBatchManager) {
            this.paymentBatchManager = paymentBatchManager;
        }
        @Action(semantics = SemanticsOf.IDEMPOTENT)
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

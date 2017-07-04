package org.estatio.capex.dom.payment.manager;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
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
import org.apache.isis.applib.annotation.CommandReification;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Programmatic;
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
    private List<PaymentBatch> newBatches = Lists.newArrayList();

    @XmlElementWrapper
    @XmlElement(name = "completeBatches")
    @Getter @Setter
    private List<PaymentBatch> completedBatches = Lists.newArrayList();

    ///////////////////////

    @XmlElement(name = "selectedBatch")
    @Getter @Setter
    private PaymentBatch selectedBatch;

    private int getSelectedBatchIdx() {
        return newBatches.indexOf(selectedBatch);
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
        newBatches = paymentBatchRepository.findNewBatches();
        if(!newBatches.isEmpty()) {
            selectBatch(newBatches.get(0));
        }

        // complete batches
        completedBatches = paymentBatchRepository.findCompletedBatches();

        // payable invoices
        List<IncomingInvoice> payableInvoices =
                incomingInvoiceRepository.findNotInAnyPaymentBatchByApprovalStateAndPaymentMethod(
                        IncomingInvoiceApprovalState.PAYABLE,
                        PaymentMethod.BANK_TRANSFER);

        Collections.sort(payableInvoices);
        payableInvoicesNotInAnyBatch = payableInvoices;


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
                if(nextBatchIdx < paymentBatchManager.newBatches.size()) {
                    paymentBatchManager.selectBatch(paymentBatchManager.newBatches.get(nextBatchIdx));
                }
            }
            return paymentBatchManager;
        }

        public String disableAct() {
            if (paymentBatchManager.newBatches.isEmpty()) {
                return "No batches";
            }
            int selectedBatchIdx = paymentBatchManager.getSelectedBatchIdx();
            if(selectedBatchIdx >= 0) {
                int nextBatchIdx = ++selectedBatchIdx;
                if (nextBatchIdx >= paymentBatchManager.newBatches.size()) {
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
                    paymentBatchManager.selectBatch(paymentBatchManager.newBatches.get(previousBatchIdx));
                }
            }
            return paymentBatchManager;
        }

        public String disableAct() {
            if (paymentBatchManager.newBatches.isEmpty()) {
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
            return paymentBatchManager.newBatches;
        }

        public String disableAct() {
            if (paymentBatchManager.newBatches.isEmpty()) {
                return "No new batches";
            }
            return null;
        }
    }

    @Mixin(method="act")
    public static class completeBatch {
        private final PaymentBatchManager paymentBatchManager;
        public completeBatch(final PaymentBatchManager paymentBatchManager) {
            this.paymentBatchManager = paymentBatchManager;
        }

        private PaymentBatch_complete mixin() {
            return factoryService.mixin(PaymentBatch_complete.class, paymentBatchManager.selectedBatch);
        }

        @Action(
                semantics = SemanticsOf.SAFE,
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
            return paymentBatchManager.init();

        }

        public DateTime default0Act() {
            return mixin().default0Act();
        }

        public String validate0Act(DateTime proposed) {
            return mixin().validate0Act(proposed);
        }

        public boolean hideAct() {
            return paymentBatchManager.selectedBatch == null || mixin().hideAct();
        }

        public String disableAct() {
            if(paymentBatchManager.selectedBatch == null) {
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


    @Mixin(method="act")
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

    @Mixin(method="act")
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

            PaymentBatch paymentBatch = paymentBatchRepository.findOrCreateBatchFor(debtorBankAccount);
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

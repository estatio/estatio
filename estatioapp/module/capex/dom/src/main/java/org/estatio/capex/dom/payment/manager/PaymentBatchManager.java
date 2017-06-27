package org.estatio.capex.dom.payment.manager;

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
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.capex.dom.payment.PaymentBatch;
import org.estatio.capex.dom.payment.PaymentBatchRepository;
import org.estatio.capex.dom.payment.approval.PaymentBatchApprovalState;
import org.estatio.capex.dom.payment.approval.triggers.PaymentBatch_complete;
import org.estatio.dom.assetfinancial.FixedAssetFinancialAccount;
import org.estatio.dom.assetfinancial.FixedAssetFinancialAccountRepository;
import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.financial.bankaccount.BankAccountRepository;

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
    @XmlElement(name = "transfers")
    @Getter @Setter
    private List<Transfer> transfers;


    public PaymentBatchManager removeTransfers(List<Transfer> transfers) {
        this.transfers.removeAll(transfers);
        return this;
    }

    public List<Transfer> choices0RemoveTransfers() {
        return this.transfers;
    }


    ///////////////////////

    @XmlTransient
    public List<PaymentBatch> getCurrentBatches() {
        return queryResultsCache.execute(
                this::doGetCurrentBatches,
                PaymentBatchManager.class, "getCurrentBatches");
    }

    private List<PaymentBatch> doGetCurrentBatches() {
        return paymentBatchRepository.findCurrentBatches();
    }


    ///////////////////////

    @Programmatic
    public PaymentBatchManager init(){
        transfers = Lists.newArrayList();

        List<IncomingInvoice> payableInvoices = incomingInvoiceRepository
                .findByApprovalStatus(IncomingInvoiceApprovalState.PAYABLE);
        for (IncomingInvoice payableInvoice : payableInvoices) {
            PaymentBatch paymentBatchIfAny = batchThatUniquelyCanPay(payableInvoice);
            Transfer transfer = new Transfer(
                    paymentBatchIfAny, payableInvoice,
                    payableInvoice.getGrossAmount(), remittanceInformationFor(payableInvoice));
            transfers.add(transfer);
        }
        Collections.sort(transfers);

        return this;
    }

    private static String remittanceInformationFor(final IncomingInvoice payableInvoice) {
        // TODO: will need to refine this, no doubt...
        return payableInvoice.getInvoiceNumber();
    }

    /**
     * Those batches whose bank account is the only one associated with the {@link IncomingInvoice#getProperty()}
     * (by way of a {@link FixedAssetFinancialAccount} tuple).
     */
    private List<PaymentBatch> canPay(final IncomingInvoice incomingInvoice) {
        List<PaymentBatch> batchesThatCanPay =
                getCurrentBatches().stream()
                    .filter(batch -> batch.hasUniqueAccountToPay(incomingInvoice))
                    .collect(Collectors.toList());
        return batchesThatCanPay;
    }

    /**
     * The one-and-only batch (if it exists) that has a bank account being the only one associated with
     * the {@link IncomingInvoice#getProperty()} (by way of a {@link FixedAssetFinancialAccount} tuple).
     */
    private PaymentBatch batchThatUniquelyCanPay(IncomingInvoice incomingInvoice) {
        List<PaymentBatch> paymentBatches = canPay(incomingInvoice);
        return paymentBatches.size() == 1
                ? paymentBatches.get(0)
                : null;
    }

    ///////////////////////

    /**
     * For {@link Transfer}s that cannot be uniquely matches to an existing batch (either because they have no
     * corresponding {@link IncomingInvoice#getProperty() property}, or because there is more than one {@link PaymentBatch}
     * that has a {@link BankAccount} associated with its property, this action allows {@link Transfer}(s) to be
     * explicitly associated with the specified {@link PaymentBatch batch} regardless.
     */
    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public PaymentBatchManager assignBatch(PaymentBatch batch, List<Transfer> unmatchedTransfers) {
        for (Transfer unmatchedTransfer : unmatchedTransfers) {
            unmatchedTransfer.setPaymentBatch(batch);
        }
        return this;
    }

    public List<PaymentBatch> choices0AssignBatch() {
        return getCurrentBatches();
    }

    public List<Transfer> choices1AssignBatch() {
        return transfers.stream()
                .filter(transfer -> transfer.getPaymentBatch() == null)
                .collect(Collectors.toList());
    }

    ///////////////////////

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public PaymentBatchManager resetBatch(List<Transfer> transfers) {
        for (Transfer unmatchedTransfer : transfers) {
            unmatchedTransfer.setPaymentBatch(null);
        }
        return this;
    }

    public List<Transfer> choices0ResetBatch() {
        return transfers.stream()
                .filter(transfer -> transfer.getPaymentBatch() != null)
                .collect(Collectors.toList());
    }


    ///////////////////////

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(name = "currentBatches", sequence = "1")
    public PaymentBatchManager createBatch(final BankAccount debtorBankAccount) {
        final DateTime createdOn = clockService.nowAsDateTime();
        paymentBatchRepository.create(createdOn, debtorBankAccount, PaymentBatchApprovalState.NEW);
        updateTransfers();
        return this;
    }

    private void updateTransfers() {
        for (Transfer transfer : transfers) {
            if(transfer.getPaymentBatch() == null) {
                PaymentBatch paymentBatchIfAny = batchThatUniquelyCanPay(transfer.getInvoice());
                if(paymentBatchIfAny != null) {
                    transfer.setPaymentBatch(paymentBatchIfAny);
                }
            }
        }
    }

    public List<BankAccount> autoComplete0CreateBatch(final String search) {
        return bankAccountRepository.autoComplete(search);
    }

    ///////////////////////


    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(name = "currentBatches", sequence = "2")
    public PaymentBatchManager deleteBatch(final PaymentBatch paymentBatch) {
        List<Transfer> transfers = this.transfers;
        for (Transfer transfer : transfers) {
            if(transfer.getPaymentBatch() == paymentBatch) {
                transfer.setPaymentBatch(null);
            }
        }
        paymentBatchRepository.delete(paymentBatch);
        return this;
    }

    public List<PaymentBatch> choices0DeleteBatch() {
        return getCurrentBatches().stream()
                .filter(x -> x.getApprovalState() == PaymentBatchApprovalState.NEW)
                .collect(Collectors.toList());
    }

    public String validateDeleteBatch(PaymentBatch paymentBatch) {
        if(paymentBatch.getApprovalState() != PaymentBatchApprovalState.NEW) {
            return "Only 'NEW' payment batches can be deleted";
        }
        return null;
    }


    ///////////////////////

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(cssClassFa = "fa-play")
    public PaymentBatchManager process(@Nullable String comment) {

        PaymentBatch previousBatch = null;
        for (Transfer transfer : this.transfers) {
            PaymentBatch paymentBatch = transfer.getPaymentBatch();
            paymentBatch.updateFor(transfer.getInvoice(), transfer.getTransferAmount(), transfer.getRemittanceInformation());

            if(paymentBatch != previousBatch) {
                factoryService.mixin(PaymentBatch_complete.class, paymentBatch).act(comment);
                previousBatch = paymentBatch;
            }
        }

        return this;
    }


    public String disableProcess() {
        List<Transfer> transfersWithoutBatch =
                this.transfers.stream()
                        .filter(x -> x.getPaymentBatch() == null)
                        .collect(Collectors.toList());
        return !transfersWithoutBatch.isEmpty()
                ? transfersWithoutBatch.size() + " transfers do not have a batch assigned"
                : null;
    }

    ///////////////////////

    @Inject
    @XmlTransient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    FactoryService factoryService;

    @Inject
    @XmlTransient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    FixedAssetFinancialAccountRepository fixedAssetFinancialAccountRepository;

    @Inject
    @XmlTransient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    IncomingInvoiceRepository incomingInvoiceRepository;

    @Inject
    @XmlTransient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    BankAccountRepository bankAccountRepository;

    @Inject
    @XmlTransient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    PaymentBatchRepository paymentBatchRepository;

    @Inject
    @XmlTransient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    ClockService clockService;

    @Inject
    @XmlTransient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    ServiceRegistry2 serviceRegistry2;

    @Inject
    @XmlTransient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    QueryResultsCache queryResultsCache;
}

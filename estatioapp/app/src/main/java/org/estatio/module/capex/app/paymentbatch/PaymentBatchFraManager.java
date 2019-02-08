package org.estatio.module.capex.app.paymentbatch;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.CommandPersistence;
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

import org.isisaddons.module.excel.dom.ExcelService;

import org.estatio.module.base.dom.VisibilityEvaluator;
import org.estatio.module.capex.app.DebtorBankAccountService;
import org.estatio.module.capex.app.paymentline.PaymentLineForExcelExportV1;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.module.capex.dom.payment.CreditTransfer;
import org.estatio.module.capex.dom.payment.PaymentBatch;
import org.estatio.module.capex.dom.payment.PaymentBatchRepository;
import org.estatio.module.capex.dom.payment.PaymentLine;
import org.estatio.module.capex.dom.payment.approval.triggers.PaymentBatch_complete;
import org.estatio.module.capex.dom.util.InvoicePageRange;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.financial.dom.BankAccountRepository;
import org.estatio.module.invoice.dom.PaymentMethod;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@DomainObject(
        objectType = "org.estatio.capex.dom.payment.PaymentBatchManager"
        ,nature = Nature.VIEW_MODEL
)
public class PaymentBatchFraManager {

    public PaymentBatchFraManager() {
        this(null);
    }

    public PaymentBatchFraManager(final Integer selectedBatchIdx) {
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
        return paymentBatchRepository.findNewBatches().stream().filter(pb->!pb.getAtPath().startsWith("/ITA")).collect(Collectors.toList());
    }


    public List<PaymentBatch> getCompletedBatches() {
        return paymentBatchRepository.findCompletedBatches();
    }


    public List<IncomingInvoice> getPayableInvoicesNotInAnyBatch() {
        return incomingInvoiceRepository.findNotInAnyPaymentBatchByAtPathPrefixesAndApprovalStateAndPaymentMethod(
                IncomingInvoiceRepository.AT_PATHS_FRA_OFFICE,
                IncomingInvoiceApprovalState.PAYABLE,
                PaymentMethod.BANK_TRANSFER);
    }


    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            publishing = Publishing.DISABLED
    )
    public PaymentBatchFraManager autoCreateBatches(
            @Nullable final List<IncomingInvoice> payableInvoices) {

        for (final IncomingInvoice payableInvoice : payableInvoices) {
            final BankAccount uniqueBankAccountIfAny = debtorBankAccountService.uniqueDebtorAccountToPay(payableInvoice);
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

        return new PaymentBatchFraManager(newBatches.isEmpty()? null: 0);
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


    public List<IncomingInvoice> default0AutoCreateBatches() {
        return getPayableInvoicesNotInAnyBatchWithBankAccountAndBic();
    }

    public List<IncomingInvoice> choices0AutoCreateBatches() {
        List<IncomingInvoice> choices = Lists.newArrayList();

        final List<IncomingInvoice> invoices = getPayableInvoicesNotInAnyBatchWithBankAccountAndBic();
        for (final IncomingInvoice payableInvoice : invoices) {
            final BankAccount uniqueBankAccountIfAny = debtorBankAccountService.uniqueDebtorAccountToPay(payableInvoice);
            if (uniqueBankAccountIfAny != null && uniqueBankAccountIfAny.getBic() != null) {
                choices.add(payableInvoice);
            }
        }

        return choices;
    }

    private List<IncomingInvoice> getPayableInvoicesNotInAnyBatchWithBankAccountAndBic() {
        return getPayableInvoicesNotInAnyBatch().stream()
                .filter(pi -> pi.getBankAccount() != null && pi.getBankAccount().getBic() != null)
                .filter(visibilityEvaluator::visibleToMe)
                .collect(Collectors.toList());
    }


    @MemberOrder(name = "newBatches", sequence = "1")
    public PaymentBatchFraManager removeAll() {
        for (PaymentBatch paymentBatch : getNewBatches()) {
            paymentBatch.clearLines();
            paymentBatch.remove();
        }
        return new PaymentBatchFraManager();
    }




    @Action(
            semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE,
            publishing = Publishing.DISABLED
    )
    public PaymentBatchFraManager reset() {

        final List<PaymentBatch> newBatches = this.getNewBatches();
        for (PaymentBatch newBatch : newBatches) {
            newBatch.clearLines();
        }
        return new PaymentBatchFraManager();
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
            publishing = Publishing.DISABLED
    )
    public PaymentBatchFraManager nextBatch() {
        int selectedBatchIdx = this.getSelectedBatchIdx();
        if (selectedBatchIdx >= 0) {
            int nextBatchIdx = ++selectedBatchIdx;
            if (nextBatchIdx < this.getNewBatches().size()) {
                return new PaymentBatchFraManager(nextBatchIdx);
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
            publishing = Publishing.DISABLED
    )
    public PaymentBatchFraManager previousBatch() {
        int selectedBatchIdx = this.getSelectedBatchIdx();
        if (selectedBatchIdx >= 0) {
            int previousBatchIdx = --selectedBatchIdx;
            if (previousBatchIdx >= 0) {
                return new PaymentBatchFraManager(previousBatchIdx);
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
            publishing = Publishing.DISABLED
    )
    public PaymentBatchFraManager selectBatch(final PaymentBatch paymentBatch) {
        final int selectedBatchIdx = getNewBatches().indexOf(paymentBatch);
        return new PaymentBatchFraManager(selectedBatchIdx);
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
            publishing = Publishing.DISABLED
    )
    public PaymentBatchFraManager completeBatch(
            final DateTime requestedExecutionDate,
            @Nullable final String comment) {
        // use the wrapper factory to generate events
        wrapperFactory.wrap(PaymentBatch_complete()).act(requestedExecutionDate, comment);

        // rather than return this manager, create a new one (force page reload)
        final PaymentBatchFraManager paymentBatchFraManager = new PaymentBatchFraManager();
        serviceRegistry2.injectServicesInto(paymentBatchFraManager);
        return new PaymentBatchFraManager(paymentBatchFraManager.getSelectedBatchIdx());

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
            commandPersistence = CommandPersistence.NOT_PERSISTED,
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
    public List<Integer> choices2DownloadReviewPdf() {
        return InvoicePageRange.firstPageChoices();
    }

    public Integer default3DownloadReviewPdf() {
        return 0;
    }
    public List<Integer> choices3DownloadReviewPdf() {
        return InvoicePageRange.lastPageChoices();
    }

    public String disableDownloadReviewPdf() {
        if (this.getCompletedBatches().isEmpty()) {
            return "No completed batches";
        }
        return null;
    }





    @Action(
            semantics = SemanticsOf.SAFE,
            commandPersistence = CommandPersistence.NOT_PERSISTED,
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
            semantics = SemanticsOf.SAFE,
            commandPersistence = CommandPersistence.NOT_PERSISTED,
            publishing = Publishing.DISABLED
    )
    public Blob downloadReviewSummary(
            final PaymentBatch paymentBatch,
            @Nullable final String documentName){
        String name = documentName!=null ? documentName.concat(".xlsx") : paymentBatch.fileNameWithSuffix("xlsx");
        return paymentBatch.downloadReviewSummary(name);
    }

    public List<PaymentBatch> choices0DownloadReviewSummary() {
        return this.getCompletedBatches();
    }

    public String disableDownloadReviewSummary() {
        if (this.getCompletedBatches().isEmpty()) {
            return "No completed batches";
        }
        return null;
    }


    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            publishing = Publishing.DISABLED
    )
    public PaymentBatchFraManager addInvoiceToPayByBankAccount(
            final IncomingInvoice incomingInvoice,
            final BankAccount debtorBankAccount) {

        PaymentBatch paymentBatch = paymentBatchRepository.findOrCreateNewByDebtorBankAccount(debtorBankAccount);
        paymentBatch.addLineIfRequired(incomingInvoice);

        return new PaymentBatchFraManager();
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
            publishing = Publishing.DISABLED
    )
    public PaymentBatchFraManager removeInvoice(
            final List<IncomingInvoice> incomingInvoices,
            @ParameterLayout(describedAs = "Whether the removed invoices should also be rejected")
            final boolean rejectAlso,
            @ParameterLayout(describedAs = "If rejecting, then explain why so that the error can be fixed")
            @Nullable final String rejectionReason) {
        PaymentBatch_removeInvoice().act(incomingInvoices, rejectAlso, rejectionReason);
        return new PaymentBatchFraManager();
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


    @Action(
            semantics = SemanticsOf.SAFE,
            commandPersistence = CommandPersistence.NOT_PERSISTED,
            publishing = Publishing.DISABLED
    )
    public Blob downloadExcelExportForNewBatches(
            @Nullable final String documentName,
            @Nullable final List<PaymentBatch> newPaymentBatches) {
        List<PaymentLineForExcelExportV1> lineVms = new ArrayList<>();
        for (PaymentBatch batch : newPaymentBatches){
            lineVms.addAll(batch.paymentLinesForExcelExport());
        }
        String name = documentName!=null ? documentName.concat(".xlsx") : "export.xlsx";
        return excelService.toExcel(lineVms, PaymentLineForExcelExportV1.class, "export", name);
    }

    public List<PaymentBatch> default1DownloadExcelExportForNewBatches(){
        return getNewBatches();
    }

    public List<PaymentBatch> choices1DownloadExcelExportForNewBatches(){
        return getNewBatches();
    }

    @Action(
            semantics = SemanticsOf.SAFE,
            commandPersistence = CommandPersistence.NOT_PERSISTED,
            publishing = Publishing.DISABLED
    )
    public Blob downloadExcelExportForCompletedBatches(
            @Nullable final String documentName,
            final LocalDate startExecutionDate,
            final LocalDate endExecutionDate
    ) {
        List<PaymentLineForExcelExportV1> lineVms = new ArrayList<>();
        List<PaymentBatch> batchesToExport = getCompletedBatches()
                .stream()
                .filter(
                        x->
                                x.getRequestedExecutionDate().toLocalDate().isAfter(startExecutionDate.minusDays(1))
                        &&
                                x.getRequestedExecutionDate().toLocalDate().isBefore(endExecutionDate.plusDays(1))
                )
                .collect(Collectors.toList());
        for (PaymentBatch batch : batchesToExport){
            lineVms.addAll(batch.paymentLinesForExcelExport());
        }
        String name = documentName!=null ? documentName.concat(".xlsx") : "export.xlsx";
        return excelService.toExcel(lineVms, PaymentLineForExcelExportV1.class, "export", name);
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
    BankAccountRepository bankAccountRepository;

    @Inject
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    DebtorBankAccountService debtorBankAccountService;


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

    @Inject
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    ExcelService excelService;

    @Inject
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    VisibilityEvaluator visibilityEvaluator;
}

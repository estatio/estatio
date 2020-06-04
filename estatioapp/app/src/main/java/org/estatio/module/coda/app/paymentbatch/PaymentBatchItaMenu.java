package org.estatio.module.coda.app.paymentbatch;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Blob;

import org.apache.isis.applib.value.DateTime;
import org.estatio.module.capex.app.invoicedownload.FullyApprovedInvoiceItaDownload;
import org.estatio.module.capex.app.invoicedownload.IncomingInvoiceDownloadManager;
import org.estatio.module.capex.app.invoicedownload.IncomingInvoiceExport;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItemRepository;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.module.coda.app.CodaCmpCodeService;
import org.estatio.module.capex.dom.payment.PaymentBatch;
import org.estatio.module.coda.dom.doc.CodaDocHead;
import org.estatio.module.coda.dom.doc.CodaDocHeadRepository;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.excel.dom.WorksheetContent;
import org.isisaddons.module.excel.dom.WorksheetSpec;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "payments.PaymentBatchItaMenu"
)
@DomainServiceLayout(
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        named = "Payments",
        menuOrder = "70.4"
)
public class PaymentBatchItaMenu {

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public PaymentBatch importPaymentBatch(final Party epcBuyerCompany, final Blob spreadsheet){
        return paymentBatchItaUploadService.importPaymentBatch(epcBuyerCompany, spreadsheet);
    }

    public List<Party> choices0ImportPaymentBatch(){
        List<Party> result = new ArrayList<>();
        codaCmpCodeService.listAll().stream().forEach(ref->{
            Party p = partyRepository.findPartyByReference(ref);
            if (p!=null) result.add(p);
        });
        return result;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public Blob downloadFullyApprovedInvoices(){
        final String fileName = String.format("fullyApprovedInvoicesDownload_%s.xlsx", Date.valueOf(LocalDate.now()));
        final List<IncomingInvoice> invoices = findFullyApprovedAndAvailableItaInvoices();

        final List<FullyApprovedInvoiceItaDownload> exports = invoices.stream()
                .map(invoice -> new FullyApprovedInvoiceItaDownload(
                        invoice,
                        codaDocHeadRepository.findByIncomingInvoice(invoice)
                ))
                .collect(Collectors.toList());

        WorksheetSpec spec = new WorksheetSpec(FullyApprovedInvoiceItaDownload.class, "fullyApprovedInvoicesDownload");
        WorksheetContent worksheetContent = new WorksheetContent(exports, spec);
        return excelService.toExcel(worksheetContent, fileName);
    }

    private List<IncomingInvoice> findFullyApprovedAndAvailableItaInvoices() {
        List<CodaDocHead> availableCodaDocHeads = codaDocHeadRepository.findAvailable();
        List<IncomingInvoice> invoices = availableCodaDocHeads.stream().map(CodaDocHead::getIncomingInvoice).collect(Collectors.toList());
        List<IncomingInvoice> fullyApprovedItaInvoices = invoices.stream().filter(incomingInvoice ->
                incomingInvoice.getAtPath().startsWith("/ITA") && (
                incomingInvoice.getApprovalState().equals(IncomingInvoiceApprovalState.PAYABLE) ||
                incomingInvoice.getApprovalState().equals(IncomingInvoiceApprovalState.PAYABLE_BYPASSING_APPROVAL) ||
                incomingInvoice.getApprovalState().equals(IncomingInvoiceApprovalState.PENDING_CODA_BOOKS_CHECK))
        ).collect(Collectors.toList());

        return fullyApprovedItaInvoices;
    }

    @Inject
    private PaymentBatchItaUploadService paymentBatchItaUploadService;

    @Inject
    private CodaCmpCodeService codaCmpCodeService;

    @Inject
    private PartyRepository partyRepository;

    @Inject
    private ExcelService excelService;

    @Inject
    private CodaDocHeadRepository codaDocHeadRepository;

}

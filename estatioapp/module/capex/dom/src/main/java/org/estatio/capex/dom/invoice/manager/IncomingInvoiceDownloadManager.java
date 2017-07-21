package org.estatio.capex.dom.invoice.manager;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.assertj.core.util.Lists;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.excel.dom.WorksheetContent;
import org.isisaddons.module.excel.dom.WorksheetSpec;
import org.isisaddons.module.pdfbox.dom.service.PdfBoxService;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentAbstract;

import org.estatio.capex.dom.documents.LookupAttachedPdfService;
import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.capex.dom.payment.PdfStamper;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.capex.dom.invoice.manager.IncomingInvoiceDownloadManager")
@NoArgsConstructor
public class IncomingInvoiceDownloadManager {

    public String title() {
        return "Invoice Download";
    }

    public IncomingInvoiceDownloadManager(final LocalDate fromInputDate, final LocalDate toInputDate){
        this.fromInputDate = fromInputDate;
        this.toInputDate = toInputDate;
    }

    @Getter @Setter
    private LocalDate fromInputDate;

    @Getter @Setter
    private LocalDate toInputDate;

    public IncomingInvoiceDownloadManager changeDates(final LocalDate fromInputDate, final LocalDate toInputDate){
        setFromInputDate(fromInputDate);
        setToInputDate(toInputDate);
        return new IncomingInvoiceDownloadManager(fromInputDate, toInputDate);
    }

    public LocalDate default0ChangeDates() {
        return getFromInputDate();
    }

    public LocalDate default1ChangeDates() {
        return getToInputDate();
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(defaultView = "table")
    public List<IncomingInvoice> getInvoices() {
        final Predicate<IncomingInvoice> incomingInvoicePredicate = x -> !x.getApprovalState().equals(IncomingInvoiceApprovalState.NEW);
        return incomingInvoiceRepository.findByDateReceivedBetween(getFromInputDate(), getToInputDate())
                .stream()
                .filter(incomingInvoicePredicate)
                .collect(Collectors.toList());
    }

    @Programmatic
    public List<IncomingInvoiceItem> getInvoiceItems() {
        return getInvoices().stream()
                .flatMap(inv -> inv.getItems().stream())
                .map(invoiceItem -> (IncomingInvoiceItem) invoiceItem)
                .collect(Collectors.toList());
    }

    @Programmatic
    public IncomingInvoiceDownloadManager init() {
        return this;
    }


    private final static Class exportClass = IncomingInvoiceExport.class;

    @Action(semantics = SemanticsOf.SAFE)
    public Blob downloadToExcel(final String fileName) {

        final List<IncomingInvoiceExport> exports = getInvoiceItems().stream()
                .map(x -> new IncomingInvoiceExport(x, documentNumberFor(x)))
                .collect(Collectors.toList());

        WorksheetSpec spec = new WorksheetSpec(exportClass, "invoiceExport");
        WorksheetContent worksheetContent = new WorksheetContent(exports, spec);
        return excelService.toExcel(worksheetContent, fileName);
    }

    private String documentNumberFor(final IncomingInvoiceItem invoiceItem) {
        final IncomingInvoice invoice = (IncomingInvoice) invoiceItem.getInvoice();
        final Optional<Document> documentIfAny = lookupAttachedPdfService.lookupIncomingInvoicePdfFrom(invoice);
        return documentIfAny.map(DocumentAbstract::getName).orElse(null);
    }

    public String default0DownloadToExcel() {
        return defaultFileNameWithSuffix(".xlsx");
    }

    static class DocumentStamper {
        private final Document document;

        DocumentStamper(final Document document) {
            this.document = document;
        }

        byte[] stampUsing(final PdfStamper pdfStamper) {
            try {
                final List<String> leftLineTexts = Lists.newArrayList(document.getName());
                final List<String> rightLineTexts = Collections.emptyList();
                final String hyperlink = null;
                
                return pdfStamper.withStampOf(document.getBlobBytes(), leftLineTexts, rightLineTexts, hyperlink);
            } catch (IOException e) {
                return null;
            }
        }
    }

    @Action(semantics = SemanticsOf.SAFE)
    public Blob downloadToPdf(final String fileName) throws IOException {

        // TODO: there's a risk this might blow up memory.  To fix this, would need a new streaming API for pdfBoxService

        final List<byte[]> pdfByteList = getInvoices().stream()
                .map(incomingInvoice -> lookupAttachedPdfService.lookupIncomingInvoicePdfFrom(incomingInvoice))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .sorted(Comparator.comparing(DocumentAbstract::getName))
                .map(DocumentStamper::new)
                .map(stamper -> stamper.stampUsing(pdfStamper))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        final byte[][] pdfByteArrays = pdfByteList.toArray(new byte[0][0]);
        final byte[] singlePdf = pdfBoxService.merge(pdfByteArrays);

        return new Blob(fileName, "application/pdf", singlePdf);
    }

    public String default0DownloadToPdf() {
        return defaultFileNameWithSuffix(".pdf");
    }


    private String defaultFileNameWithSuffix(final String suffix) {
        final String fileName = String.format("%s_%s-%s",
                exportClass.getSimpleName(),
                getFromInputDate().toString("yyyyMMdd"),
                getToInputDate().toString("yyyyMMdd")
        );

        return fileName.concat(suffix);
    }



    @javax.inject.Inject
    private IncomingInvoiceRepository incomingInvoiceRepository;

    @javax.inject.Inject
    private PdfStamper pdfStamper;

    @javax.inject.Inject
    private ExcelService excelService;

    @javax.inject.Inject
    private PdfBoxService pdfBoxService;

    @javax.inject.Inject
    private LookupAttachedPdfService lookupAttachedPdfService;

}

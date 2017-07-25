package org.estatio.capex.dom.invoice.manager;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;

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

import org.estatio.capex.dom.coda.CodaElement;
import org.estatio.capex.dom.coda.CodaMapping;
import org.estatio.capex.dom.coda.CodaMappingRepository;
import org.estatio.capex.dom.documents.LookupAttachedPdfService;
import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.capex.dom.payment.PdfStamper;
import org.estatio.capex.dom.state.StateTransitionRepositoryGeneric;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;

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

    public IncomingInvoiceDownloadManager(final LocalDate fromInputDate, final LocalDate toInputDate, final org.estatio.dom.asset.Property property){
        this.fromInputDate = fromInputDate;
        this.toInputDate = toInputDate;
        this.propertyReference = property == null ? null : property.getReference();
    }

    @Getter @Setter
    private LocalDate fromInputDate;

    @Getter @Setter
    private LocalDate toInputDate;

    @Getter @Setter
    private String propertyReference;

    public Property getProperty(){
        return getPropertyReference() == null ? null : propertyRepository.findPropertyByReference(getPropertyReference());
    }

    public IncomingInvoiceDownloadManager changeParameters(
            final LocalDate fromInputDate,
            final LocalDate toInputDate,
            @Nullable
            final org.estatio.dom.asset.Property property){
        setFromInputDate(fromInputDate);
        setToInputDate(toInputDate);
        setPropertyReference(property == null ? null : property.getReference());
        return new IncomingInvoiceDownloadManager(fromInputDate, toInputDate, property);
    }

    public LocalDate default0ChangeParameters() {
        return getFromInputDate();
    }

    public LocalDate default1ChangeParameters() {
        return getToInputDate();
    }

    public Property default2ChangeParameters() {
        return getProperty();
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(defaultView = "table")
    public List<IncomingInvoice> getInvoices() {
        final Predicate<IncomingInvoice> excludeNew = x -> !x.getApprovalState().equals(IncomingInvoiceApprovalState.NEW);
        final Predicate<IncomingInvoice> excludeDiscarded = x -> !x.getApprovalState().equals(IncomingInvoiceApprovalState.DISCARDED);
        return incomingInvoiceRepository.findByPropertyAndDateReceivedBetween(getProperty(), getFromInputDate(), getToInputDate())
                .stream()
                .filter(excludeNew)
                .filter(excludeDiscarded)
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
                .map(x -> new IncomingInvoiceExport(x, documentNumberFor(x), codaElementFor(x), commentsFor(x)))
                .sorted(Comparator.comparing(x -> x.getDocumentNumber()))
                .collect(Collectors.toList());

        WorksheetSpec spec = new WorksheetSpec(exportClass, "invoiceExport");
        WorksheetContent worksheetContent = new WorksheetContent(exports, spec);
        return excelService.toExcel(worksheetContent, fileName);
    }

    private CodaElement codaElementFor(final IncomingInvoiceItem x) {
        final List<CodaMapping> codaMappings = codaMappingRepository.findMatching(x.getIncomingInvoiceType(), x.getCharge());
        return codaMappings.size() == 0 ? null : codaMappings.get(0).getCodaElement();
    }

    private String documentNumberFor(final IncomingInvoiceItem invoiceItem) {
        final IncomingInvoice invoice = (IncomingInvoice) invoiceItem.getInvoice();
        final Optional<Document> documentIfAny = lookupAttachedPdfService.lookupIncomingInvoicePdfFrom(invoice);
        return documentIfAny.map(DocumentAbstract::getName).orElse(null);
    }

    private String commentsFor(final IncomingInvoiceItem invoiceItem){
        StringBuffer result = new StringBuffer();
        final IncomingInvoice invoice = (IncomingInvoice) invoiceItem.getInvoice();
        List<IncomingInvoiceApprovalStateTransition> transitions = stateTransitionRepositoryGeneric.findByDomainObject(invoice, IncomingInvoiceApprovalStateTransition.class);
        for (IncomingInvoiceApprovalStateTransition transition : transitions){
            if (transition.getTask()!=null && transition.getTask().getComment() !=null){
                result.append(transition.getTask().getComment());
                result.append(" | ");
            }
        }
        return result.toString();
    }

    @Inject
    protected StateTransitionRepositoryGeneric stateTransitionRepositoryGeneric;

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
        final String fileName = String.format("%s_%s_%s-%s",
                exportClass.getSimpleName(),
                getPropertyReference() == null ? "" : getPropertyReference(),
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

    @Inject
    private CodaMappingRepository codaMappingRepository;

    @Inject
    private PropertyRepository propertyRepository;


}

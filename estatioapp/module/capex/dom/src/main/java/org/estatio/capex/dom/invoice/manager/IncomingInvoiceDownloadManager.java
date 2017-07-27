package org.estatio.capex.dom.invoice.manager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.assertj.core.util.Lists;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.excel.dom.WorksheetContent;
import org.isisaddons.module.excel.dom.WorksheetSpec;

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
import org.estatio.capex.dom.pdfmanipulator.PdfManipulator;
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

    //region > downloadToPdf (action)
    @Mixin(method="act")
    public static class downloadToPdf {
        private final IncomingInvoiceDownloadManager manager;
        public downloadToPdf(final IncomingInvoiceDownloadManager manager) {
            this.manager = manager;
        }
        @Action(semantics = SemanticsOf.SAFE)
        @ActionLayout(contributed=Contributed.AS_ACTION)
        public Blob act(
                final String fileName,
                @ParameterLayout(named = "How many first pages of each invoice's PDF?")
                final Integer numFirstPages,
                @ParameterLayout(named = "How many final pages of each invoice's PDF?")
                final Integer numLastPages) throws IOException {

            final List<DocumentPreparer> preparers = manager.getInvoices().stream()
                    .map(incomingInvoice -> lookupAttachedPdfService.lookupIncomingInvoicePdfFrom(incomingInvoice))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .sorted(Comparator.comparing(DocumentAbstract::getName))
                    .map(document -> new DocumentPreparer(document, numFirstPages, numLastPages))
                    .collect(Collectors.toList());

            final List<File> fileList = preparers.stream()
                    .map(preparer -> preparer.stampUsing(pdfManipulator).getTempFile())
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            final byte[] singlePdf = pdfBoxService2.merge(fileList);

            preparers.forEach(DocumentPreparer::cleanup);

            return new Blob(fileName, "application/pdf", singlePdf);
        }
        public String default0Act() {
            return manager.defaultFileNameWithSuffix(".pdf");
        }
        public Integer default1Act() {
            return 3;
        }
        public List<Integer> choices1Act() {
            return Lists.newArrayList(1,2,3,4,5,10,20,50);
        }
        public Integer default2Act() {
            return 1;
        }
        public List<Integer> choices2Act() {
            return Lists.newArrayList(0,1,2,3,4,5,10,20,50);
        }

        @javax.inject.Inject
        private PdfManipulator pdfManipulator;

        @javax.inject.Inject
        private PdfBoxService2 pdfBoxService2;


        @javax.inject.Inject
        private LookupAttachedPdfService lookupAttachedPdfService;

    }
    //endregion
    
    String defaultFileNameWithSuffix(final String suffix) {
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
    private ExcelService excelService;

    @javax.inject.Inject
    private LookupAttachedPdfService lookupAttachedPdfService;

    @Inject
    private CodaMappingRepository codaMappingRepository;

    @Inject
    private PropertyRepository propertyRepository;


    // TODO: to add into PdfBox service in the future...
    @DomainService(nature = NatureOfService.DOMAIN)
    public static class PdfBoxService2 {

        @Programmatic
        public byte[] merge(final List<File> fileList) throws IOException {

            final ByteArrayOutputStream baos = new ByteArrayOutputStream();

            final PDFMergerUtility ut = new PDFMergerUtility();
            for (File file : fileList) {
                ut.addSource(file);
            }

            ut.setDestinationStream(baos);
            ut.mergeDocuments(MemoryUsageSetting.setupTempFileOnly());

            return baos.toByteArray();
        }

        @Programmatic
        public byte[] merge(final File... files) throws IOException {
            return merge(Arrays.asList(files));
        }

    }

    public static class DownloadException extends RuntimeException {
        public DownloadException() {
        }

        public DownloadException(final String message, final Throwable cause) {
            super(message, cause);
        }

        public DownloadException(final Throwable cause) {
            super(cause);
        }

    }


}

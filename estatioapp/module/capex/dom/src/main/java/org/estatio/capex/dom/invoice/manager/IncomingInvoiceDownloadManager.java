package org.estatio.capex.dom.invoice.manager;

import java.io.File;
import java.io.IOException;
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
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.PropertyLayout;
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
import org.estatio.capex.dom.invoice.IncomingInvoiceType;
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


    public static class DownloadException extends RuntimeException {
        public DownloadException(final String message, final Throwable cause) {
            super(message, cause);
        }
    }



    public String title() {
        return "Invoice Download";
    }

    public IncomingInvoiceDownloadManager(final LocalDate fromInputDate, final LocalDate toInputDate, final org.estatio.dom.asset.Property property, final IncomingInvoiceType incomingInvoiceType){
        this.fromInputDate = fromInputDate;
        this.toInputDate = toInputDate;
        this.propertyReference = property == null ? null : property.getReference();
        this.incomingInvoiceTypeName = incomingInvoiceType == null ? null : incomingInvoiceType.name();
    }

    @Getter @Setter
    private LocalDate fromInputDate;

    @Getter @Setter
    private LocalDate toInputDate;

    @Getter @Setter
    private String propertyReference;

    @Getter @Setter
    @PropertyLayout(named = "Incoming Invoice Type")
    private String incomingInvoiceTypeName;

    public Property getProperty(){
        return getPropertyReference() == null ? null : propertyRepository.findPropertyByReference(getPropertyReference());
    }

    IncomingInvoiceType getIncomingInvoiceType() {
        return getIncomingInvoiceTypeName() == null ? null : IncomingInvoiceType.valueOf(getIncomingInvoiceTypeName());
    }


    @CollectionLayout(defaultView = "table")
    public List<IncomingInvoice> getInvoices() {
        final Predicate<IncomingInvoice> excludeNew = x -> !x.getApprovalState().equals(IncomingInvoiceApprovalState.NEW);
        final Predicate<IncomingInvoice> excludeDiscarded = x -> !x.getApprovalState().equals(IncomingInvoiceApprovalState.DISCARDED);
        final Predicate<IncomingInvoice> filterType = getIncomingInvoiceType()!= null ? x -> x.getType().equals(getIncomingInvoiceType()) : x->true;
        return incomingInvoiceRepository.findByPropertyAndDateReceivedBetween(getProperty(), getFromInputDate(), getToInputDate())
                .stream()
                .filter(excludeNew)
                .filter(excludeDiscarded)
                .filter(filterType)
                .collect(Collectors.toList());
    }

    @Programmatic
    private List<IncomingInvoiceItem> getInvoiceItems() {
        return getInvoices().stream()
                .flatMap(inv -> inv.getItems().stream())
                .map(invoiceItem -> (IncomingInvoiceItem) invoiceItem)
                .collect(Collectors.toList());
    }

    @Programmatic
    public IncomingInvoiceDownloadManager init() {
        return this;
    }



    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(contributed= Contributed.AS_ACTION)
    public IncomingInvoiceDownloadManager changeParameters(
            final LocalDate fromInputDate,
            final LocalDate toInputDate,
            @Nullable
            final org.estatio.dom.asset.Property property,
            @Nullable
            final IncomingInvoiceType incomingInvoiceType){
        setFromInputDate(fromInputDate);
        setToInputDate(toInputDate);
        setPropertyReference(property == null ? null : property.getReference());
        setIncomingInvoiceTypeName(incomingInvoiceType == null ? null : incomingInvoiceType.name());
        return new IncomingInvoiceDownloadManager(fromInputDate, toInputDate, property, incomingInvoiceType);
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

    public IncomingInvoiceType default3ChangeParameters() {
        return getIncomingInvoiceType();
    }




    @Action(semantics = SemanticsOf.SAFE)
    public Blob downloadToExcel(final String fileName) {

        final List<IncomingInvoiceExport> exports = getInvoiceItems().stream()
                .map(item -> new IncomingInvoiceExport(
                        item,
                        documentNumberFor(item),
                        codaElementFor(item),
                        commentsFor(item)))
                .sorted(Comparator.comparing(x -> x.getDocumentNumber()))
                .collect(Collectors.toList());

        WorksheetSpec spec = new WorksheetSpec(IncomingInvoiceDownloadManager.exportClass, "invoiceExport");
        WorksheetContent worksheetContent = new WorksheetContent(exports, spec);
        return excelService.toExcel(worksheetContent, fileName);
    }

    public String default0DownloadToExcel() {
        return defaultFileNameWithSuffix(".xlsx");
    }

    public String disableDownloadToExcel() {
        return getInvoices().isEmpty() ? "No invoices to download": null;
    }




    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(named = "Download single PDF")
    public Blob downloadToPdfSingle(
            final String fileName,
            @ParameterLayout(named = "How many first pages of each invoice's PDF?")
            final Integer numFirstPages,
            @ParameterLayout(named = "How many final pages of each invoice's PDF?")
            final Integer numLastPages) throws IOException {

        final List<DocumentPreparer> preparers = documentPreparersForInvoices(numFirstPages, numLastPages);
        final List<File> fileList = filesFrom(preparers);

        final byte[] singlePdf = pdfBoxService2.merge(fileList);

        preparers.forEach(DocumentPreparer::cleanup);

        return new Blob(fileName, "application/pdf", singlePdf);
    }

    public String disableDownloadToPdfSingle() {
        return getInvoices().isEmpty() ? "No invoices to download": null;
    }

    public String default0DownloadToPdfSingle() {
        return defaultFileNameWithSuffix(".pdf");
    }
    public Integer default1DownloadToPdfSingle() {
        return 3;
    }
    public List<Integer> choices1DownloadToPdfSingle() {
        return Lists.newArrayList(1,2,3,4,5,10,20,50);
    }
    public Integer default2DownloadToPdfSingle() {
        return 1;
    }
    public List<Integer> choices2DownloadToPdfSingle() {
        return Lists.newArrayList(0,1,2,3,4,5,10,20,50);
    }




    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(named = "Download all PDFs (zipped)")
    public Blob downloadToPdfZipped(final String fileName) throws IOException {

        final List<DocumentPreparer> preparers = documentPreparersForInvoices();
        final List<ZipService.FileAndName> fileList = fileAndNamesFrom(preparers);

        final byte[] zipBytes = zipService.zip(fileList);

        preparers.forEach(DocumentPreparer::cleanup);

        return new Blob(fileName, "application/zip", zipBytes);
    }

    public String disableDownloadToPdfZipped() {
        return getInvoices().isEmpty() ? "No invoices to download": null;
    }

    public String default0DownloadToPdfZipped() {
        return defaultFileNameWithSuffix(".zip");
    }




    private List<DocumentPreparer> documentPreparersForInvoices() {
        return documentPreparersForInvoices(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    private List<DocumentPreparer> documentPreparersForInvoices(
            Integer numFirstPages,
            Integer numLastPages) {

        return getInvoices().stream()
                .map(invoice -> {
                    final Document document =
                            lookupAttachedPdfService.lookupIncomingInvoicePdfFrom(invoice).orElse(null);

                    IncomingInvoiceApprovalStateTransition approvalTransitionIfAny =
                            stateTransitionRepository.findByDomainObjectAndToState(invoice,
                                    IncomingInvoiceApprovalState.APPROVED_BY_COUNTRY_DIRECTOR);

                    if(approvalTransitionIfAny == null) {
                        approvalTransitionIfAny =
                                stateTransitionRepository.findByDomainObjectAndToState(invoice,
                                        IncomingInvoiceApprovalState.APPROVED_BY_CORPORATE_MANAGER);
                    }
                    return new DocumentPreparer(invoice, approvalTransitionIfAny,
                            document, numFirstPages, numLastPages);
                })
                .filter(dp -> dp.getDocumentName() != null)
                .sorted(Comparator.comparing(DocumentPreparer::getDocumentName))
                .collect(Collectors.toList());
    }


    private List<ZipService.FileAndName> fileAndNamesFrom(final List<DocumentPreparer> preparers) {
        return preparers.stream()
                .map(preparer -> new ZipService.FileAndName(preparer.getDocumentName(), preparer.stampUsing(pdfManipulator).getTempFile()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    private List<File> filesFrom(final List<DocumentPreparer> preparers) {
        return preparers.stream()
                .map(preparer -> preparer.stampUsing(pdfManipulator).getTempFile())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }




    CodaElement codaElementFor(final IncomingInvoiceItem x) {
        final List<CodaMapping> codaMappings = codaMappingRepository.findMatching(x.getIncomingInvoiceType(), x.getCharge());
        return codaMappings.size() == 0 ? null : codaMappings.get(0).getCodaElement();
    }

    String documentNumberFor(final IncomingInvoiceItem invoiceItem) {
        final IncomingInvoice invoice = (IncomingInvoice) invoiceItem.getInvoice();
        final Optional<Document> documentIfAny = lookupAttachedPdfService.lookupIncomingInvoicePdfFrom(invoice);
        return documentIfAny.map(DocumentAbstract::getName).orElse(null);
    }

    String commentsFor(final IncomingInvoiceItem invoiceItem){
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


    final static Class exportClass = IncomingInvoiceExport.class;

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
    IncomingInvoiceRepository incomingInvoiceRepository;

    @Inject
    CodaMappingRepository codaMappingRepository;

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    StateTransitionRepositoryGeneric stateTransitionRepositoryGeneric;

    @Inject
    IncomingInvoiceApprovalStateTransition.Repository stateTransitionRepository;


    @javax.inject.Inject
    ExcelService excelService;


    @javax.inject.Inject
    PdfManipulator pdfManipulator;

    @javax.inject.Inject
    LookupAttachedPdfService lookupAttachedPdfService;


    @javax.inject.Inject
    PdfBoxService2 pdfBoxService2;

    @javax.inject.Inject
    ZipService zipService;


}

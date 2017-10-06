package org.estatio.capex.dom.invoice.manager;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.assertj.core.util.Lists;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.schema.utils.jaxbadapters.JodaLocalDateStringAdapter;

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
import org.estatio.capex.dom.invoice.IncomingInvoiceItemRepository;
import org.estatio.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.capex.dom.pdfmanipulator.PdfManipulator;
import org.estatio.capex.dom.state.NatureOfTransition;
import org.estatio.capex.dom.state.StateTransitionRepositoryGeneric;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.invoice.InvoiceItem;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@DomainObject(
        objectType = "org.estatio.capex.dom.invoice.manager.IncomingInvoiceDownloadManager")
@XmlRootElement(name = "incomingInvoiceDownloadManager")
@XmlType(
        propOrder = {

        }
)
@XmlAccessorType(XmlAccessType.FIELD)
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

    public IncomingInvoiceDownloadManager(
            final LocalDate reportedDate,
            final boolean allProperties,
            final Property property,
            final IncomingInvoiceType incomingInvoiceType) {
        this.reportedDate = reportedDate;
        this.allProperties = allProperties;
        this.property = property;
        this.incomingInvoiceType = incomingInvoiceType;
    }

    @XmlTransient
    public boolean isAllUnreported() {
        return getReportedDate() == null;
    }

    /**
     * Required if 'allUnreported' not set
     */
    @XmlElement(required = false)
    @Nullable
    @Getter @Setter
    @XmlJavaTypeAdapter(JodaLocalDateStringAdapter.ForJaxb.class)
    private LocalDate reportedDate;

    @XmlElement(required = true)
    @Getter @Setter
    private boolean allProperties;

    /**
     * If not set when 'allProperties' not set, will pick up invoices which have no associated properties.
     */
    @XmlElement(required = false)
    @Nullable
    @Getter @Setter
    private Property property;

    @XmlTransient
    public boolean isAllTypes() {
        return getIncomingInvoiceType() == null;
    }

    /**
     * Required if 'allTypes' is not set.
     */
    @XmlElement(required = false)
    @Nullable
    @Getter @Setter
    private IncomingInvoiceType incomingInvoiceType;


    @XmlTransient
    public int getNumberOfInvoices() {
        return getInvoices().size();
    }
    @XmlTransient
    public int getNumberOfInvoiceItems() {
        return getInvoiceItems().size();
    }



    @CollectionLayout(defaultView = "table")
    public List<IncomingInvoice> getInvoices() {
        return getInvoiceItems().stream().map(InvoiceItem::getInvoice)
                .filter(IncomingInvoice.class::isInstance)
                .map(IncomingInvoice.class::cast)
                .distinct()
                .collect(Collectors.toList());
    }

    List<IncomingInvoiceItem> getInvoiceItems() {
        if(isAllProperties()) {
            if(getIncomingInvoiceType() == null) {
                return incomingInvoiceItemRepository.findCompletedOrLaterByReportedDate(getReportedDate());
            } else {
                return incomingInvoiceItemRepository.findCompletedOrLaterByIncomingInvoiceTypeAndReportedDate(getIncomingInvoiceType(), getReportedDate());
            }
        } else {
            if(getIncomingInvoiceType() == null) {
                return incomingInvoiceItemRepository.findCompletedOrLaterByPropertyAndReportedDate(getProperty(), getReportedDate());
            } else {
                return incomingInvoiceItemRepository.findCompletedOrLaterByPropertyAndIncomingInvoiceTypeAndReportedDate(getProperty(), getIncomingInvoiceType(), getReportedDate());
            }
        }
    }


    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    public IncomingInvoiceDownloadManager report(LocalDate reportedDate) {
        final List<IncomingInvoiceItem> invoiceItems = getInvoiceItems();
        for (IncomingInvoiceItem invoiceItem : invoiceItems) {
            if(invoiceItem.getReportedDate() == null) {
                invoiceItem.setReportedDate(reportedDate);
            }
        }
        return new IncomingInvoiceDownloadManager(
                reportedDate, allProperties, property, incomingInvoiceType);
    }
    public LocalDate default0Report() {
        return clockService.now();
    }
    public String disableReport() {
        if(getReportedDate() != null){
            return "Select 'all unreported' first";
        }
        return null;
    }



    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(contributed= Contributed.AS_ACTION)
    public IncomingInvoiceDownloadManager changeReportedDate(
            @Nullable
            final LocalDate reportedDate){
        return new IncomingInvoiceDownloadManager(
                reportedDate, allProperties, property, incomingInvoiceType);
    }

    public LocalDate default0ChangeReportedDate() {
        return getReportedDate();
    }

    public List<LocalDate> choices0ChangeReportedDate() {
        return incomingInvoiceItemRepository.findDistinctReportDates();
    }




    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(contributed= Contributed.AS_ACTION)
    public IncomingInvoiceDownloadManager changeProperty(
            final boolean allProperties,
            @Nullable
            final org.estatio.dom.asset.Property property){
        return new IncomingInvoiceDownloadManager(
                reportedDate, allProperties, allProperties ? null : property, incomingInvoiceType);
    }

    public boolean default0ChangeProperty() {
        return isAllProperties();
    }

    public Property default1ChangeProperty() {
        return getProperty();
    }

    public List<Property> choices1ChangeProperty() {
        return propertyRepository.allProperties();
    }



    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(contributed= Contributed.AS_ACTION)
    public IncomingInvoiceDownloadManager changeType(
            @Nullable
            final IncomingInvoiceType incomingInvoiceType){
        return new IncomingInvoiceDownloadManager(
                reportedDate, allProperties, property, incomingInvoiceType);
    }

    public IncomingInvoiceType default0ChangeType() {
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
        return getInvoiceItems().isEmpty() ? "No invoice items to download": null;
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
        return getInvoiceItems().isEmpty() ? "No invoice items to download": null;
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
        return getInvoiceItems().isEmpty() ? "No invoice items to download": null;
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

                    IncomingInvoiceApprovalStateTransition approvalTransitionIfAny = null;

                    final IncomingInvoiceApprovalState approvalState = invoice.getApprovalState();
                    switch (approvalState) {
                    case NEW:
                    case COMPLETED:
                    case DISCARDED:
                    case APPROVED:
                        // these states all imply that the invoice hasn't yet been approved
                        // (or it might have once been approved, but since been rejected and not yet re-completed).
                        break;
                    case APPROVED_BY_COUNTRY_DIRECTOR:
                    case APPROVED_BY_CORPORATE_MANAGER:
                    case PENDING_BANK_ACCOUNT_CHECK:
                    case PAYABLE:
                    case PAID:
                        // all of these states imply that the invoice has been approved.
                        approvalTransitionIfAny = stateTransitionRepository.findByDomainObjectAndToState(invoice,
                                IncomingInvoiceApprovalState.APPROVED_BY_COUNTRY_DIRECTOR,
                                NatureOfTransition.EXPLICIT);
                        if(approvalTransitionIfAny == null) {
                            approvalTransitionIfAny = stateTransitionRepository.findByDomainObjectAndToState(invoice,
                                IncomingInvoiceApprovalState.APPROVED_BY_CORPORATE_MANAGER,
                                NatureOfTransition.EXPLICIT);
                        }
                        break;
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

        final String reportedDate =
                isAllUnreported()
                        ? "unreported"
                        : getReportedDate().toString("yyyyMMdd");

        final String propertyRef =
                isAllProperties()
                        ? "allProperties"
                        : (getProperty() != null
                                ? getProperty().getReference()
                                : "withNoProperty");

        final String type =
                getIncomingInvoiceType() != null
                        ? getIncomingInvoiceType().name()
                        : "allIncomingInvoiceTypes";

        final String fileName = String.format("%s_%s_%s_%s",
                exportClass.getSimpleName(),
                reportedDate,
                propertyRef,
                type
        );

        return fileName.concat(suffix);
    }



    @Inject
    @XmlTransient
    IncomingInvoiceRepository incomingInvoiceRepository;

    @Inject
    @XmlTransient
    IncomingInvoiceItemRepository incomingInvoiceItemRepository;

    @Inject
    @XmlTransient
    CodaMappingRepository codaMappingRepository;

    @Inject
    @XmlTransient
    PropertyRepository propertyRepository;

    @Inject
    @XmlTransient
    StateTransitionRepositoryGeneric stateTransitionRepositoryGeneric;

    @Inject
    @XmlTransient
    IncomingInvoiceApprovalStateTransition.Repository stateTransitionRepository;

    @javax.inject.Inject
    @XmlTransient
    ExcelService excelService;


    @javax.inject.Inject
    @XmlTransient
    PdfManipulator pdfManipulator;

    @javax.inject.Inject
    @XmlTransient
    LookupAttachedPdfService lookupAttachedPdfService;


    @javax.inject.Inject
    @XmlTransient
    PdfBoxService2 pdfBoxService2;

    @javax.inject.Inject
    @XmlTransient
    ZipService zipService;

    @javax.inject.Inject
    @XmlTransient
    ClockService clockService;


}

package org.estatio.module.capex.app.invoicedownload;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.tablecol.TableColumnOrderService;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.schema.utils.jaxbadapters.JodaLocalDateStringAdapter;

import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.excel.dom.WorksheetContent;
import org.isisaddons.module.excel.dom.WorksheetSpec;
import org.isisaddons.module.pdfbox.dom.service.PdfBoxService;

import org.incode.module.base.dom.MimeTypes;
import org.incode.module.country.dom.impl.Country;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentAbstract;
import org.incode.module.zip.impl.ZipService;

import org.estatio.module.asset.dom.FixedAsset;
import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.base.platform.applib.ReasonBuffer2;
import org.estatio.module.capex.dom.coda.CodaElement;
import org.estatio.module.capex.dom.coda.CodaMapping;
import org.estatio.module.capex.dom.coda.CodaMappingRepository;
import org.estatio.module.capex.dom.documents.LookupAttachedPdfService;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItemRepository;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.module.capex.dom.state.NatureOfTransition;
import org.estatio.module.capex.dom.state.StateTransitionRepositoryGeneric;
import org.estatio.module.capex.dom.util.InvoicePageRange;
import org.estatio.module.capex.platform.pdfmanipulator.PdfManipulator;
import org.estatio.module.countryapptenancy.dom.CountryServiceForCurrentUser;
import org.estatio.module.invoice.dom.InvoiceItem;

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
            final Property property,
            final Country country,
            final LocalDate reportedDate,
            final IncomingInvoiceType incomingInvoiceType) {
        this.reportedDate = reportedDate;
        this.property = property;
        this.country = country;
        this.incomingInvoiceType = incomingInvoiceType;
    }

    @XmlElement(required = false)
    @Nullable
    @Getter @Setter
    private Property property;

    @XmlElement(required = false)
    @Nullable
    @Getter @Setter
    private Country country;

    @XmlTransient
    @Programmatic
    public boolean isAllUnreported() {
        return getReportedDate() == null;
    }

    @XmlElement(required = false)
    @Nullable
    @Getter @Setter
    @XmlJavaTypeAdapter(JodaLocalDateStringAdapter.ForJaxb.class)
    private LocalDate reportedDate;

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
        List<IncomingInvoiceItem> result = new ArrayList<>();
        if(getIncomingInvoiceType() == null) {
            result.addAll(incomingInvoiceItemRepository.findCompletedOrLaterByFixedAssetAndReportedDate(
                    getProperty(), getReportedDate()));
        } else {
            result.addAll(incomingInvoiceItemRepository.findCompletedOrLaterByFixedAssetAndIncomingInvoiceTypeAndReportedDate(
                    getProperty(), getIncomingInvoiceType(), getReportedDate()));
        }
        return filterInvoiceItemsByCountryOfBuyer(getCountry(), result);
    }

    List<IncomingInvoiceItem> filterInvoiceItemsByCountryOfBuyer(final Country country, final List<IncomingInvoiceItem> invoiceItems){
        return country == null
                    ? invoiceItems
                    : invoiceItems.stream()
                .filter(x->x.getInvoice().getBuyer().getApplicationTenancyPath().contains(country.getReference()))
                .collect(Collectors.toList());
    }


    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    public IncomingInvoiceDownloadManager report() {
        LocalDate reportedDate = clockService.now();
        final List<IncomingInvoiceItem> invoiceItems = getInvoiceItems();
        for (IncomingInvoiceItem invoiceItem : invoiceItems) {
            if(invoiceItem.getReportedDate() == null) {
                invoiceItem.setReportedDate(reportedDate);
            }
        }
        return new IncomingInvoiceDownloadManager(
                property, country, reportedDate, incomingInvoiceType);
    }
    public String disableReport() {
        ReasonBuffer2 buf = ReasonBuffer2.forSingle();
        buf.append(getReportedDate() != null, "Clear 'report date' in order to report on all items currently unreported.");
        buf.append(getInvoices().isEmpty(), "No invoices");
        return buf.getReason();
    }



    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(contributed= Contributed.AS_ACTION)
    public IncomingInvoiceDownloadManager changeReportedDate(
            @Nullable
            final LocalDate reportedDate){
        return new IncomingInvoiceDownloadManager(
                property, country, reportedDate, incomingInvoiceType);
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
            @Nullable
            final Property property){
        return new IncomingInvoiceDownloadManager(
                property, property!=null ? property.getCountry() : null, reportedDate, incomingInvoiceType);
    }

    public Property default0ChangeProperty() {
        return getProperty();
    }

    public List<Property> choices0ChangeProperty() {
        return propertyRepository.allProperties();
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(contributed= Contributed.AS_ACTION)
    public IncomingInvoiceDownloadManager changeCountry(
            @Nullable
            final Country country){
        return new IncomingInvoiceDownloadManager(
                property, country, reportedDate, incomingInvoiceType);
    }

    public List<Country> choices0ChangeCountry() {
        return countryServiceForCurrentUser.countriesForCurrentUser();
    }

    public Country default0ChangeCountry() {
        return getCountry();
    }



    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(contributed= Contributed.AS_ACTION)
    public IncomingInvoiceDownloadManager changeType(
            @Nullable
            final IncomingInvoiceType incomingInvoiceType){
        return new IncomingInvoiceDownloadManager(
                property, country, reportedDate, incomingInvoiceType);
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
                .sorted(Comparator.comparing(x -> x.getDocumentNumber()!=null ? x.getDocumentNumber() : "_No_Document")) // guard only for (demo)fixtures because in production a document can be expected
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
    public Blob downloadToExcelForAllProperties(final LocalDate startDate, final LocalDate endDate, final Country country, @Nullable final String fileName) {

        final List<IncomingInvoiceExport> exports = getReportedInvoiceItemsWithPropertyForPeriodAndCountry(startDate, endDate, country).stream()
                .map(item -> new IncomingInvoiceExport(
                        item,
                        documentNumberFor(item),
                        codaElementFor(item),
                        commentsFor(item)))
                .sorted(Comparator.comparing(x -> x.getDocumentNumber()!=null ? x.getDocumentNumber() : "_No_Document")) // guard only for (demo)fixtures because in production a document can be expected
                .collect(Collectors.toList());

        WorksheetSpec spec = new WorksheetSpec(IncomingInvoiceDownloadManager.exportClass, "invoiceExport");
        WorksheetContent worksheetContent = new WorksheetContent(exports, spec);
        return excelService.toExcel(worksheetContent, fileName!=null ? fileName.concat(".xlsx") : fileNameAllProperties(startDate, endDate));
    }

    public List<LocalDate> choices0DownloadToExcelForAllProperties() {
        return incomingInvoiceItemRepository.findDistinctReportDates();
    }

    public List<LocalDate> choices1DownloadToExcelForAllProperties(final LocalDate startDate) {
        return startDate!=null ? incomingInvoiceItemRepository.findDistinctReportDates().stream().filter(x->!x.isBefore(startDate)).collect(Collectors.toList()) : null;
    }

    public List<Country> choices2DownloadToExcelForAllProperties(){
        return countryServiceForCurrentUser.countriesForCurrentUser();
    }

    public String validateDownloadToExcelForAllProperties(final LocalDate startDate, final LocalDate endDate, final Country country, final String fileName){
        if (endDate.isBefore(startDate)) return "End date cannot be before start date";
        return null;
    }

    @Programmatic
    public String fileNameAllProperties(final LocalDate startDate, final LocalDate endDate) {
        String defaultFileName = String.format("%s_%s_%s_%s",
                exportClass.getSimpleName(),
                "all_properties",
                startDate,
                endDate
        );
        return defaultFileName.concat(".xlsx");
    }

    @Programmatic
    List<IncomingInvoiceItem> getReportedInvoiceItemsWithPropertyForPeriodAndCountry(final LocalDate startDate, final LocalDate endDate, final Country country){
        List<IncomingInvoiceItem> result = new ArrayList<>();
        List<LocalDate> reportedDatesInRange = incomingInvoiceItemRepository.findDistinctReportDates().stream().filter(x->!x.isBefore(startDate) && !x.isAfter(endDate)).collect(Collectors.toList());
        for (LocalDate reportedDate : reportedDatesInRange){
            result.addAll(incomingInvoiceItemRepository.findCompletedOrLaterByReportedDate(reportedDate).stream()
                    .filter(x->x.getFixedAsset()!=null)
                    .filter(x->hasCountry(x.getFixedAsset(), country))
                    .collect(Collectors.toList()));
        }
        return result;
    }

    private boolean hasCountry(final FixedAsset fixedAsset, final Country country){
        if (fixedAsset!=null && fixedAsset.getClass().isAssignableFrom(Property.class)){
            Property castedFa = (Property) fixedAsset;
            if (castedFa.getCountry()!=null && castedFa.getCountry()==country){
                return true;
            }
        }
        return false;
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

        final byte[] singlePdf = pdfBoxService.merge(fileList);

        preparers.forEach(DocumentPreparer::cleanup);

        return new Blob(fileName, MimeTypes.APPLICATION_PDF.asStr(), singlePdf);
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
        return InvoicePageRange.firstPageChoices();
    }
    public Integer default2DownloadToPdfSingle() {
        return 1;
    }
    public List<Integer> choices2DownloadToPdfSingle() {
        return InvoicePageRange.lastPageChoices();
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
                getProperty() != null
                        ? getProperty().getReference()
                        : "withNoProperty";

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



    @DomainService(nature = NatureOfService.DOMAIN)
    public static class TableColumnOrderServiceForDownloadManager implements TableColumnOrderService
 {

     @Override
     public List<String> orderParented(
             final Object parent,
             final String collectionId,
             final Class<?> collectionType,
             final List<String> propertyIds) {
         if (!(parent instanceof IncomingInvoiceDownloadManager)) {
             return null;
         }

         return Arrays.asList(
                 "buyer",
                 "seller",
                 "type",
                 //"property",
                 //"atPath",
                 //"number",
                 "grossAmount",
                 //"bankAccount",
                 "paymentMethod",
                 "dateReceived",
                 "invoiceDate",
                 "dueDate",
                 "approvalState"
         );
     }

     @Override
     public List<String> orderStandalone(final Class<?> collectionType, final List<String> propertyIds) {
         return null;
     }
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
    PdfBoxService pdfBoxService;

    @javax.inject.Inject
    @XmlTransient
    ZipService zipService;

    @javax.inject.Inject
    @XmlTransient
    ClockService clockService;

    @javax.inject.Inject
    @XmlTransient
    CountryServiceForCurrentUser countryServiceForCurrentUser;


}

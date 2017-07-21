package org.estatio.capex.dom.invoice.manager;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalState;

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

    @Action(semantics = SemanticsOf.SAFE)
    public Blob downloadToExcel() {
        final Class exportClass = IncomingInvoiceExport.class;

        final String fileName = String.format("%s_%s-%s",
                        exportClass.getSimpleName(), getFromInputDate().toString("yyyyMMdd"),
                        exportClass.getSimpleName(), getToInputDate().toString("yyyyMMdd"));

        final List<IncomingInvoiceExport> exports = getInvoiceItems().stream()
                .map(x -> new IncomingInvoiceExport(x))
                .collect(Collectors.toList());

        WorksheetSpec spec = new WorksheetSpec(exportClass, "invoiceExport");
        WorksheetContent worksheetContent = new WorksheetContent(exports, spec);
        return excelService.toExcel(worksheetContent, fileName.concat(".xlsx"));
    }

    @Action(semantics = SemanticsOf.SAFE)
    public Blob downloadToPdf() {
        final String fileName = "export.xlsx";

        final List<IncomingInvoiceExport> exports = getInvoiceItems().stream()
                .map(x -> new IncomingInvoiceExport(x))
                .collect(Collectors.toList());

        WorksheetSpec spec = new WorksheetSpec(IncomingInvoiceExport.class, "invoiceExport");
        WorksheetContent worksheetContent = new WorksheetContent(exports, spec);
        return excelService.toExcel(worksheetContent, fileName);
    }


    @javax.inject.Inject
    private IncomingInvoiceRepository incomingInvoiceRepository;

    @javax.inject.Inject
    private ExcelService excelService;

}

package org.estatio.capex.dom.payment.export;

import java.util.List;
import java.util.stream.Collectors;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.PromptStyle;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.excel.dom.WorksheetContent;
import org.isisaddons.module.excel.dom.WorksheetSpec;

import org.estatio.capex.dom.payment.PaymentLine;
import org.estatio.capex.dom.payment.PaymentLineRepository;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.capex.dom.invoice.manager.PaymentLineDownloadManager")
@NoArgsConstructor
public class PaymentLineDownloadManager {

    public String title() {
        return "Payment Line Download";
    }

    public PaymentLineDownloadManager(final LocalDate fromRequestedExecutionDate){
        this.fromRequestedExecutionDate = fromRequestedExecutionDate;
    }

    @Getter @Setter
    @PropertyLayout(promptStyle = PromptStyle.INLINE)
    private LocalDate fromRequestedExecutionDate;

    @MemberOrder(sequence = "1", name = "fromRequestedExecutionDate")
    public PaymentLineDownloadManager editFromRequestedExecutionDate(LocalDate fromRequestedExecutionDate){
        return new PaymentLineDownloadManager(fromRequestedExecutionDate);
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(defaultView = "table")
    public List<PaymentLine> getPayments() {
        return paymentLineRepository.findFromRequestedExecutionDate(fromRequestedExecutionDate);
    }

    @Programmatic
    public PaymentLineDownloadManager init() {
        return this;
    }

    private final static Class<PaymentLineExportV1> exportClass = PaymentLineExportV1.class;

    @Action(semantics = SemanticsOf.SAFE)
    public Blob downloadToExcel(final String fileName) {

        final List<PaymentLineExportV1> exportV1s = getPayments()
                .stream()
                .map(x -> new PaymentLineExportV1(x))
                .collect(Collectors.toList());

        WorksheetSpec spec = new WorksheetSpec(exportClass, "invoiceExport");
        WorksheetContent worksheetContent = new WorksheetContent(exportV1s, spec);
        return excelService.toExcel(worksheetContent, fileName);
    }

    public String default0DownloadToExcel() {
        final String fileName = String.format("%s_%s",
                exportClass.getSimpleName(), getFromRequestedExecutionDate().toString("yyyyMMdd"));
        return fileName.concat(".xlsx");
    }


    @javax.inject.Inject
    private PaymentLineRepository paymentLineRepository;

    @javax.inject.Inject
    private ExcelService excelService;

}

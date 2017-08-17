package org.estatio.capex.dom.invoice.manager;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.excel.dom.WorksheetContent;
import org.isisaddons.module.excel.dom.WorksheetSpec;

//region > downloadToExcel (action)
@Mixin(method="act")
public class IncomingInvoiceDownloadManager_downloadToExcel extends IncomingInvoiceDownloadManager_downloadAbstract {
    public IncomingInvoiceDownloadManager_downloadToExcel(final IncomingInvoiceDownloadManager manager) {
        super(manager);
    }
    @Action(semantics = SemanticsOf.SAFE)
    public Blob act(final String fileName) {

        final List<IncomingInvoiceExport> exports = manager.getInvoiceItems().stream()
                .map(item -> new IncomingInvoiceExport(
                                    item,
                                    manager.documentNumberFor(item),
                                    manager.codaElementFor(item),
                                    manager.commentsFor(item)))
                .sorted(Comparator.comparing(x -> x.getDocumentNumber()))
                .collect(Collectors.toList());

        WorksheetSpec spec = new WorksheetSpec(IncomingInvoiceDownloadManager.exportClass, "invoiceExport");
        WorksheetContent worksheetContent = new WorksheetContent(exports, spec);
        return excelService.toExcel(worksheetContent, fileName);
    }

    public String default0Act() {
        return manager.defaultFileNameWithSuffix(".xlsx");
    }

    @Override
    public String disableAct() {
        return super.disableAct();
    }

    @javax.inject.Inject
    private ExcelService excelService;

}

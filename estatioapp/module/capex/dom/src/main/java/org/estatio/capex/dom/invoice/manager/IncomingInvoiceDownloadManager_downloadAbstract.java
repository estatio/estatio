package org.estatio.capex.dom.invoice.manager;

import org.isisaddons.module.excel.dom.ExcelService;

public abstract class IncomingInvoiceDownloadManager_downloadAbstract {

    protected final IncomingInvoiceDownloadManager manager;
    public IncomingInvoiceDownloadManager_downloadAbstract(final IncomingInvoiceDownloadManager manager) {
        this.manager = manager;
    }
    protected String disableAct() {
        return manager.getInvoices().isEmpty() ? "No invoices to download": null;
    }

    @javax.inject.Inject
    private ExcelService excelService;

}

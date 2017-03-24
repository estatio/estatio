package org.estatio.dom.lease.invoicing.viewmodel;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.dom.invoice.InvoiceStatus;

@Mixin(method = "act")
public class InvoiceSummaryForPropertyDueDateStatus_statusOfInvoiced extends
        InvoiceSummaryForPropertyDueDateStatus_statusOfAbstract {

    public InvoiceSummaryForPropertyDueDateStatus_statusOfInvoiced(final InvoiceSummaryForPropertyDueDateStatus invoiceSummary) {
        super(invoiceSummary, InvoiceStatus.INVOICED);
    }
}

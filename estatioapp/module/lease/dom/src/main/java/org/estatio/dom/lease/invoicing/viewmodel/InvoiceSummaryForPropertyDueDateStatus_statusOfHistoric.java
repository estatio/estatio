package org.estatio.dom.lease.invoicing.viewmodel;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.dom.invoice.InvoiceStatus;

@Mixin(method = "act")
public class InvoiceSummaryForPropertyDueDateStatus_statusOfHistoric extends
        InvoiceSummaryForPropertyDueDateStatus_statusOfAbstract {

    public InvoiceSummaryForPropertyDueDateStatus_statusOfHistoric(final InvoiceSummaryForPropertyDueDateStatus invoiceSummary) {
        super(invoiceSummary, InvoiceStatus.HISTORIC);
    }
}

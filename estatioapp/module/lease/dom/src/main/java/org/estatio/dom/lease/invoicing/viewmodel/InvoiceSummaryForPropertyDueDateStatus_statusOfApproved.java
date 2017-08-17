package org.estatio.dom.lease.invoicing.viewmodel;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.dom.invoice.InvoiceStatus;

/**
 * TODO: REVIEW: could inline this mixin, however inherits functionality from superclass, so maybe best left as is?
 */
@Mixin(method = "act")
public class InvoiceSummaryForPropertyDueDateStatus_statusOfApproved extends
        InvoiceSummaryForPropertyDueDateStatus_statusOfAbstract {

    public InvoiceSummaryForPropertyDueDateStatus_statusOfApproved(final InvoiceSummaryForPropertyDueDateStatus invoiceSummary) {
        super(invoiceSummary, InvoiceStatus.APPROVED);
    }
}

package org.estatio.module.lease.dom.invoicing.summary;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.dom.invoice.InvoiceStatus;

/**
 * TODO: REVIEW: could inline this mixin, however inherits functionality from superclass, so maybe best left as is?
 */
@Mixin(method = "act")
public class InvoiceSummaryForPropertyDueDateStatus_statusOfNew extends
        InvoiceSummaryForPropertyDueDateStatus_statusOfAbstract {

    public InvoiceSummaryForPropertyDueDateStatus_statusOfNew(final InvoiceSummaryForPropertyDueDateStatus invoiceSummary) {
        super(invoiceSummary, InvoiceStatus.NEW);
    }
}

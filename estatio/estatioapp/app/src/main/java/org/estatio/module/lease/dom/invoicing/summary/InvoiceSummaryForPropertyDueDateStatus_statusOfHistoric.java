package org.estatio.module.lease.dom.invoicing.summary;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.module.invoice.dom.InvoiceStatus;

/**
 * TODO: REVIEW: could inline this mixin, however inherits functionality from superclass, so maybe best left as is?
 */
@Mixin(method = "act")
public class InvoiceSummaryForPropertyDueDateStatus_statusOfHistoric extends
        InvoiceSummaryForPropertyDueDateStatus_statusOfAbstract {

    public InvoiceSummaryForPropertyDueDateStatus_statusOfHistoric(final InvoiceSummaryForPropertyDueDateStatus invoiceSummary) {
        super(invoiceSummary, InvoiceStatus.HISTORIC);
    }
}

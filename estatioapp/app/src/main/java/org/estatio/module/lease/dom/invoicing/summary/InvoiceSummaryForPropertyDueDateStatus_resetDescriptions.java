package org.estatio.module.lease.dom.invoicing.summary;

import java.util.List;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.lease.dom.invoicing.InvoiceForLease;

/**
 * TODO: inline this mixin
 */
@Mixin(method = "act")
public class InvoiceSummaryForPropertyDueDateStatus_resetDescriptions {

    private final InvoiceSummaryForPropertyDueDateStatus summary;

    public InvoiceSummaryForPropertyDueDateStatus_resetDescriptions(final InvoiceSummaryForPropertyDueDateStatus summary) {
        this.summary = summary;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    public InvoiceSummaryForPropertyDueDateStatus act() {

        final List<InvoiceForLease> invoices = summary.getInvoices();
        for (final InvoiceForLease ninvoice : invoices) {
            ninvoice.resetDescriptions();
        }
        return summary;
    }

}

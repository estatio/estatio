package org.estatio.dom.lease.invoicing.viewmodel;

import java.util.List;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.lease.invoicing.InvoiceForLease;
import org.estatio.dom.lease.invoicing.InvoiceForLease_resetSendTo;

/**
 * TODO: inline this mixin
 */
@Mixin(method = "act")
public class InvoiceSummaryForPropertyDueDateStatus_resetSendToAll {

    private final InvoiceSummaryForPropertyDueDateStatus summary;

    public InvoiceSummaryForPropertyDueDateStatus_resetSendToAll(final InvoiceSummaryForPropertyDueDateStatus summary) {
        this.summary = summary;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    public InvoiceSummaryForPropertyDueDateStatus act() {

        final List<InvoiceForLease> invoices = summary.getInvoices();
        for (final InvoiceForLease invoice : invoices) {
            service.apply(invoice);
        }

        return summary;
    }

    @javax.inject.Inject
    InvoiceForLease_resetSendTo.Service service;
}

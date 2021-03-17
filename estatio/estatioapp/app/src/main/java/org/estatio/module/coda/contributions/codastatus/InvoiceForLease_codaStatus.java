package org.estatio.module.coda.contributions.codastatus;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.lease.dom.invoicing.InvoiceForLease;

@Mixin(method="prop")
public class InvoiceForLease_codaStatus {


    private final InvoiceForLease invoice;

    public InvoiceForLease_codaStatus(InvoiceForLease invoice) {
        this.invoice = invoice;
    }

    /**
     * Returns the text of the first status message of most recent published event.
     */
    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public String prop() {
        final StatusMessageSummary summary = statusMessageSummaryCache.findFor(invoice);
        return summary != null ? summary.getMessage() : null;
    }

    @Inject
    StatusMessageSummaryCache statusMessageSummaryCache;

}

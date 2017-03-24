package org.estatio.dom.lease.invoicing;

import java.util.List;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;

import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.invoicing.viewmodel.InvoiceSummaryForPropertyDueDateStatus;

@Mixin(method = "act")
public class InvoiceSummaryForPropertyDueDateStatus_resetSendToOverride {

    private final InvoiceSummaryForPropertyDueDateStatus summary;

    public InvoiceSummaryForPropertyDueDateStatus_resetSendToOverride(final InvoiceSummaryForPropertyDueDateStatus summary) {
        this.summary = summary;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    public InvoiceSummaryForPropertyDueDateStatus act() {

        final List<InvoiceForLease> invoices = summary.getInvoices();
        for (final InvoiceForLease invoice : invoices) {
            final Lease lease = invoice.getLease();
            final CommunicationChannel sendTo = repository.firstCurrentTenantInvoiceAddress(lease);
            invoice.setSendTo(sendTo);
        }

        return summary;
    }

    @javax.inject.Inject
    InvoiceForLeaseRepository repository;
}

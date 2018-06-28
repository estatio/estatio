package org.estatio.module.coda.contributions.republish;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CommandPersistence;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.invoice.dom.InvoiceRepository;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;
import org.estatio.module.lease.dom.invoicing.summary.InvoiceSummaryAbstract;

@Mixin(method="act")
public class InvoiceSummaryAbstract_republishAll {

    private final InvoiceSummaryAbstract invoiceSummaryAbstract;

    public InvoiceSummaryAbstract_republishAll(InvoiceSummaryAbstract invoiceSummaryAbstract) {
        this.invoiceSummaryAbstract = invoiceSummaryAbstract;
    }

    @Action(
            commandPersistence = CommandPersistence.NOT_PERSISTED,
            publishing = Publishing.DISABLED,
            semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE
    )
    @ActionLayout(
            cssClassFa = "share-alt",
            cssClass = "btn-warning",
            contributed = Contributed.AS_ACTION
    )
    @MemberOrder(sequence = "999")
    public InvoiceSummaryAbstract act() {
        for (InvoiceForLease invoice : invoiceSummaryAbstract.getInvoices()) {
            invoiceRepublisherService.republishIfPresent(invoice);
        }
        return invoiceSummaryAbstract;
    }

    @Inject
    InvoiceRepublisherService invoiceRepublisherService;

    @Inject
    InvoiceRepository invoiceRepository;


}

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

import org.estatio.module.lease.dom.invoicing.InvoiceForLease;

@Mixin(method="act")
public class InvoiceForLease_republish {

    private final InvoiceForLease invoice;

    public InvoiceForLease_republish(InvoiceForLease invoice) {
        this.invoice = invoice;
    }

    @Action(
            commandPersistence = CommandPersistence.NOT_PERSISTED,
            publishing = Publishing.DISABLED,
            semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE
    )
    @ActionLayout(
            cssClassFa = "share-alt",
            contributed = Contributed.AS_ACTION
    )
    @MemberOrder(sequence = "999")
    public InvoiceForLease act() {
        invoiceRepublisherService.republishIfPresent(invoice);
        return invoice;
    }

    public String disableAct() {
        return invoiceRepublisherService.disableRepublishIfPresent(invoice);
    }

    @Inject
    InvoiceRepublisherService invoiceRepublisherService;

}

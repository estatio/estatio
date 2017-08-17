package org.estatio.dom.invoice;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.dom.party.Party;

/**
 * This cannot be inlined (needs to be a mixin) because Party does not know about invoices.
 */
@Mixin(method="coll")
public class Party_invoicesFrom {
    private final Party seller;
    public Party_invoicesFrom(final Party seller) { this.seller = seller; }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed= Contributed.AS_ASSOCIATION)
    public List<IncomingInvoice> coll() {
        return invoiceRepository.findBySeller(seller)
                .stream()
                .filter(IncomingInvoice.class::isInstance)
                .map(IncomingInvoice.class::cast).collect(
                Collectors.toList());
    }
    @Inject
    InvoiceRepository invoiceRepository;
}

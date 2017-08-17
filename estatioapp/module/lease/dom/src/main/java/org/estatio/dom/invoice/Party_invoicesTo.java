package org.estatio.dom.invoice;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.party.Party;

@Mixin(method="coll")
public class Party_invoicesTo {
    private final Party buyer;
    public Party_invoicesTo(final Party buyer) { this.buyer = buyer; }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed= Contributed.AS_ASSOCIATION)
    public List<Invoice> coll() {
        return invoiceRepository.findByBuyer(buyer);
    }
    @Inject
    InvoiceRepository invoiceRepository;
}

package org.estatio.dom.invoice;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.party.Party;

public class Party_invoiceContributions {

    @Mixin(method="coll")
    public static class Party_invoicesFrom {
        private final Party seller;
        public Party_invoicesFrom(final Party seller) { this.seller = seller; }

        @Action(semantics = SemanticsOf.SAFE)
        @ActionLayout(contributed=Contributed.AS_ASSOCIATION)
        public List<Invoice> coll() {
            return invoiceRepository.findBySeller(seller);
        }
        @Inject
        InvoiceRepository invoiceRepository;
    }

    @Mixin(method="coll")
    public static class Party_invoicesTo {
        private final Party buyer;
        public Party_invoicesTo(final Party buyer) { this.buyer = buyer; }

        @Action(semantics = SemanticsOf.SAFE)
        @ActionLayout(contributed=Contributed.AS_ASSOCIATION)
        public List<Invoice> coll() {
            return invoiceRepository.findByBuyer(buyer);
        }
        @Inject
        InvoiceRepository invoiceRepository;
    }


}

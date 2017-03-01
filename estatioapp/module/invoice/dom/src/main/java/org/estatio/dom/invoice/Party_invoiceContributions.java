package org.estatio.dom.invoice;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.party.Party;

@DomainService(
        nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY
)
public class Party_invoiceContributions {

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<Invoice> invoicesFrom(final Party seller) {
        return invoiceRepository.findBySeller(seller);
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<Invoice> invoicesTo(final Party buyer) {
        return invoiceRepository.findByBuyer(buyer);
    }


    @Inject
    InvoiceRepository invoiceRepository;

}

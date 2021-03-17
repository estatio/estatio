package org.estatio.module.capex.contributions;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.module.party.dom.Party;

@Mixin
public class Party_discardedInvoicesFrom {

    private final Party seller;

    public Party_discardedInvoicesFrom(final Party seller) {
        this.seller = seller;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed= Contributed.AS_ASSOCIATION)
    public List<IncomingInvoice> $$() {
        return incomingInvoiceRepository.findBySellerAndApprovalStates(seller, Arrays.asList(IncomingInvoiceApprovalState.DISCARDED));
    }
    @Inject
    private IncomingInvoiceRepository incomingInvoiceRepository;
}

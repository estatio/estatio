package org.estatio.module.capex.contributions;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

/**
 * This cannot be inlined (needs to be a mixin) because Party does not know about invoices.
 */
@Mixin(method = "coll")
public class Party_invoicesFrom {

    private final Party seller;

    public Party_invoicesFrom(final Party seller) {
        this.seller = seller;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<IncomingInvoice> coll() {
        EnumSet<IncomingInvoiceApprovalState> allStatesExceptDiscarded = EnumSet.complementOf(EnumSet.of(IncomingInvoiceApprovalState.DISCARDED));
        List<IncomingInvoice> allInvoicesNotDiscardedOrNull = incomingInvoiceRepository.findBySellerAndApprovalStates(seller, new ArrayList<>(allStatesExceptDiscarded));
        List<IncomingInvoice> allInvoicesApprovalStateNull = incomingInvoiceRepository.findBySellerAndApprovalStateIsNull(seller);

        return Stream.concat(allInvoicesNotDiscardedOrNull.stream(), allInvoicesApprovalStateNull.stream()).collect(Collectors.toList());
    }

    @Inject
    IncomingInvoiceRepository incomingInvoiceRepository;
}

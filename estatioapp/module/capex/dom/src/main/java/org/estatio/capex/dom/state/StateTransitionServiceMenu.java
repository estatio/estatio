package org.estatio.capex.dom.state;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.RestrictTo;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;

@DomainService(nature = NatureOfService.VIEW_MENU_ONLY, objectType = "prototyping.StateTransitionServiceMenu")
@DomainServiceLayout(named = "Prototyping", menuBar = DomainServiceLayout.MenuBar.SECONDARY)
public class StateTransitionServiceMenu {


    @Action(restrictTo = RestrictTo.PROTOTYPING)
    public void fixUpTransitionsForAllIncomingInvoices() {

        final List<IncomingInvoice> incomingInvoices = incomingInvoiceRepository.listAll();
        for (IncomingInvoice incomingInvoice : incomingInvoices) {
            stateTransitionService.trigger(incomingInvoice, IncomingInvoiceApprovalStateTransition.class, null, null);
        }

    }


    @Inject
    IncomingInvoiceRepository incomingInvoiceRepository;

    @Inject
    StateTransitionService stateTransitionService;



}

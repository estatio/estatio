package org.estatio.capex.dom.invoice.approval;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.state.StateTransitionEvent;

@DomainService(nature = NatureOfService.DOMAIN)
public class IncomingInvoiceApprovedFullySubscriber extends AbstractSubscriber {

    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void on(IncomingInvoiceApprovalStateTransitionType.TransitionEvent ev) {

        if(ev.getPhase() != StateTransitionEvent.Phase.TRANSITIONED) {
            return;
        }

        final IncomingInvoiceApprovalStateTransitionType transitionType = ev.getTransitionType();
        switch (transitionType) {
        case CHECK_BANK_ACCOUNT:
            ev.getDomainObject().setApprovedFully(true);
            break;
        }
    }

    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void on(IncomingInvoice.ApprovalInvalidatedEvent ev) {
        ev.getIncomingInvoice().setApprovedFully(false);
    }


}

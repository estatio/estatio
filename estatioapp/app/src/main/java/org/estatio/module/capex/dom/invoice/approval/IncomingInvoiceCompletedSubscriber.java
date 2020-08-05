package org.estatio.module.capex.dom.invoice.approval;

import javax.inject.Inject;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.module.capex.dom.invoice.accountingaudit.IncomingInvoiceAccountingStateTransitionType;
import org.estatio.module.task.dom.state.StateTransitionEvent;
import org.estatio.module.task.dom.state.StateTransitionService;

@DomainService(nature = NatureOfService.DOMAIN)
public class IncomingInvoiceCompletedSubscriber extends AbstractSubscriber {

    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void on(IncomingInvoiceApprovalStateTransitionType.TransitionEvent ev) {

        if(ev.getPhase() != StateTransitionEvent.Phase.TRANSITIONED) {
            return;
        }

        final IncomingInvoiceApprovalStateTransitionType transitionType = ev.getTransitionType();
        switch (transitionType) {
        case COMPLETE:
            stateTransitionService.trigger(ev.getDomainObject(), IncomingInvoiceAccountingStateTransitionType.INVOICE_COMPLETED, null, null);
            break;
        }
    }

    @Inject
    StateTransitionService stateTransitionService;

}

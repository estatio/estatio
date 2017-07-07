package org.estatio.capex.dom.order.approval;

import javax.inject.Inject;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.capex.dom.order.Order;
import org.estatio.capex.dom.state.StateTransitionService;

@DomainService(nature = NatureOfService.DOMAIN)
public class OrderApprovalStateSubscriber extends AbstractSubscriber {

    /**
     * This entire callback is a workaround, because we can't mutate the state of the domain object
     * in the persisted callback
     */
    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void on(Order.ObjectPersistingEvent ev) {
        final Order order = ev.getSource();

        // for the OrderInvoiceLine import from existing spreadsheets, will be set to APPROVED, so do nothing
        if(order.getApprovalState() == null) {
            order.setApprovalState(OrderApprovalStateTransitionType.INSTANTIATE.getToState());
        }
    }

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void on(Order.ObjectPersistedEvent ev) {
        // nb: note that the order at this stage has no items attached to it,
        // so there is a limit as to what we can safely do.
        // however, it *is* ok to just create the state chart for the domain object.
        final Order order = ev.getSource();

        OrderApprovalState approvalState = order.getApprovalState();
        if(approvalState == OrderApprovalStateTransitionType.INSTANTIATE.getToState()) {
            // ie was set in the persisting callback
            stateTransitionService.trigger(order, OrderApprovalStateTransitionType.INSTANTIATE, null, null);
        }
    }


    @Inject
    StateTransitionService stateTransitionService;


}

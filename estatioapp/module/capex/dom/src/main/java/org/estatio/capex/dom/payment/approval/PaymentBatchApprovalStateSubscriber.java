package org.estatio.capex.dom.payment.approval;

import javax.inject.Inject;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.module.capex.dom.payment.PaymentBatch;
import org.estatio.module.capex.dom.state.StateTransitionService;

@DomainService(nature = NatureOfService.DOMAIN)
public class PaymentBatchApprovalStateSubscriber extends AbstractSubscriber {

    /**
     * This entire callback is a workaround, because we can't mutate the state of the domain object
     * in the persisted callback
     */
    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void on(PaymentBatch.ObjectPersistingEvent ev) {
        final PaymentBatch order = ev.getSource();

        // for the OrderInvoiceLine import from existing spreadsheets, will be set to APPROVED, so do nothing
        if(order.getApprovalState() == null) {
            order.setApprovalState(PaymentBatchApprovalStateTransitionType.INSTANTIATE.getToState());
        }
    }

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void on(PaymentBatch.ObjectPersistedEvent ev) {
        // nb: note that the batch at this stage has no lines attached to it,
        // so there is a limit as to what we can safely do.
        // however, it *is* ok to just create the state chart for the domain object.
        final PaymentBatch paymentBatch = ev.getSource();

        PaymentBatchApprovalState approvalState = paymentBatch.getApprovalState();
        if(approvalState == PaymentBatchApprovalStateTransitionType.INSTANTIATE.getToState()) {
            // ie was set in the persisting callback
            stateTransitionService.trigger(paymentBatch, PaymentBatchApprovalStateTransitionType.INSTANTIATE, null, null);
        }
    }


    @Inject
    StateTransitionService stateTransitionService;


}

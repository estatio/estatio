package org.estatio.capex.dom.invoice.payment.approval.transitions;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.capex.dom.invoice.payment.Payment;
import org.estatio.capex.dom.invoice.payment.approval.PaymentApprovalStateTransitionType;
import org.estatio.capex.dom.state.StateTransitionService;

public abstract class Payment_abstractTransition {

    protected final Payment payment;
    protected final PaymentApprovalStateTransitionType transitionType;

    protected Payment_abstractTransition(
            final Payment payment,
            final PaymentApprovalStateTransitionType transitionType) {
        this.payment = payment;
        this.transitionType = transitionType;
    }

    @Action()
    public Payment $$(@Nullable
                      final String comment){
        stateTransitionService.apply(payment, transitionType, comment);
        return payment;
    }

    public boolean hide$$() {
        return !stateTransitionService.canApply(payment, transitionType);
    }

    @Inject
    private ServiceRegistry2 serviceRegistry2;
    @Inject
    private StateTransitionService stateTransitionService;

}

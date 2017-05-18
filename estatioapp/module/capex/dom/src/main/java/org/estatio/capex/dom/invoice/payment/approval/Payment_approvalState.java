package org.estatio.capex.dom.invoice.payment.approval;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.invoice.payment.Payment;
import org.estatio.capex.dom.state.StateTransitionService;

@Mixin(method="prop")
public class Payment_approvalState {

    private final Payment payment;
    public Payment_approvalState(final Payment payment) {
        this.payment = payment;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed= Contributed.AS_ASSOCIATION)
    public PaymentApprovalState prop() {
        return stateTransitionService.currentStateOf(payment, PaymentApprovalStateTransition.class);
    }
    public boolean hide() {
        return false;
    }

    @Inject
    StateTransitionService stateTransitionService;
}

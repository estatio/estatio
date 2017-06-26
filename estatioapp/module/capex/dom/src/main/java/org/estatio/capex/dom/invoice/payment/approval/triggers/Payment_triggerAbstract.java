package org.estatio.capex.dom.invoice.payment.approval.triggers;

import java.util.List;

import org.apache.isis.applib.annotation.Action;

import org.estatio.capex.dom.invoice.payment.Payment;
import org.estatio.capex.dom.invoice.payment.approval.PaymentApprovalState;
import org.estatio.capex.dom.invoice.payment.approval.PaymentApprovalStateTransition;
import org.estatio.capex.dom.invoice.payment.approval.PaymentApprovalStateTransitionType;
import org.estatio.capex.dom.triggers.DomainObject_triggerAbstract;

abstract class Payment_triggerAbstract
        extends DomainObject_triggerAbstract<
                                    Payment,
                                    PaymentApprovalStateTransition,
                                    PaymentApprovalStateTransitionType,
                                    PaymentApprovalState> {

    Payment_triggerAbstract(
            final Payment payment,
            final List<PaymentApprovalState> fromStates,
            final PaymentApprovalStateTransitionType requiredTransitionTypeIfAny) {
        super(payment, PaymentApprovalStateTransition.class, fromStates, requiredTransitionTypeIfAny);
    }

    Payment_triggerAbstract(
            final Payment payment,
            final PaymentApprovalStateTransitionType requiredTransitionType) {
        super(payment, PaymentApprovalStateTransition.class, requiredTransitionType.getFromStates(), requiredTransitionType);
    }

    @Action()
    public Payment act(final String comment) {
        trigger(comment);
        return getDomainObject();
    }

    public boolean hideAct() {
        return cannotTransition();
    }

}

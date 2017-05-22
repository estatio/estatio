package org.estatio.capex.dom.invoice.payment.approval.transitions;

import org.estatio.capex.dom.invoice.payment.Payment;
import org.estatio.capex.dom.invoice.payment.approval.PaymentApprovalState;
import org.estatio.capex.dom.invoice.payment.approval.PaymentApprovalStateTransition;
import org.estatio.capex.dom.invoice.payment.approval.PaymentApprovalStateTransitionType;
import org.estatio.capex.dom.task.AbstractTransitionMixin;

public abstract class Payment_abstractTransition extends AbstractTransitionMixin<Payment, PaymentApprovalStateTransition, PaymentApprovalStateTransitionType, PaymentApprovalState> {

    protected Payment_abstractTransition(
            final Payment payment,
            final PaymentApprovalStateTransitionType transitionType) {
        super(payment, transitionType);
    }

}

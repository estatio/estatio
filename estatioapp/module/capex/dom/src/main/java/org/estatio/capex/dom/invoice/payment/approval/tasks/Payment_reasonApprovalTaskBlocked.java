package org.estatio.capex.dom.invoice.payment.approval.tasks;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.invoice.payment.Payment;
import org.estatio.capex.dom.invoice.payment.approval.PaymentApprovalState;
import org.estatio.capex.dom.invoice.payment.approval.PaymentApprovalStateTransition;
import org.estatio.capex.dom.invoice.payment.approval.PaymentApprovalStateTransitionType;
import org.estatio.capex.dom.task.DomainObject_reasonGuardNotSatisfied;

@Mixin(method="prop")
public class Payment_reasonApprovalTaskBlocked
        extends DomainObject_reasonGuardNotSatisfied<
                        Payment,
                        PaymentApprovalStateTransition,
                        PaymentApprovalStateTransitionType,
                        PaymentApprovalState> {

    public Payment_reasonApprovalTaskBlocked(final Payment payment) {
        super(payment, PaymentApprovalStateTransition.class);
    }


}

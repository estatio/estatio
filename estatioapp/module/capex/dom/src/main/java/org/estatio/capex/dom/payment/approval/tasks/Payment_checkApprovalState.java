package org.estatio.capex.dom.payment.approval.tasks;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.payment.Payment;
import org.estatio.capex.dom.payment.approval.PaymentApprovalState;
import org.estatio.capex.dom.payment.approval.PaymentApprovalStateTransition;
import org.estatio.capex.dom.payment.approval.PaymentApprovalStateTransitionType;
import org.estatio.capex.dom.task.DomainObject_checkStateAbstract;

@Mixin(method="act")
public class Payment_checkApprovalState
        extends DomainObject_checkStateAbstract<
                            Payment,
                            PaymentApprovalStateTransition,
                            PaymentApprovalStateTransitionType,
                            PaymentApprovalState> {

    public Payment_checkApprovalState(final Payment payment) {
        super(payment, PaymentApprovalStateTransition.class);
    }


}

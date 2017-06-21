package org.estatio.capex.dom.invoice.payment.approval.tasks;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.invoice.payment.Payment;
import org.estatio.capex.dom.invoice.payment.approval.PaymentApprovalState;
import org.estatio.capex.dom.invoice.payment.approval.PaymentApprovalStateTransition;
import org.estatio.capex.dom.invoice.payment.approval.PaymentApprovalStateTransitionType;
import org.estatio.capex.dom.task.DomainObject_checkState;

@Mixin(method="act")
public class Payment_checkApprovalState
        extends DomainObject_checkState<
                    Payment,
                    PaymentApprovalStateTransition,
                    PaymentApprovalStateTransitionType,
                    PaymentApprovalState> {

    public Payment_checkApprovalState(final Payment payment) {
        super(payment, PaymentApprovalStateTransition.class);
    }


}

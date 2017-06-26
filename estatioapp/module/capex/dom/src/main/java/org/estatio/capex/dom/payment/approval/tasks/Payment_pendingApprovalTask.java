package org.estatio.capex.dom.payment.approval.tasks;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.payment.Payment;
import org.estatio.capex.dom.payment.approval.PaymentApprovalState;
import org.estatio.capex.dom.payment.approval.PaymentApprovalStateTransition;
import org.estatio.capex.dom.payment.approval.PaymentApprovalStateTransitionType;
import org.estatio.capex.dom.task.DomainObject_pendingTaskAbstract;

@Mixin(method="prop")
public class Payment_pendingApprovalTask
        extends DomainObject_pendingTaskAbstract<
                    Payment,
                    PaymentApprovalStateTransition,
                    PaymentApprovalStateTransitionType,
                    PaymentApprovalState> {

    public Payment_pendingApprovalTask(final Payment payment) {
        super(payment, PaymentApprovalStateTransition.class);
    }


}

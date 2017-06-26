package org.estatio.capex.dom.payment.approval.tasks;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.payment.Payment;
import org.estatio.capex.dom.payment.approval.PaymentApprovalState;
import org.estatio.capex.dom.payment.approval.PaymentApprovalStateTransition;
import org.estatio.capex.dom.payment.approval.PaymentApprovalStateTransitionType;
import org.estatio.capex.dom.task.DomainObject_tasksAbstract;

@Mixin(method = "coll")
public class Payment_approvalTasks extends
        DomainObject_tasksAbstract<
                Payment,
                PaymentApprovalStateTransition,
                PaymentApprovalStateTransitionType,
                PaymentApprovalState> {

    public Payment_approvalTasks(final Payment payment) {
        super(payment, PaymentApprovalStateTransition.class);
    }

}

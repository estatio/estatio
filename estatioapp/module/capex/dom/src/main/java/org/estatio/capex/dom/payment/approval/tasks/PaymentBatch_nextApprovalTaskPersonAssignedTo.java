package org.estatio.capex.dom.payment.approval.tasks;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.payment.PaymentBatch;
import org.estatio.capex.dom.payment.approval.PaymentBatchApprovalState;
import org.estatio.capex.dom.payment.approval.PaymentBatchApprovalStateTransition;
import org.estatio.capex.dom.payment.approval.PaymentBatchApprovalStateTransitionType;
import org.estatio.capex.dom.task.DomainObject_nextTaskPersonAssignedToAbstract;

@Mixin(method="prop")
public class PaymentBatch_nextApprovalTaskPersonAssignedTo
        extends DomainObject_nextTaskPersonAssignedToAbstract<
        PaymentBatch,
        PaymentBatchApprovalStateTransition,
        PaymentBatchApprovalStateTransitionType,
        PaymentBatchApprovalState> {

    public PaymentBatch_nextApprovalTaskPersonAssignedTo(final PaymentBatch paymentBatch) {
        super(paymentBatch, PaymentBatchApprovalStateTransition.class);
    }

    public boolean hideProp() {
        return super.hideProp();
    }

}

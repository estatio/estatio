package org.estatio.capex.dom.payment.approval.tasks;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.module.capex.dom.payment.PaymentBatch;
import org.estatio.capex.dom.payment.approval.PaymentBatchApprovalState;
import org.estatio.capex.dom.payment.approval.PaymentBatchApprovalStateTransition;
import org.estatio.capex.dom.payment.approval.PaymentBatchApprovalStateTransitionType;
import org.estatio.capex.dom.dobj.DomainObject_tasksAbstract;

/**
 * This mixin cannot (easily) be inlined because it inherits functionality from its superclass, and in any case
 * this follows a common pattern applicable for all domain objects that have an associated state transition machine.
 */
@Mixin(method = "coll")
public class PaymentBatch_approvalTasks extends
        DomainObject_tasksAbstract<
                PaymentBatch,
                PaymentBatchApprovalStateTransition,
                PaymentBatchApprovalStateTransitionType,
                PaymentBatchApprovalState> {

    public PaymentBatch_approvalTasks(final PaymentBatch paymentBatch) {
        super(paymentBatch, PaymentBatchApprovalStateTransition.class);
    }

}

package org.estatio.module.capex.dom.payment.approval.tasks;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.dobj.DomainObject_pendingTaskAbstract;
import org.estatio.module.capex.dom.payment.PaymentBatch;
import org.estatio.module.capex.dom.payment.approval.PaymentBatchApprovalState;
import org.estatio.module.capex.dom.payment.approval.PaymentBatchApprovalStateTransition;
import org.estatio.module.capex.dom.payment.approval.PaymentBatchApprovalStateTransitionType;

/**
 * This mixin cannot (easily) be inlined because it inherits functionality from its superclass, and in any case
 * this follows a common pattern applicable for all domain objects that have an associated state transition machine.
 */
@Mixin(method="prop")
public class PaymentBatch_pendingApprovalTask
        extends DomainObject_pendingTaskAbstract<
                PaymentBatch,
                PaymentBatchApprovalStateTransition,
                PaymentBatchApprovalStateTransitionType,
                PaymentBatchApprovalState> {

    public PaymentBatch_pendingApprovalTask(final PaymentBatch paymentBatch) {
        super(paymentBatch, PaymentBatchApprovalStateTransition.class);
    }


}

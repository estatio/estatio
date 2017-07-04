package org.estatio.capex.dom.payment.approval.tasks;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.payment.PaymentBatch;
import org.estatio.capex.dom.payment.approval.PaymentBatchApprovalState;
import org.estatio.capex.dom.payment.approval.PaymentBatchApprovalStateTransition;
import org.estatio.capex.dom.payment.approval.PaymentBatchApprovalStateTransitionType;
import org.estatio.capex.dom.dobj.DomainObject_pendingTaskAbstract;

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

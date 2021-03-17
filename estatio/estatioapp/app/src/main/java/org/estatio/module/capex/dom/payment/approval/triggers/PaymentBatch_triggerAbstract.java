package org.estatio.module.capex.dom.payment.approval.triggers;

import org.estatio.module.capex.dom.payment.PaymentBatch;
import org.estatio.module.capex.dom.payment.approval.PaymentBatchApprovalState;
import org.estatio.module.capex.dom.payment.approval.PaymentBatchApprovalStateTransition;
import org.estatio.module.capex.dom.payment.approval.PaymentBatchApprovalStateTransitionType;
import org.estatio.module.task.dom.triggers.DomainObject_triggerAbstract;

abstract class PaymentBatch_triggerAbstract
        extends DomainObject_triggerAbstract<
        PaymentBatch,
        PaymentBatchApprovalStateTransition,
        PaymentBatchApprovalStateTransitionType,
        PaymentBatchApprovalState> {

    public static abstract class ActionDomainEvent<MIXIN> extends DomainObject_triggerAbstract.ActionDomainEvent<MIXIN> {
        @Override
        public Class<?> getStateTransitionClass() {
            return PaymentBatchApprovalStateTransition.class;
        }
    }

    PaymentBatch_triggerAbstract(
            final PaymentBatch paymentBatch,
            final PaymentBatchApprovalStateTransitionType requiredTransitionType) {
        super(paymentBatch, PaymentBatchApprovalStateTransition.class, requiredTransitionType.getFromStates(), requiredTransitionType);
    }


}

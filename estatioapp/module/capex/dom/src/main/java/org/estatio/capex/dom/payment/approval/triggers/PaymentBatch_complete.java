package org.estatio.capex.dom.payment.approval.triggers;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.payment.PaymentBatch;
import org.estatio.capex.dom.payment.approval.PaymentBatchApprovalStateTransitionType;

@Mixin(method="act")
public class PaymentBatch_complete extends PaymentBatch_triggerAbstract {

    private final PaymentBatch paymentBatch;

    public PaymentBatch_complete(PaymentBatch paymentBatch) {
        super(paymentBatch, PaymentBatchApprovalStateTransitionType.COMPLETE);
        this.paymentBatch = paymentBatch;
    }

}

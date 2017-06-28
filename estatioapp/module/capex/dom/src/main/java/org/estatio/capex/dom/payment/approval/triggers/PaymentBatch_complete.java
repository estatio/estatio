package org.estatio.capex.dom.payment.approval.triggers;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.payment.PaymentBatch;
import org.estatio.capex.dom.payment.approval.PaymentBatchApprovalStateTransitionType;

@Mixin(method="act")
public class PaymentBatch_complete extends PaymentBatch_triggerAbstract {

    private final PaymentBatch paymentBatch;

    public PaymentBatch_complete(PaymentBatch paymentBatch) {
        super(paymentBatch, PaymentBatchApprovalStateTransitionType.COMPLETE);
        this.paymentBatch = paymentBatch;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(cssClassFa = "fa-flag-checkered")
    @Override public PaymentBatch act(@Nullable final String comment) {
        return super.act(comment);
    }

    @Override public boolean hideAct() {
        return super.hideAct();
    }

    @Override public String disableAct() {
        return super.disableAct();
    }
}

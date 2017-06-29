package org.estatio.capex.dom.payment.approval.triggers;

import javax.annotation.Nullable;

import org.joda.time.DateTime;

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
    public PaymentBatch act(
            DateTime requestedExecutionDate,
            @Nullable final String comment) {
        paymentBatch.setRequestedExecutionDate(requestedExecutionDate);
        trigger(comment);
        return getDomainObject();
    }

    public boolean hideAct() {
        return cannotTransition();
    }

    public String disableAct() {
        return reasonGuardNotSatisified();
    }

}

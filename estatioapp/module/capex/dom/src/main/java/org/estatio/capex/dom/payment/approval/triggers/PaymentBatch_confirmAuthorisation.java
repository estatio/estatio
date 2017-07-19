package org.estatio.capex.dom.payment.approval.triggers;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.payment.PaymentBatch;
import org.estatio.capex.dom.payment.approval.PaymentBatchApprovalStateTransitionType;

@Mixin(method="act")
public class PaymentBatch_confirmAuthorisation extends PaymentBatch_triggerAbstract {

    private final PaymentBatch paymentBatch;

    public PaymentBatch_confirmAuthorisation(PaymentBatch paymentBatch) {
        super(paymentBatch, PaymentBatchApprovalStateTransitionType.CONFIRM_AUTHORISATION);
        this.paymentBatch = paymentBatch;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(cssClassFa = "fa-check")
    public PaymentBatch act(
            @Nullable final String comment) {
        trigger(comment, null);
        return getDomainObject();
    }

    public boolean hideAct() {
        return cannotTransition();
    }


}

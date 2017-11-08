package org.estatio.capex.dom.payment.approval.triggers;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.capex.dom.payment.PaymentBatch;
import org.estatio.capex.dom.payment.approval.PaymentBatchApprovalStateTransitionType;

/**
 * This mixin cannot (easily) be inlined because it inherits functionality from its superclass, and in any case
 * this follows a common pattern applicable for all domain objects that have an associated state transition machine.
 */
@Mixin(method="act")
public class PaymentBatch_discard extends PaymentBatch_triggerAbstract {

    private final PaymentBatch paymentBatch;

    public PaymentBatch_discard(PaymentBatch paymentBatch) {
        super(paymentBatch, PaymentBatchApprovalStateTransitionType.DISCARD);
        this.paymentBatch = paymentBatch;
    }

    public static class ActionDomainEvent extends PaymentBatch_triggerAbstract.ActionDomainEvent<PaymentBatch_discard> {}

    @Action(
            domainEvent = ActionDomainEvent.class,
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(cssClassFa = "trash-o")
    public PaymentBatch act(
            @Nullable final String comment) {
        trigger(comment, null);

        paymentBatch.clearLines();

        return paymentBatch;
    }

    public boolean hideAct() {
        return cannotTransition();
    }


}

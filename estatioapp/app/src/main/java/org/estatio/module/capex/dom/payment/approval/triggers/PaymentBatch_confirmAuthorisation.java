package org.estatio.module.capex.dom.payment.approval.triggers;

import javax.annotation.Nullable;
import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType;
import org.estatio.module.capex.dom.payment.PaymentBatch;
import org.estatio.module.capex.dom.payment.approval.PaymentBatchApprovalStateTransitionType;
import org.estatio.module.capex.dom.state.StateTransitionService;

/**
 * This mixin cannot (easily) be inlined because it inherits functionality from its superclass, and in any case
 * this follows a common pattern applicable for all domain objects that have an associated state transition machine.
 */
@Mixin(method="act")
public class PaymentBatch_confirmAuthorisation extends PaymentBatch_triggerAbstract {

    private final PaymentBatch paymentBatch;

    public PaymentBatch_confirmAuthorisation(PaymentBatch paymentBatch) {
        super(paymentBatch, PaymentBatchApprovalStateTransitionType.CONFIRM_AUTHORISATION);
        this.paymentBatch = paymentBatch;
    }

    public static class ActionDomainEvent extends PaymentBatch_triggerAbstract.ActionDomainEvent<PaymentBatch_confirmAuthorisation> {}

    @Action(
            domainEvent = ActionDomainEvent.class,
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(cssClassFa = "fa-check")
    public PaymentBatch act(
            @Nullable final String comment) {
        trigger(comment, null);

        Lists.newArrayList(paymentBatch.getLines()).forEach(x -> triggerPaidOn(x.getInvoice()));

        return paymentBatch;
    }

    private void triggerPaidOn(final IncomingInvoice invoice) {
        stateTransitionService.trigger(invoice, IncomingInvoiceApprovalStateTransitionType.PAY_BY_IBP, null, null);
    }

    public boolean hideAct() {
        return cannotTransition();
    }


    @Inject
    StateTransitionService stateTransitionService;

}

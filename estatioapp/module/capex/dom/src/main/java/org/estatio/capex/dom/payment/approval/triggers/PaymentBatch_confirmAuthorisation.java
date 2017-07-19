package org.estatio.capex.dom.payment.approval.triggers;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.assertj.core.util.Lists;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType;
import org.estatio.capex.dom.payment.PaymentBatch;
import org.estatio.capex.dom.payment.approval.PaymentBatchApprovalStateTransitionType;
import org.estatio.capex.dom.state.StateTransitionService;

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

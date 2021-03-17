package org.estatio.module.capex.dom.payment.approval.triggers;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.joda.time.DateTime;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;

import org.estatio.module.capex.dom.payment.PaymentBatch;
import org.estatio.module.capex.dom.payment.approval.PaymentBatchApprovalStateTransitionType;

/**
 * This mixin cannot (easily) be inlined because it inherits functionality from its superclass, and in any case
 * this follows a common pattern applicable for all domain objects that have an associated state transition machine.
 */
@Mixin(method="act")
public class PaymentBatch_complete extends PaymentBatch_triggerAbstract {

    private final PaymentBatch paymentBatch;

    public PaymentBatch_complete(PaymentBatch paymentBatch) {
        super(paymentBatch, PaymentBatchApprovalStateTransitionType.COMPLETE);
        this.paymentBatch = paymentBatch;
    }

    public static class ActionDomainEvent extends PaymentBatch_triggerAbstract.ActionDomainEvent<PaymentBatch_complete> {}

    @Action(
            domainEvent = ActionDomainEvent.class,
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(cssClassFa = "fa-flag-checkered")
    public PaymentBatch act(
            final DateTime requestedExecutionDate,
            @Nullable final String comment) {
        paymentBatch.setRequestedExecutionDate(requestedExecutionDate);

        paymentBatch.setTotalNetAmountOnComplete(paymentBatch.getTotalNetAmount());
        paymentBatch.setTotalVatAmountOnComplete(paymentBatch.getTotalVatAmount());
        paymentBatch.setTotalGrossAmountOnComplete(paymentBatch.getTotalGrossAmount());
        paymentBatch.setNumTransfersOnComplete(paymentBatch.getNumTransfers());
        paymentBatch.setNumInvoicesOnComplete(paymentBatch.getNumInvoices());

        trigger(comment, null);
        return getDomainObject();
    }

    public DateTime default0Act() {
        final DateTime now = clockService.nowAsDateTime();
        return now.plusHours(2).minusMinutes(now.getMinuteOfHour()).minusSeconds(now.getSecondOfMinute()).minusMillis(now.getMillisOfSecond());
    }

    public String validate0Act(DateTime proposed) {
        if(proposed == null) {
            return null;
        }
        final DateTime now = clockService.nowAsDateTime();
        return proposed.isBefore(now) ? "Requested execution date cannot be in the past" : null;
    }

    public boolean hideAct() {
        return cannotTransition();
    }

    public String disableAct() {
        if(paymentBatch.getLines().isEmpty()) {
            return "No payment lines";
        }
        return reasonGuardNotSatisified();
    }

    @Inject
    ClockService clockService;

}

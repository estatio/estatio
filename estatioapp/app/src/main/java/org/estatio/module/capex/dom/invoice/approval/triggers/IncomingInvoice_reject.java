package org.estatio.module.capex.dom.invoice.approval.triggers;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.title.TitleService;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType;
import org.estatio.module.capex.dom.payment.PaymentBatch;
import org.estatio.module.capex.dom.payment.PaymentLine;
import org.estatio.module.capex.dom.payment.PaymentLineRepository;
import org.estatio.module.capex.dom.payment.approval.PaymentBatchApprovalState;
import org.estatio.module.party.dom.Person;

/**
 * This mixin cannot (easily) be inlined because it inherits functionality from its superclass, and in any case
 * this follows a common pattern applicable for all domain objects that have an associated state transition machine.
 */
@Mixin(method = "act")
public class IncomingInvoice_reject extends IncomingInvoice_triggerAbstract {

    private final IncomingInvoice incomingInvoice;

    public IncomingInvoice_reject(IncomingInvoice incomingInvoice) {
        super(incomingInvoice, IncomingInvoiceApprovalStateTransitionType.REJECT);
        this.incomingInvoice = incomingInvoice;
    }

    public static class ActionDomainEvent
            extends IncomingInvoice_triggerAbstract.ActionDomainEvent<IncomingInvoice_reject> {
    }

    @Action(
            domainEvent = IncomingInvoice_next.ActionDomainEvent.class,
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(cssClassFa = "fa-thumbs-o-down", cssClass = "btn-warning")
    public Object act(
            final String role,
            @Nullable final Person personToAssignNextTo,
            final String reason) {

        final List<PaymentLine> paymentLines =
                paymentLineRepository.findByInvoice(incomingInvoice);
        // because of the disableXxx guard, this should return either 0 or 1 lines.
        for (PaymentLine paymentLine : paymentLines) {
            final PaymentBatch paymentBatch = paymentLine.getBatch();
            paymentBatch.removeLineFor(incomingInvoice);
        }

        final IncomingInvoiceApprovalStateTransition transition = trigger(personToAssignNextTo, reason, reason);
        if (transition.getTask() != null)
            transition.getTask().setToHighestPriority();

        return objectToReturn();
    }

    protected Object objectToReturn() {
        return getDomainObject();
    }

    public boolean hideAct() {
        return cannotTransition();
    }

    public String disableAct() {

        final List<PaymentLine> paymentLines =
                paymentLineRepository.findByInvoice(incomingInvoice);
        for (PaymentLine paymentLine : paymentLines) {
            final PaymentBatch paymentBatch = paymentLine.getBatch();
            final PaymentBatchApprovalState state = paymentBatch.getApprovalState();
            if (state != PaymentBatchApprovalState.NEW && state != PaymentBatchApprovalState.DISCARDED) {
                return String.format("Invoice is in batch %s", titleService.titleOf(paymentBatch));
            }
        }

        return reasonGuardNotSatisified();
    }

    public String default0Act() {
        return enumPartyRoleTypeName();
    }

    public Person default1Act() {
        return defaultPersonToAssignNextTo();
    }

    public List<Person> choices1Act() {
        return choicesPersonToAssignNextTo();
    }

    @Inject
    PaymentLineRepository paymentLineRepository;

    @Inject
    TitleService titleService;

}

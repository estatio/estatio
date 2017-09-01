package org.estatio.capex.dom.invoice.approval;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Ordering;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.invoice.IncomingInvoice;


@Mixin(method="prop")
public class IncomingInvoice_previousComments {

    private final IncomingInvoice incomingInvoice;
    public IncomingInvoice_previousComments(final IncomingInvoice incomingInvoice) {
        this.incomingInvoice = incomingInvoice;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed= Contributed.AS_ASSOCIATION)
    @PropertyLayout(multiLine = 5)
    public String prop() {
        final List<IncomingInvoiceApprovalStateTransition> stateTransitions =
                transitionRepository.findByDomainObject(incomingInvoice);

        final StringBuilder buf = new StringBuilder();
        stateTransitions.stream()
                .filter(IncomingInvoiceApprovalStateTransition::isCompleted)
                .sorted(Ordering.natural().onResultOf(IncomingInvoiceApprovalStateTransition::getCompletedOn).reversed())
                .forEach(transition -> appendSummaryTo(transition, buf));

        return buf.toString();
    }

    private void appendSummaryTo(
            final IncomingInvoiceApprovalStateTransition transition,
            final StringBuilder appendTo) {

        if(!transition.isCompleted()) {
            return;
        }

        final StringBuilder buf = summaryOf(transition);
        if(buf.length() > 0) {
            appendTo.append(buf).append(System.lineSeparator());
        }

    }

    private StringBuilder summaryOf(final IncomingInvoiceApprovalStateTransition transition) {
        final StringBuilder buf = new StringBuilder();

        final String comment = transition.getComment();
        final IncomingInvoiceApprovalStateTransitionType transitionType = transition.getTransitionType();
        if(comment != null) {
            buf.append(transitionType).append(": ").append(comment);
            final String completedBy = transition.getCompletedBy();
            if(completedBy != null) {
                buf.append(" (").append(completedBy).append(")");
            }
        }
        return buf;
    }

    @Inject
    IncomingInvoiceApprovalStateTransition.Repository transitionRepository;

}

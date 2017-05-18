package org.estatio.capex.dom.invoice.payment.approval.tasks;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.invoice.payment.Payment;
import org.estatio.capex.dom.invoice.payment.approval.PaymentApprovalStateTransition;
import org.estatio.capex.dom.task.Task;

@Mixin
public class Payment_tasks {

    private final Payment payment;

    public Payment_tasks(final Payment payment) {
        this.payment = payment;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<Task> tasks() {
        final List<PaymentApprovalStateTransition> transitions = repository.findByDomainObject(payment);
        return Task.from(transitions);
    }

    @Inject
    PaymentApprovalStateTransition.Repository repository;

}

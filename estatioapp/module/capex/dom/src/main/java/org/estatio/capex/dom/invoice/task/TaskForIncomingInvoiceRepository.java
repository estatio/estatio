package org.estatio.capex.dom.invoice.task;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.state.IncomingInvoiceState;
import org.estatio.capex.dom.invoice.state.IncomingInvoiceTransitionType;
import org.estatio.capex.dom.task.Task;
import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.roles.EstatioRole;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = StateTransitionForIncomingInvoice.class
)
public class TaskForIncomingInvoiceRepository extends UdoDomainRepositoryAndFactory<StateTransitionForIncomingInvoice> {

    public TaskForIncomingInvoiceRepository() {
        super(TaskForIncomingInvoiceRepository.class, StateTransitionForIncomingInvoice.class);
    }

    @Programmatic
    public java.util.List<StateTransitionForIncomingInvoice> listAll() {
        return allInstances(StateTransitionForIncomingInvoice.class);
    }

    @Programmatic
    public List<StateTransitionForIncomingInvoice> findByInvoice(
            final IncomingInvoice invoice
    ) {
        return allMatches(
                new org.apache.isis.applib.query.QueryDefault<>(
                        StateTransitionForIncomingInvoice.class,
                        "findByInvoice",
                        "invoice", invoice));
    }

    @Programmatic
    public List<? extends Task<?, IncomingInvoice, IncomingInvoiceTransitionType, IncomingInvoiceState>> findByInvoiceAndTransition(
            final IncomingInvoice invoice,
            final IncomingInvoiceTransitionType transition) {
        return allMatches(
                new org.apache.isis.applib.query.QueryDefault<>(
                        StateTransitionForIncomingInvoice.class,
                        "findByInvoiceAndTransition",
                        "invoice", invoice,
                        "transition", transition));
    }

    @Programmatic
    public List<StateTransitionForIncomingInvoice> findByInvoiceAndRole(
            final IncomingInvoice invoice,
            final EstatioRole assignedTo
    ) {
        return allMatches(
                new org.apache.isis.applib.query.QueryDefault<>(
                        StateTransitionForIncomingInvoice.class,
                        "findByInvoiceAndAssignedTo",
                        "invoice", invoice, "assignedTo", assignedTo));
    }

    @Programmatic
    public List<StateTransitionForIncomingInvoice> findByInvoiceAndRoleAndNotCompleted(
            final IncomingInvoice invoice,
            final EstatioRole assignedTo
    ) {
        return allMatches(
                new org.apache.isis.applib.query.QueryDefault<>(
                        StateTransitionForIncomingInvoice.class,
                        "findByInvoice",
                        "invoice", invoice, "assignedTo", assignedTo));
    }


    @Programmatic
    public StateTransitionForIncomingInvoice create(
            final IncomingInvoice invoice,
            final IncomingInvoiceTransitionType transition,
            final EstatioRole assignTo,
            final String description) {
        final StateTransitionForIncomingInvoice task = repositoryService.instantiate(StateTransitionForIncomingInvoice.class);
        task.setInvoice(invoice);
        task.setAssignedTo(assignTo);
        task.setTransition(transition);
        task.setDescription(description);
        task.setCreatedOn(clockService.nowAsLocalDateTime());
        repositoryService.persistAndFlush(task);
        return task;
    }

    @Inject
    RepositoryService repositoryService;
    @Inject
    ClockService clockService;

}

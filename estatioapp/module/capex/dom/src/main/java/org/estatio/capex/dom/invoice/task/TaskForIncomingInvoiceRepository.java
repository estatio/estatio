package org.estatio.capex.dom.invoice.task;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.clock.ClockService;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.rule.IncomingInvoiceTransition;
import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.roles.EstatioRole;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = TaskForIncomingInvoice.class
)
public class TaskForIncomingInvoiceRepository extends UdoDomainRepositoryAndFactory<TaskForIncomingInvoice> {

    public TaskForIncomingInvoiceRepository() {
        super(TaskForIncomingInvoiceRepository.class, TaskForIncomingInvoice.class);
    }

    @Programmatic
    public java.util.List<TaskForIncomingInvoice> listAll() {
        return allInstances(TaskForIncomingInvoice.class);
    }

    @Programmatic
    public List<TaskForIncomingInvoice> findByInvoice(
            final IncomingInvoice invoice
    ) {
        return allMatches(
                new org.apache.isis.applib.query.QueryDefault<>(
                        TaskForIncomingInvoice.class,
                        "findByInvoice",
                        "invoice", invoice));
    }

    @Programmatic
    public List<TaskForIncomingInvoice> findByInvoiceAndRole(
            final IncomingInvoice invoice,
            final EstatioRole assignedTo
    ) {
        return allMatches(
                new org.apache.isis.applib.query.QueryDefault<>(
                        TaskForIncomingInvoice.class,
                        "findByInvoiceAndAssignedTo",
                        "invoice", invoice, "assignedTo", assignedTo));
    }

    @Programmatic
    public List<TaskForIncomingInvoice> findByInvoiceAndRoleAndNotCompleted(
            final IncomingInvoice invoice,
            final EstatioRole assignedTo
    ) {
        return allMatches(
                new org.apache.isis.applib.query.QueryDefault<>(
                        TaskForIncomingInvoice.class,
                        "findByInvoice",
                        "invoice", invoice, "assignedTo", assignedTo));
    }


    @Programmatic
    public TaskForIncomingInvoice create(
            final IncomingInvoice invoice,
            final IncomingInvoiceTransition transition,
            final EstatioRole assignTo,
            final String description) {
        final TaskForIncomingInvoice task = newTransientInstance(TaskForIncomingInvoice.class);
        task.setInvoice(invoice);
        task.setAssignedTo(assignTo);
        task.setTransition(transition);
        task.setDescription(description);
        task.setCreatedOn(clockService.nowAsLocalDateTime());
        persistIfNotAlready(task);
        return task;
    }

    @Inject
    ClockService clockService;

}

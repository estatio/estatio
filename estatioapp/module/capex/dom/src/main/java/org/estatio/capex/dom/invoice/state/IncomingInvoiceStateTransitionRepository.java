package org.estatio.capex.dom.invoice.state;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDateTime;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.metamodel.MetaModelService3;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.task.Task;
import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.roles.EstatioRole;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = IncomingInvoiceStateTransition.class
)
public class IncomingInvoiceStateTransitionRepository extends UdoDomainRepositoryAndFactory<IncomingInvoiceStateTransition> {

    public IncomingInvoiceStateTransitionRepository() {
        super(IncomingInvoiceStateTransitionRepository.class, IncomingInvoiceStateTransition.class);
    }

    @Programmatic
    public List<IncomingInvoiceStateTransition> findByInvoice(
            final IncomingInvoice incomingInvoice) {
        return allMatches(
                new org.apache.isis.applib.query.QueryDefault<>(
                        IncomingInvoiceStateTransition.class,
                        "findByInvoice",
                        "invoice", incomingInvoice));
    }

    @Programmatic
    public IncomingInvoiceStateTransition findByInvoiceAndIncomplete(final IncomingInvoice incomingInvoice) {
        // can't be uniqueMatch, because for the very first call to StateTransitionService#apply, called on an
        // INSTANTIATED event, there won't yet be any StateTransitions for this invoice
        return firstMatch(
                new org.apache.isis.applib.query.QueryDefault<>(
                        IncomingInvoiceStateTransition.class,
                        "findByInvoiceAndIncomplete",
                        "invoice", incomingInvoice));
    }


    @Programmatic
    public IncomingInvoiceStateTransition findByTask(final Task task) {
        return firstMatch(
                new org.apache.isis.applib.query.QueryDefault<>(
                        IncomingInvoiceStateTransition.class,
                        "findByTask",
                        "task", task));
    }

    /**
     * Creates the transition with corresponding {@link Task}.
     */
    @Programmatic
    public IncomingInvoiceStateTransition create(
            final IncomingInvoice invoice,
            final IncomingInvoiceStateTransitionType transitionType,
            final IncomingInvoiceState fromState,
            final EstatioRole taskAssignToIfAny,
            final String taskDescription) {

        final String transitionObjectType = metaModelService3.toObjectType(IncomingInvoiceStateTransition.class);
        final LocalDateTime createdOn = clockService.nowAsLocalDateTime();

        final Task task;
        if(taskAssignToIfAny != null) {
            task = new Task(taskAssignToIfAny, taskDescription, transitionObjectType, createdOn);
            repositoryService.persist(task);
        } else {
            task = null;
        }

        final IncomingInvoiceStateTransition stateTransition =
                new IncomingInvoiceStateTransition(transitionType, invoice, fromState, task);
        repositoryService.persistAndFlush(stateTransition);

        return stateTransition;
    }

    @Inject
    MetaModelService3 metaModelService3;

    @Inject
    RepositoryService repositoryService;

    @Inject
    ClockService clockService;

}

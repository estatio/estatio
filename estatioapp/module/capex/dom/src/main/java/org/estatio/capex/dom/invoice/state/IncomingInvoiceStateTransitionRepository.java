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

    // REVIEW: what if multiple task/transitions are found?  This code
    @Programmatic
    public IncomingInvoiceStateTransition findByInvoiceAndTransitionTypeAndTaskCompleted(
            final IncomingInvoice invoice,
            final IncomingInvoiceStateTransitionType transitionType,
            final boolean taskCompleted) {
        return uniqueMatch(
                new org.apache.isis.applib.query.QueryDefault<>(
                        IncomingInvoiceStateTransition.class,
                        "findByInvoiceAndTransitionTypeAndTaskCompleted",
                        "invoice", invoice,
                        "transitionType", transitionType,
                        "taskCompleted", taskCompleted));
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

    /**
     * Creates the transition with corresponding {@link Task}.
     */
    @Programmatic
    public IncomingInvoiceStateTransition create(
            final IncomingInvoice invoice,
            final IncomingInvoiceStateTransitionType transitionType,
            final EstatioRole assignTo,
            final String description) {

        final String transitionObjectType = metaModelService3.toObjectType(IncomingInvoiceStateTransition.class);
        final LocalDateTime createdOn = clockService.nowAsLocalDateTime();

        final Task task = new Task(assignTo, description, transitionObjectType, createdOn);
        repositoryService.persist(task);

        final IncomingInvoiceStateTransition stateTransition =
                new IncomingInvoiceStateTransition(transitionType, invoice, task);
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

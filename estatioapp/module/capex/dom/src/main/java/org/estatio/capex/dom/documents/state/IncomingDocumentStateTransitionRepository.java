package org.estatio.capex.dom.documents.state;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDateTime;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.metamodel.MetaModelService3;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.invoice.state.IncomingInvoiceStateTransition;
import org.estatio.capex.dom.task.Task;
import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.roles.EstatioRole;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = IncomingInvoiceStateTransition.class
)
public class IncomingDocumentStateTransitionRepository extends UdoDomainRepositoryAndFactory<IncomingDocumentStateTransition> {

    public IncomingDocumentStateTransitionRepository() {
        super(IncomingDocumentStateTransitionRepository.class, IncomingDocumentStateTransition.class);
    }

    @Programmatic
    public List<IncomingDocumentStateTransition> findByDocumentAndTransitionTypeAndTaskCompleted(
            final Document document,
            final IncomingDocumentStateTransitionType transitionType,
            final boolean taskCompleted) {
        return allMatches(
                new org.apache.isis.applib.query.QueryDefault<>(
                        IncomingDocumentStateTransition.class,
                        "findByDocumentAndTransitionTypeAndTaskCompleted",
                        "document", document,
                        "transitionType", transitionType,
                        "taskCompleted", taskCompleted));
    }

    @Programmatic
    public List<IncomingDocumentStateTransition> findByDocument(
            final Document document) {
        return allMatches(
                new org.apache.isis.applib.query.QueryDefault<>(
                        IncomingDocumentStateTransition.class,
                        "findByDocument",
                        "document", document));
    }

    @Programmatic
    public IncomingDocumentStateTransition findByTask(final Task task) {
        return firstMatch(
                new org.apache.isis.applib.query.QueryDefault<>(
                        IncomingDocumentStateTransition.class,
                        "findByTask",
                        "task", task));
    }

    /**
     * Creates the transition with corresponding {@link Task}.
     */
    @Programmatic
    public IncomingDocumentStateTransition create(
            final Document document,
            final IncomingDocumentStateTransitionType transitionType,
            final EstatioRole assignTo,
            final String description) {

        final String transitionObjectType = metaModelService3.toObjectType(IncomingDocumentStateTransition.class);
        final LocalDateTime createdOn = clockService.nowAsLocalDateTime();

        final Task task = new Task(assignTo, description, transitionObjectType, createdOn);
        repositoryService.persist(task);

        final IncomingDocumentStateTransition stateTransition =
                new IncomingDocumentStateTransition(transitionType, document, task);
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

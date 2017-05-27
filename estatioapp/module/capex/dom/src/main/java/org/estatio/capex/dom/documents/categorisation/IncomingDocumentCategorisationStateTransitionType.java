package org.estatio.capex.dom.documents.categorisation;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.util.Enums;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.state.StateTransitionEvent;
import org.estatio.capex.dom.state.StateTransitionRepository;
import org.estatio.capex.dom.state.StateTransitionServiceSupportAbstract;
import org.estatio.capex.dom.state.StateTransitionStrategy;
import org.estatio.capex.dom.state.StateTransitionType;
import org.estatio.capex.dom.state.TaskAssignmentStrategy;
import org.estatio.dom.roles.EstatioRole;

import lombok.Getter;

@Getter
public enum IncomingDocumentCategorisationStateTransitionType
        implements StateTransitionType<
                        Document,
                        IncomingDocumentCategorisationStateTransition,
                        IncomingDocumentCategorisationStateTransitionType,
                        IncomingDocumentCategorisationState> {

    // a "pseudo" transition type; won't ever see this persisted as a state transition
    INSTANTIATE(
            (IncomingDocumentCategorisationState)null,
            IncomingDocumentCategorisationState.NEW,
            TaskAssignmentStrategy.Util.none(), StateTransitionStrategy.Util.next()
    ),
    CATEGORISE_DOCUMENT_TYPE_AND_ASSOCIATE_WITH_PROPERTY(
            IncomingDocumentCategorisationState.NEW,
            IncomingDocumentCategorisationState.CATEGORISED_AND_ASSOCIATED_WITH_PROPERTY,
            TaskAssignmentStrategy.Util.to(EstatioRole.MAIL_ROOM), StateTransitionStrategy.Util.next()
    ),
    ASSOCIATE_WITH_DOMAIN_ENTITY(
            IncomingDocumentCategorisationState.CATEGORISED_AND_ASSOCIATED_WITH_PROPERTY,
            IncomingDocumentCategorisationState.ASSOCIATED_WITH_DOMAIN_ENTITY,
            TaskAssignmentStrategy.Util.to(EstatioRole.USER), StateTransitionStrategy.Util.none()
    ),
    RESET(
            IncomingDocumentCategorisationState.CATEGORISED_AND_ASSOCIATED_WITH_PROPERTY,
            IncomingDocumentCategorisationState.NEW,
            TaskAssignmentStrategy.Util.none(), StateTransitionStrategy.Util.next()
    )
    ;

    private final List<IncomingDocumentCategorisationState> fromStates;
    private final IncomingDocumentCategorisationState toState;
    private final StateTransitionStrategy stateTransitionStrategy;
    private final TaskAssignmentStrategy taskAssignmentStrategy;

    IncomingDocumentCategorisationStateTransitionType(
            final List<IncomingDocumentCategorisationState> fromState,
            final IncomingDocumentCategorisationState toState,
            final TaskAssignmentStrategy taskAssignmentStrategy,
            final StateTransitionStrategy stateTransitionStrategy) {
        this.fromStates = fromState;
        this.toState = toState;
        this.stateTransitionStrategy = stateTransitionStrategy;
        this.taskAssignmentStrategy = taskAssignmentStrategy;
    }

    IncomingDocumentCategorisationStateTransitionType(
            final IncomingDocumentCategorisationState fromState,
            final IncomingDocumentCategorisationState toState,
            final TaskAssignmentStrategy taskAssignmentStrategy,
            final StateTransitionStrategy stateTransitionStrategy) {
        this(fromState != null ? Collections.singletonList(fromState): null, toState, taskAssignmentStrategy,
                stateTransitionStrategy
        );
    }

    @Override
    public StateTransitionStrategy getTransitionStrategy() {
        return stateTransitionStrategy;
    }

    public static class TransitionEvent
            extends StateTransitionEvent<
                        Document,
                        IncomingDocumentCategorisationStateTransition,
                        IncomingDocumentCategorisationStateTransitionType,
                        IncomingDocumentCategorisationState> {
        public TransitionEvent(
                final Document domainObject,
                final IncomingDocumentCategorisationStateTransition stateTransitionIfAny,
                final IncomingDocumentCategorisationStateTransitionType transitionType) {
            super(domainObject, stateTransitionIfAny, transitionType);
        }
    }

    @Override
    public TransitionEvent newStateTransitionEvent(
            final Document domainObject,
            final IncomingDocumentCategorisationStateTransition transitionIfAny) {
        return new TransitionEvent(domainObject, transitionIfAny, this);
    }

    @Override
    public boolean canApply(
            final Document domainObject,
            final ServiceRegistry2 serviceRegistry2) {
        // can never apply the initial pseudo approval
        return getFromStates() != null;
    }

    @Override
    public void applyTo(
            final Document domainObject,
            final ServiceRegistry2 serviceRegistry2) {
        // nothing to do....
    }


    @Override
    public IncomingDocumentCategorisationStateTransition createTransition(
            final Document domainObject,
            final IncomingDocumentCategorisationState fromState,
            final EstatioRole assignToIfAny,
            final ServiceRegistry2 serviceRegistry2) {

        final IncomingDocumentCategorisationStateTransition.Repository repository =
                serviceRegistry2.lookupService(IncomingDocumentCategorisationStateTransition.Repository.class);

        final String taskDescription = Enums.getFriendlyNameOf(this);
        return repository.create(domainObject, this, fromState, assignToIfAny, taskDescription);
    }

    @DomainService(nature = NatureOfService.DOMAIN)
    public static class SupportService extends StateTransitionServiceSupportAbstract<
                        Document,
            IncomingDocumentCategorisationStateTransition,
            IncomingDocumentCategorisationStateTransitionType,
            IncomingDocumentCategorisationState> {

        public SupportService() {
            super(IncomingDocumentCategorisationStateTransitionType.class, IncomingDocumentCategorisationStateTransition.class
            );
        }

        @Override
        protected StateTransitionRepository<
                Document,
                IncomingDocumentCategorisationStateTransition,
                IncomingDocumentCategorisationStateTransitionType,
                IncomingDocumentCategorisationState
                > getRepository() {
            return repository;
        }

        @Inject
        IncomingDocumentCategorisationStateTransition.Repository repository;

    }
}

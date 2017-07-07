package org.estatio.capex.dom.documents.categorisation;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.state.AdvancePolicy;
import org.estatio.capex.dom.state.NextTransitionSearchStrategy;
import org.estatio.capex.dom.state.StateTransitionEvent;
import org.estatio.capex.dom.state.StateTransitionRepository;
import org.estatio.capex.dom.state.StateTransitionServiceSupportAbstract;
import org.estatio.capex.dom.state.StateTransitionType;
import org.estatio.capex.dom.state.TaskAssignmentStrategy;
import org.estatio.dom.party.PartyRoleTypeEnum;
import org.estatio.dom.party.Person;
import org.estatio.dom.party.role.IPartyRoleType;

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
            NextTransitionSearchStrategy.firstMatching(),
            TaskAssignmentStrategy.none(),
            AdvancePolicy.MANUAL),
    CATEGORISE(
            IncomingDocumentCategorisationState.NEW,
            IncomingDocumentCategorisationState.CATEGORISED,
            NextTransitionSearchStrategy.none(),
            TaskAssignmentStrategy.to(PartyRoleTypeEnum.OFFICE_ADMINISTRATOR),
            AdvancePolicy.MANUAL),
    DISCARD_ASSOCIATED(
            IncomingDocumentCategorisationState.CATEGORISED,
            IncomingDocumentCategorisationState.DISCARDED,
            NextTransitionSearchStrategy.none(),
            TaskAssignmentStrategy.none(),
            AdvancePolicy.MANUAL),
    DISCARD(
            IncomingDocumentCategorisationState.NEW,
            IncomingDocumentCategorisationState.DISCARDED,
            NextTransitionSearchStrategy.none(),
            TaskAssignmentStrategy.none(),
            AdvancePolicy.MANUAL);

    private final List<IncomingDocumentCategorisationState> fromStates;
    private final IncomingDocumentCategorisationState toState;
    private final NextTransitionSearchStrategy nextTransitionSearchStrategy;
    private final TaskAssignmentStrategy taskAssignmentStrategy;
    private final AdvancePolicy advancePolicy;

    IncomingDocumentCategorisationStateTransitionType(
            final List<IncomingDocumentCategorisationState> fromState,
            final IncomingDocumentCategorisationState toState,
            final NextTransitionSearchStrategy nextTransitionSearchStrategy,
            final TaskAssignmentStrategy taskAssignmentStrategy,
            final AdvancePolicy advancePolicy) {
        this.fromStates = fromState;
        this.toState = toState;
        this.nextTransitionSearchStrategy = nextTransitionSearchStrategy;
        this.taskAssignmentStrategy = taskAssignmentStrategy;
        this.advancePolicy = advancePolicy;
    }

    IncomingDocumentCategorisationStateTransitionType(
            final IncomingDocumentCategorisationState fromState,
            final IncomingDocumentCategorisationState toState,
            final NextTransitionSearchStrategy nextTransitionSearchStrategy,
            final TaskAssignmentStrategy taskAssignmentStrategy,
            final AdvancePolicy advancePolicy) {
        this(fromState != null ? Collections.singletonList(fromState): null, toState, nextTransitionSearchStrategy,
                taskAssignmentStrategy,
                advancePolicy);
    }

    @Override
    public NextTransitionSearchStrategy getNextTransitionSearchStrategy() {
        return nextTransitionSearchStrategy;
    }

    @Override
    public AdvancePolicy advancePolicyFor(
            final Document domainObject, final ServiceRegistry2 serviceRegistry2) {
        return advancePolicy;
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
    public IncomingDocumentCategorisationStateTransition createTransition(
            final Document domainObject,
            final IncomingDocumentCategorisationState fromState,
            final IPartyRoleType assignToIfAny,
            final Person personToAssignToIfAny,
            final String taskDescriptionIfAny, final ServiceRegistry2 serviceRegistry2) {

        final IncomingDocumentCategorisationStateTransition.Repository repository =
                serviceRegistry2.lookupService(IncomingDocumentCategorisationStateTransition.Repository.class);

        final String taskDescription = Util.taskDescriptionUsing(taskDescriptionIfAny, this);

        return repository.create(domainObject, this, fromState, assignToIfAny, personToAssignToIfAny, taskDescription);
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

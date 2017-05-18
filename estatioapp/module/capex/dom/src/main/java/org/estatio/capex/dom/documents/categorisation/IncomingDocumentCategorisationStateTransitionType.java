package org.estatio.capex.dom.documents.categorisation;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.util.Enums;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.state.StateTransitionEvent;
import org.estatio.capex.dom.state.StateTransitionRepository;
import org.estatio.capex.dom.state.StateTransitionServiceSupportAbstract;
import org.estatio.capex.dom.state.StateTransitionType;
import org.estatio.capex.dom.task.Task;
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
    INSTANTIATING(
            (IncomingDocumentCategorisationState)null,
            IncomingDocumentCategorisationState.NEW
    ),
    CATEGORISE_DOCUMENT_TYPE_AND_ASSOCIATE_WITH_PROPERTY(
            IncomingDocumentCategorisationState.NEW,
            IncomingDocumentCategorisationState.CATEGORISED_AND_ASSOCIATED_WITH_PROPERTY),
    ASSOCIATE_WITH_DOMAIN_ENTITY(
            IncomingDocumentCategorisationState.CATEGORISED_AND_ASSOCIATED_WITH_PROPERTY,
            IncomingDocumentCategorisationState.ASSOCIATED_WITH_DOMAIN_ENTITY),;

    private final List<IncomingDocumentCategorisationState> fromStates;
    private final IncomingDocumentCategorisationState toState;

    IncomingDocumentCategorisationStateTransitionType(
            final List<IncomingDocumentCategorisationState> fromState,
            final IncomingDocumentCategorisationState toState) {
        this.fromStates = fromState;
        this.toState = toState;
    }

    IncomingDocumentCategorisationStateTransitionType(
            final IncomingDocumentCategorisationState fromState,
            final IncomingDocumentCategorisationState toState
    ) {
        this(fromState != null ? Collections.singletonList(fromState): null, toState);
    }

    public static class IncomingDocumentCategorisationTransitionEvent
            extends StateTransitionEvent<
                        Document,
                        IncomingDocumentCategorisationStateTransition,
                        IncomingDocumentCategorisationStateTransitionType,
                        IncomingDocumentCategorisationState> {
        public IncomingDocumentCategorisationTransitionEvent(
                final Document domainObject,
                final IncomingDocumentCategorisationStateTransition stateTransitionIfAny,
                final IncomingDocumentCategorisationStateTransitionType transitionType) {
            super(domainObject, stateTransitionIfAny, transitionType);
        }
    }

    @Override
    public IncomingDocumentCategorisationTransitionEvent newStateTransitionEvent(
            final Document domainObject,
            final IncomingDocumentCategorisationStateTransition transitionIfAny) {
        return new IncomingDocumentCategorisationTransitionEvent(domainObject, transitionIfAny, this);
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

    /**
     * No {@link Task} will be created unless this method is overridden.
     *
     * @param serviceRegistry2 -to lookup domain services etc
     */
    @Programmatic
    @Override
    public EstatioRole assignTaskTo(final ServiceRegistry2 serviceRegistry2) {
        return null;
    }

    @Override public IncomingDocumentCategorisationStateTransition createTransition(
            final Document domainObject,
            final IncomingDocumentCategorisationState fromState,
            final ServiceRegistry2 serviceRegistry2) {

        final IncomingDocumentCategorisationStateTransition.Repository repository =
                serviceRegistry2.lookupService(IncomingDocumentCategorisationStateTransition.Repository.class);

        final EstatioRole assignToIfAny = this.assignTaskTo(serviceRegistry2);

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
            super(IncomingDocumentCategorisationStateTransitionType.class, IncomingDocumentCategorisationStateTransition.class,
                    IncomingDocumentCategorisationState.NEW);
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

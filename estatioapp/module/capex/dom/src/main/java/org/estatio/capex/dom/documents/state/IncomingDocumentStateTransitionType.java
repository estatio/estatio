package org.estatio.capex.dom.documents.state;

import java.util.Collections;
import java.util.List;

import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.util.Enums;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.state.StateTransitionType;
import org.estatio.capex.dom.task.Task;
import org.estatio.dom.roles.EstatioRole;

import lombok.Getter;

@Getter
public enum IncomingDocumentStateTransitionType
        implements StateTransitionType<
                        Document,
                        IncomingDocumentStateTransition,
                        IncomingDocumentStateTransitionType,
                        IncomingDocumentState> {

    // a "pseudo" transition type; won't ever see this persisted as a state transition
    INSTANTIATING(
            (IncomingDocumentState)null,
            IncomingDocumentState.NEW
    ),
    CATEGORISE_DOCUMENT_TYPE_AND_ASSOCIATE_WITH_PROPERTY(
            IncomingDocumentState.NEW,
            IncomingDocumentState.CATEGORISED_AND_ASSOCIATED_WITH_PROPERTY),
    ASSOCIATE_WITH_DOMAIN_ENTITY(
            IncomingDocumentState.CATEGORISED_AND_ASSOCIATED_WITH_PROPERTY,
            IncomingDocumentState.ASSOCIATED_WITH_DOMAIN_ENTITY),;

    private final List<IncomingDocumentState> fromStates;
    private final IncomingDocumentState toState;

    IncomingDocumentStateTransitionType(
            final List<IncomingDocumentState> fromState,
            final IncomingDocumentState toState) {
        this.fromStates = fromState;
        this.toState = toState;
    }

    IncomingDocumentStateTransitionType(
            final IncomingDocumentState fromState,
            final IncomingDocumentState toState
    ) {
        this(fromState != null ? Collections.singletonList(fromState): null, toState);
    }

    @Override
    public boolean canApply(
            final Document domainObject,
            final ServiceRegistry2 serviceRegistry2) {
        // can never apply the initial pseudo state
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

    @Override public IncomingDocumentStateTransition createTransition(
            final Document domainObject,
            final IncomingDocumentState fromState,
            final ServiceRegistry2 serviceRegistry2) {

        final IncomingDocumentStateTransitionRepository repository =
                serviceRegistry2.lookupService(IncomingDocumentStateTransitionRepository.class);

        final EstatioRole assignToIfAny = this.assignTaskTo(serviceRegistry2);

        final String taskDescription = Enums.getFriendlyNameOf(this);
        return repository.create(domainObject, this, fromState, assignToIfAny, taskDescription);
    }

}

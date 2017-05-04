package org.estatio.capex.dom.documents.state;

import java.util.Collections;
import java.util.List;

import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.state.StateTransitionType;
import org.estatio.dom.roles.EstatioRole;

import lombok.Getter;

@Getter
public enum IncomingDocumentStateTransitionType implements StateTransitionType<
        Document, IncomingDocumentStateTransition, IncomingDocumentStateTransitionType, IncomingDocumentState> {

    INSTANTIATING(
            (IncomingDocumentState)null,
            IncomingDocumentState.NEW
    ){
        // can never apply the pseudo-state (so not selected as the 'next' state)
        @Override
        public boolean canApply(
                final Document domainObject,
                final ServiceRegistry2 serviceRegistry2) {
            return false;
        }

    },
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



    @Override public boolean canApply(
            final Document domainObject, final ServiceRegistry2 serviceRegistry2) {
        return false;
    }

    @Override public void applyTo(
            final Document domainObject, final ServiceRegistry2 serviceRegistry2) {

    }

    @Override public EstatioRole assignTaskTo(final ServiceRegistry2 serviceRegistry2) {
        return null;
    }

    @Override public IncomingDocumentStateTransition createTransition(
            final Document domainObject, final ServiceRegistry2 serviceRegistry2, final IncomingDocumentState fromState) {
        return null;
    }

}

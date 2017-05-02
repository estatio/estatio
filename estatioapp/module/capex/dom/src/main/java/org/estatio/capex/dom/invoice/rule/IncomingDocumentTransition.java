package org.estatio.capex.dom.invoice.rule;

import lombok.Getter;

@Getter
public enum IncomingDocumentTransition {

    CATEGORISE_DOCUMENT_TYPE_AND_ASSOCIATE_WITH_PROPERTY(
            IncomingDocumentState.NEW,
            IncomingDocumentState.CATEGORISED_AND_ASSOCIATED_WITH_PROPERTY),
    ASSOCIATE_WITH_DOMAIN_ENTITY(
            IncomingDocumentState.CATEGORISED_AND_ASSOCIATED_WITH_PROPERTY,
            IncomingDocumentState.ASSOCIATED_WITH_DOMAIN_ENTITY),;

    private final IncomingDocumentState fromState;
    private final IncomingDocumentState toState;

    IncomingDocumentTransition(
            final IncomingDocumentState fromState,
            final IncomingDocumentState toState) {

        this.fromState = fromState;
        this.toState = toState;
    }
}

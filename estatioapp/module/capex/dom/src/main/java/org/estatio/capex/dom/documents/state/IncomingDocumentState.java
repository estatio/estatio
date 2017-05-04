package org.estatio.capex.dom.documents.state;

import org.estatio.capex.dom.state.State;

public enum IncomingDocumentState implements State<IncomingDocumentState> {
    NEW,
    CATEGORISED_AND_ASSOCIATED_WITH_PROPERTY,
    ASSOCIATED_WITH_DOMAIN_ENTITY
}

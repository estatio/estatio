package org.estatio.capex.dom.documents.categorisation;

import org.estatio.capex.dom.state.State;

public enum IncomingDocumentCategorisationState implements State<IncomingDocumentCategorisationState> {
    NEW,
    CATEGORISED_AND_ASSOCIATED_WITH_PROPERTY,
    ASSOCIATED_WITH_DOMAIN_ENTITY
}

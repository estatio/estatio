package org.estatio.module.capex.dom.documents.categorisation;

import org.estatio.module.capex.dom.state.State;

public enum IncomingDocumentCategorisationState implements State<IncomingDocumentCategorisationState> {
    NEW,
    CATEGORISED,
    DISCARDED
}

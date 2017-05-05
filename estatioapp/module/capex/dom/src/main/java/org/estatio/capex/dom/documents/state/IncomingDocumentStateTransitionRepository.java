package org.estatio.capex.dom.documents.state;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.invoice.state.IncomingInvoiceStateTransition;
import org.estatio.capex.dom.state.StateTransitionRepositoryAbstract;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = IncomingInvoiceStateTransition.class
)
public class IncomingDocumentStateTransitionRepository
        extends StateTransitionRepositoryAbstract<
                    Document,
                    IncomingDocumentStateTransition,
                    IncomingDocumentStateTransitionType,
                    IncomingDocumentState> {

    public IncomingDocumentStateTransitionRepository() {
        super(IncomingDocumentStateTransition.class);
    }

}

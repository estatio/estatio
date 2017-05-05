package org.estatio.capex.dom.documents.state;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.state.StateTransitionRepository;
import org.estatio.capex.dom.state.StateTransitionServiceSupportAbstract;

@DomainService(nature = NatureOfService.DOMAIN)
public class IncomingDocumentStateTransitionTypeSupportService
        extends StateTransitionServiceSupportAbstract<
                    Document,
                    IncomingDocumentStateTransition,
                    IncomingDocumentStateTransitionType,
                    IncomingDocumentState> {

    public IncomingDocumentStateTransitionTypeSupportService() {
        super(IncomingDocumentStateTransitionType.class, IncomingDocumentStateTransition.class,
                IncomingDocumentState.NEW);
    }

    @Override
    protected StateTransitionRepository<
            Document,
            IncomingDocumentStateTransition,
            IncomingDocumentStateTransitionType,
            IncomingDocumentState
            > getRepository() {
        return repository;
    }

    @Inject
    IncomingDocumentStateTransitionRepository repository;

}

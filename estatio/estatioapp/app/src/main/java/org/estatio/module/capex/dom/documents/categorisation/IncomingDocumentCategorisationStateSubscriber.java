package org.estatio.module.capex.dom.documents.categorisation;

import javax.inject.Inject;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

import org.estatio.module.capex.dom.documents.IncomingDocumentRepository;
import org.estatio.module.task.dom.state.StateTransitionService;
import org.estatio.module.invoice.dom.DocumentTypeData;

@DomainService(nature = NatureOfService.DOMAIN)
public class IncomingDocumentCategorisationStateSubscriber extends AbstractSubscriber {

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void toInstantiateWhen(IncomingDocumentRepository.UploadDomainEvent ev) {
        switch (ev.getEventPhase()) {
        case EXECUTED:
            final Document document = (Document) ev.getReturnValue();
            if (document.getType()==DocumentTypeData.INCOMING.findUsing(documentTypeRepository) && (document.getAtPath().startsWith("/FRA") || document.getAtPath().startsWith("/BEL"))) {
                stateTransitionService.trigger(
                        document, IncomingDocumentCategorisationStateTransitionType.INSTANTIATE, null, null);
            }
            break;
        }
    }

    @Inject
    StateTransitionService stateTransitionService;

    @Inject
    DocumentTypeRepository documentTypeRepository;



}

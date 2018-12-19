package org.estatio.module.coda.subscribers;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.module.capex.dom.documents.IncomingDocumentRepository;

@DomainService(nature = NatureOfService.DOMAIN)
public class IncomingDocumentLinkToIncomingInvoiceForItaSubscriber extends AbstractSubscriber {

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void toInstantiateWhen(IncomingDocumentRepository.UploadDomainEvent ev) {
        switch (ev.getEventPhase()) {
        case EXECUTED:
            final Document document = (Document) ev.getReturnValue();
            // TODO: link
            break;
        }
    }

}

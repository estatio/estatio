package org.estatio.capex.dom.documents;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.incode.module.communications.dom.mixins.Document_communicationAttachments;
import org.incode.module.communications.dom.mixins.Document_communications;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.Document_backgroundCommands;
import org.incode.module.document.dom.mixins.T_documents;

import org.estatio.dom.invoice.DocumentTypeData;

@DomainService(nature = NatureOfService.DOMAIN)
public class HideSupportingDocumentsForIncomingInvoiceDocument extends AbstractSubscriber {

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void on(T_documents.ActionDomainEvent ev) {
        hideIfIncomingDocument(ev);
    }

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void on(Document_backgroundCommands.ActionDomainEvent ev) {
        hideIfIncomingDocument(ev);
    }

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void on(Document_communicationAttachments.ActionDomainEvent ev) {
        hideIfIncomingDocument(ev);
    }

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void on(Document_communications.ActionDomainEvent ev) {
        hideIfIncomingDocument(ev);
    }

    private void hideIfIncomingDocument(final org.apache.isis.applib.services.eventbus.ActionDomainEvent ev) {
        switch (ev.getEventPhase()) {
        case HIDE:
            Object mixedIn = ev.getMixedIn();
            if (mixedIn instanceof Document) {
                Document document = (Document) mixedIn;
                if (DocumentTypeData.docTypeDataFor(document).isIncoming()) {
                    ev.hide();
                }
            }
        }
    }


}

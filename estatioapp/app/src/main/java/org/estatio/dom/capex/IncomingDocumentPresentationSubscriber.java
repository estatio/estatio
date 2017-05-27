package org.estatio.dom.capex;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.i18n.TranslatableString;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentAbstract;
import org.incode.module.document.dom.impl.docs.Document_delete;

import org.estatio.dom.invoice.DocumentTypeData;

@DomainService(
        nature = NatureOfService.DOMAIN,
        objectType = "estatio.VetoDeleteOfIncomingDocumentIfCategorised"
)
public class IncomingDocumentPresentationSubscriber extends AbstractSubscriber {

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void disableIfIncomingAndCategorisedFor(final Document_delete.ActionDomainEvent ev) {
        switch (ev.getEventPhase()) {
        case DISABLE:
            final Document document = (Document) ev.getMixedIn();
            if(DocumentTypeData.hasIncomingType(document) && !DocumentTypeData.INCOMING.isDocTypeFor(document)) {
                ev.veto(TranslatableString.tr(
                        "Document has already been categorized (as {documentType})",
                        "documentType", document.getType().getName()));
            }
        }
    }

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void hideIfIncomingFor(final Document.RenderedAtDomainEvent ev) {
        switch (ev.getEventPhase()) {
        case HIDE:
            final DocumentAbstract document = ev.getSource();
            if(DocumentTypeData.hasIncomingType(document)) {
                ev.hide();
            }
        }
    }

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void hideIfIncomingFor(final Document.StateDomainEvent ev) {
        switch (ev.getEventPhase()) {
        case HIDE:
            final DocumentAbstract document = ev.getSource();
            if(DocumentTypeData.hasIncomingType(document)) {
                ev.hide();
            }
        }
    }
}

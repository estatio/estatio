package org.estatio.module.capex.subscriptions;

import javax.inject.Inject;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.i18n.TranslatableString;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentAbstract;
import org.incode.module.document.dom.impl.docs.Document_delete;

import org.estatio.module.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition;
import org.estatio.module.invoice.dom.DocumentTypeData;

@DomainService(
        nature = NatureOfService.DOMAIN,
        objectType = "estatio.VetoDeleteOfIncomingDocumentIfCategorised"
)
public class IncomingDocumentPresentationSubscriber extends AbstractSubscriber {

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void disableIfIncomingAndCategorisedFor(final Document_delete.ActionDomainEvent ev) {
        final Document document = (Document) ev.getMixedIn();
        switch (ev.getEventPhase()) {
        case DISABLE:
            if(DocumentTypeData.hasIncomingType(document) && !DocumentTypeData.INCOMING.isDocTypeFor(document)) {
                ev.veto(TranslatableString.tr(
                        "Document has already been categorised (as {documentType})",
                        "documentType", document.getType().getName()));
            }
            break;
        case EXECUTING:
            repository.deleteFor(document);
        }
    }

    @Inject
    IncomingDocumentCategorisationStateTransition.Repository repository;

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

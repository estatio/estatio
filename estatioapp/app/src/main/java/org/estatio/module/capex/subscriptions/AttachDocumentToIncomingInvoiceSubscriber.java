package org.estatio.module.capex.subscriptions;

import javax.inject.Inject;

import org.axonframework.eventhandling.annotation.EventHandler;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import org.estatio.module.capex.dom.documents.IncomingDocumentRepository;
import org.estatio.module.docflow.dom.DocFlowZip;
import org.estatio.module.docflow.restapi.DocFlowZipServiceRestApi;

import static org.estatio.module.docflow.dom.DocFlowZipService.PAPERCLIP_ROLE_NAME_GENERATED;

@DomainService(nature = NatureOfService.DOMAIN)
public class AttachDocumentToIncomingInvoiceSubscriber extends AbstractSubscriber {

    @EventHandler
    public void on(IncomingDocumentRepository.UploadDomainEvent ev) {
        switch (ev.getEventPhase()) {
            case EXECUTED:
                final Document document = (Document) ev.getReturnValue();
                final String userRef1 = document.getName().replace(".pdf", "");

                attachCodaDocHead(document, userRef1);
        }
    }

    @EventHandler
    public void on(DocFlowZipServiceRestApi.UpsertDomainEvent ev) {
        switch (ev.getEventPhase()) {
            case EXECUTED:
                final DocFlowZip docFlowZip = (DocFlowZip) ev.getReturnValue();
                final Document document =
                        paperclipRepository.findByAttachedToAndRoleName(docFlowZip, PAPERCLIP_ROLE_NAME_GENERATED)
                                .stream()
                                .map(Paperclip::getDocument)
                                .filter(Document.class::isInstance)
                                .map(Document.class::cast)
                                .findFirst()
                                .orElse(null);

                if(document == null) {
                    // not expected, but can't do anything if this were to occur
                    return;
                }

                final long sdiId = docFlowZip.getSdiId();
                final String userRef1 = "" + sdiId;

                attachCodaDocHead(document, userRef1);
        }
    }

    private void attachCodaDocHead(final Document document, final String userRef1) {
        attachDocumentToCodaDocHeadService.attachCodaDocHead(document, userRef1);
    }

    @Inject
    PaperclipRepository paperclipRepository;

    @Inject
    AttachDocumentToCodaDocHeadService attachDocumentToCodaDocHeadService;
}

package org.estatio.module.capex.subscriptions;

import java.util.Objects;

import javax.inject.Inject;

import org.axonframework.eventhandling.annotation.EventHandler;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

import org.estatio.module.capex.dom.documents.IncomingDocumentRepository;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.coda.dom.doc.CodaDocHead;
import org.estatio.module.coda.dom.doc.CodaDocLine;
import org.estatio.module.coda.dom.doc.CodaDocLineRepository;
import org.estatio.module.coda.dom.doc.DerivedObjectUpdater;
import org.estatio.module.invoice.dom.DocumentTypeData;

@DomainService(nature = NatureOfService.DOMAIN)
public class AttachDocumentToIncomingInvoiceSubscriber extends AbstractSubscriber {

    @EventHandler
    public void on(IncomingDocumentRepository.UploadDomainEvent ev) {
        switch (ev.getEventPhase()) {
            case EXECUTED:
                final Document document = (Document) ev.getReturnValue();
                final String docName = document.getName().replace(".pdf", "");
                final DocumentType docType = DocumentTypeData.INCOMING_INVOICE.findUsing(documentTypeRepository);

                codaDocLineRepository.findByUserRef1(docName)
                        .stream()
                        .map(CodaDocLine::getDocHead)
                        .distinct()
                        .map(CodaDocHead::getIncomingInvoice)
                        .filter(Objects::nonNull)
                        .forEach(invoice -> {
                            paperclipRepository.attach(document, null, invoice);
                            document.setType(docType);
                        });
        }
    }

    @EventHandler
    public void on(DerivedObjectUpdater.UpsertIncomingInvoiceEvent ev) {
        switch (ev.getEventPhase()) {
            case EXECUTED:
                final IncomingInvoice incomingInvoice = (IncomingInvoice) ev.getReturnValue();
                final DocumentType docType = DocumentTypeData.INCOMING_INVOICE.findUsing(documentTypeRepository);

                incomingDocumentRepository.findAllIncomingDocumentsByName(ev.getUserRef1())
                        .forEach(document -> {
                            paperclipRepository.attach(document, null, incomingInvoice);
                            document.setType(docType);
                        });
        }
    }

    @Inject
    CodaDocLineRepository codaDocLineRepository;

    @Inject
    PaperclipRepository paperclipRepository;

    @Inject
    DocumentTypeRepository documentTypeRepository;

    @Inject
    IncomingDocumentRepository incomingDocumentRepository;

}

package org.estatio.module.capex.subscriptions;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.axonframework.eventhandling.annotation.EventHandler;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

import org.estatio.module.capex.dom.documents.IncomingDocumentRepository;
import org.estatio.module.coda.dom.doc.CodaDocHead;
import org.estatio.module.coda.dom.doc.CodaDocLine;
import org.estatio.module.coda.dom.doc.CodaDocLineRepository;
import org.estatio.module.docflow.dom.DocFlowZip;
import org.estatio.module.docflow.restapi.DocFlowZipServiceRestApi;
import org.estatio.module.invoice.dom.DocumentTypeData;

import static org.estatio.module.docflow.dom.DocFlowZipService.PAPERCLIP_ROLE_NAME_GENERATED;

@DomainService(nature = NatureOfService.DOMAIN)
public class AttachDocumentToIncomingInvoiceSubscriber extends AbstractSubscriber {

    @EventHandler
    public void on(IncomingDocumentRepository.UploadDomainEvent ev) {
        switch (ev.getEventPhase()) {
            case EXECUTED:
                final Document document = (Document) ev.getReturnValue();

                List<String> userRef1Candidates = Lists.newArrayList();
                final String userRef1 = document.getName().replace(".pdf", "");
                userRef1Candidates.add(userRef1);
                userRef1Candidates.add("B" + userRef1);

                kickCodaDocHeadsAndUpdatePaperclips(document, userRef1Candidates);
        }
    }

    @EventHandler
    public void on(DocFlowZipServiceRestApi.UpsertDomainEvent ev) {
        switch (ev.getEventPhase()) {
            case EXECUTED:
                final DocFlowZip docFlowZip = (DocFlowZip) ev.getReturnValue();
                final Document document = docFlowZip.locateAttachedDocument(PAPERCLIP_ROLE_NAME_GENERATED);

                if(document == null) {
                    // not expected, but can't do anything if this were to occur
                    return;
                }

                final long sdiId = docFlowZip.getSdiId();
                final String userRef1 = "" + sdiId;

                kickCodaDocHeadsAndUpdatePaperclips(document, userRef1);
        }
    }

    private void kickCodaDocHeadsAndUpdatePaperclips(final Document document, final List<String> userRef1Candidates) {
        for (final String userRef1Candidate : userRef1Candidates) {
            final List<CodaDocHead> updated = kickCodaDocHeadsAndUpdatePaperclips(document, userRef1Candidate);
            if(!updated.isEmpty()) {
                // one or more doc heads were found, so no need to try other candidates
                return;
            }
        }
    }

    private List<CodaDocHead> kickCodaDocHeadsAndUpdatePaperclips(final Document document, final String userRef1) {
        final DocumentType docType = DocumentTypeData.INCOMING_INVOICE.findUsing(documentTypeRepository);

        final List<CodaDocHead> updated = Lists.newArrayList();
        codaDocLineRepository.findByUserRef1(userRef1)
                .stream()
                .map(CodaDocLine::getDocHead)
                .distinct()
                .peek(CodaDocHead::kick) // if was invalid but is now valid, then kick in order to create invoice etc.
                .peek(updated::add)      // keep track of fact that this was updated.
                .map(CodaDocHead::getIncomingInvoice)
                .filter(Objects::nonNull)
                .forEach(invoice -> {
                    paperclipRepository.attach(document, null, invoice);
                    document.setType(docType);
                });

        return updated;
    }

    @Inject
    CodaDocLineRepository codaDocLineRepository;

    @Inject
    PaperclipRepository paperclipRepository;

    @Inject
    DocumentTypeRepository documentTypeRepository;


}

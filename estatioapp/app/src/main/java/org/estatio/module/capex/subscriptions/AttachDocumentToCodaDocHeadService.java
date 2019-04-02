package org.estatio.module.capex.subscriptions;

import java.util.Objects;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

import org.estatio.module.coda.dom.doc.CodaDocHead;
import org.estatio.module.coda.dom.doc.CodaDocLine;
import org.estatio.module.coda.dom.doc.CodaDocLineRepository;
import org.estatio.module.invoice.dom.DocumentTypeData;

@DomainService(nature = NatureOfService.DOMAIN)
public class AttachDocumentToCodaDocHeadService  {

    public void attachCodaDocHead(final Document document, final String userRef1) {
        final DocumentType docType = DocumentTypeData.INCOMING_INVOICE.findUsing(documentTypeRepository);
        codaDocLineRepository.findByUserRef1(userRef1)
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

    @Inject
    CodaDocLineRepository codaDocLineRepository;

    @Inject
    PaperclipRepository paperclipRepository;

    @Inject
    DocumentTypeRepository documentTypeRepository;

}

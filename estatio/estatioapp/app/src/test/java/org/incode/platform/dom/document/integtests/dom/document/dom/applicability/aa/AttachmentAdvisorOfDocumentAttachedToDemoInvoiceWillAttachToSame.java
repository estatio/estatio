package org.incode.platform.dom.document.integtests.dom.document.dom.applicability.aa;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.incode.module.document.dom.impl.applicability.AttachmentAdvisorAbstract;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.platform.dom.document.integtests.demo.dom.invoice.DemoInvoice;

public class AttachmentAdvisorOfDocumentAttachedToDemoInvoiceWillAttachToSame extends AttachmentAdvisorAbstract<Document> {

    public AttachmentAdvisorOfDocumentAttachedToDemoInvoiceWillAttachToSame() {
        super(Document.class);
    }

    @Override
    protected List<PaperclipSpec> doAdvise(
            final DocumentTemplate documentTemplate,
            final Document document,
            final Document createdDocument) {

        final List<Paperclip> paperclips = paperclipRepository.findByDocument(document);
        final Optional<DemoInvoice> demoInvoiceIfAny =
                paperclips.stream().map(Paperclip::getAttachedTo)
                        .filter(DemoInvoice.class::isInstance)
                        .map(DemoInvoice.class::cast)
                        .findFirst();

        return demoInvoiceIfAny.isPresent()
                ? Collections.singletonList(new PaperclipSpec(null, document, createdDocument))
                : Collections.emptyList();
    }

    @Inject
    PaperclipRepository paperclipRepository;


}


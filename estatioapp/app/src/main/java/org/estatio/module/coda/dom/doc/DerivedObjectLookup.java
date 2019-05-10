package org.estatio.module.coda.dom.doc;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;

@DomainService(nature = NatureOfService.DOMAIN)
public class DerivedObjectLookup {

    IncomingInvoice invoiceIfAnyFrom(final CodaDocHead docHead) {
        return docHead != null ? docHead.getIncomingInvoice() : null;
    }

    String documentNameIfAnyFrom(final CodaDocHead docHead) {
        return docHead != null
                ? docHead.getSummaryLineDocumentName(LineCache.DEFAULT)
                : null;
    }

    Paperclip paperclipIfAnyFrom(final CodaDocHead docHead) {

        final IncomingInvoice invoiceIfAny = invoiceIfAnyFrom(docHead);
        final String documentNameIfAny = documentNameIfAnyFrom(docHead);

        return documentNameIfAny != null && invoiceIfAny != null
                ? paperclipRepository.findByAttachedTo(invoiceIfAny)
                .stream()
                .filter(pc -> documentNameIfAny.equals(pc.getDocument().getName()))
                .findFirst().orElse(null)
                : null;
    }


    @Inject
    PaperclipRepository paperclipRepository;


}

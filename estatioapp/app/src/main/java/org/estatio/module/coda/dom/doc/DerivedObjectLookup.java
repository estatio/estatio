package org.estatio.module.coda.dom.doc;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.order.OrderItem;
import org.estatio.module.capex.dom.orderinvoice.OrderItemInvoiceItemLink;
import org.estatio.module.capex.dom.orderinvoice.OrderItemInvoiceItemLinkRepository;

@DomainService(nature = NatureOfService.DOMAIN)
public class DerivedObjectLookup {

    static final String AT_PATH = "/ITA";

    public IncomingInvoice invoiceIfAnyFrom(final CodaDocHead docHead) {
        return docHead != null ? docHead.getIncomingInvoice() : null;
    }

    public OrderItemInvoiceItemLink linkIfAnyFrom(final CodaDocHead docHead) {

        final IncomingInvoice invoiceIfAny = invoiceIfAnyFrom(docHead);

        final OrderItem orderItemIfAny =
                docHead != null
                        ? docHead.getSummaryLineExtRefOrderItem()
                        : null;
        final IncomingInvoiceItem invoiceItemIfAny = firstItemOf(invoiceIfAny);
        return orderItemIfAny != null && invoiceItemIfAny != null
                ? linkRepository.findUnique(orderItemIfAny, invoiceItemIfAny)
                : null;
    }

    public String documentNameIfAnyFrom(final CodaDocHead docHead) {
        return docHead != null
                ? docHead.getSummaryLineDocumentName()
                : null;
    }

    public Paperclip paperclipIfAnyFrom(final CodaDocHead docHead) {

        final IncomingInvoice invoiceIfAny = invoiceIfAnyFrom(docHead);
        final String documentNameIfAny = documentNameIfAnyFrom(docHead);

        return documentNameIfAny != null && invoiceIfAny != null
                ? paperclipRepository.findByAttachedTo(invoiceIfAny)
                .stream()
                .filter(pc -> documentNameIfAny.equals(pc.getDocument().getName()))
                .findFirst().orElse(null)
                : null;
    }


    private static IncomingInvoiceItem firstItemOf(final IncomingInvoice existingInvoiceIfAny) {
        return existingInvoiceIfAny != null
                ? existingInvoiceIfAny.getItems().size() == 1
                ? (IncomingInvoiceItem) existingInvoiceIfAny.getItems().first()
                : null
                : null;
    }

    @Inject
    PaperclipRepository paperclipRepository;

    @Inject
    OrderItemInvoiceItemLinkRepository linkRepository;


}

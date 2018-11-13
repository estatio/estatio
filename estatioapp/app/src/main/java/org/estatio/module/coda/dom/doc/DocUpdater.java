package org.estatio.module.coda.dom.doc;

import org.incode.module.document.dom.impl.paperclips.Paperclip;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.orderinvoice.OrderItemInvoiceItemLink;

public interface DocUpdater {
    void check(CodaDocHead codaDocHead, CodaDocHead previousCodaDocHead);


    IncomingInvoice invoiceIfAnyFrom(CodaDocHead docHead);
    OrderItemInvoiceItemLink linkIfAnyFrom(CodaDocHead docHead);
    String documentNameIfAnyFrom(CodaDocHead docHead);
    Paperclip paperclipIfAnyFrom(CodaDocHead docHead);

    IncomingInvoice updateIncomingInvoice(CodaDocHead docHead);
    IncomingInvoice updateIncomingInvoice(CodaDocHead docHead, IncomingInvoice existingInvoiceIfAny);

    void updateSyncAndHandling(
            CodaDocHead docHead, IncomingInvoice incomingInvoice);

    void updateLinkToOrderItem(
            CodaDocHead docHead,
            ErrorSet softErrors);
    void updateLinkToOrderItem(
            CodaDocHead docHead, OrderItemInvoiceItemLink existingLinkIfAny,
            ErrorSet softErrors);

    void updatePaperclip(
            CodaDocHead docHead,
            ErrorSet softErrors);

    void updatePaperclip(
            CodaDocHead docHead, Paperclip existingPaperclipIfAny, String existingDocumentNameIfAny,
            ErrorSet softErrors);

    void updatePendingTask(
            final CodaDocHead docHead,
            ErrorSet hardErrors, ErrorSet softErrors);
}

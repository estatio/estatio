package org.estatio.capex.dom.documents.categorisation.document;

import org.apache.isis.applib.annotation.Mixin;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.documents.categorisation.invoice.IncomingDocAsInvoiceViewModel;
import org.estatio.dom.invoice.DocumentTypeData;

@Mixin(method = "act")
public class Document_classifyAsInvoice extends Document_classifyAsAbstract {

    protected final Document document;

    public Document_classifyAsInvoice(final Document document) {
        super(document, DocumentTypeData.INCOMING_INVOICE);
        this.document = document;
    }

    protected IncomingDocAsInvoiceViewModel doCreate() {
        return new IncomingDocAsInvoiceViewModel(getDocument());
    }

}

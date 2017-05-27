package org.estatio.capex.dom.documents;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.documents.invoice.IncomingInvoiceViewModel;
import org.estatio.dom.invoice.DocumentTypeData;

@Mixin(method = "act")
public class HasDocumentAbstract_categoriseAsInvoice
        extends HasDocumentAbstract_categoriseAbstract {

    public HasDocumentAbstract_categoriseAsInvoice(final HasDocumentAbstract hasDocument) {
        super(hasDocument, DocumentTypeData.INCOMING_INVOICE, IncomingInvoiceViewModel.class);
    }

}

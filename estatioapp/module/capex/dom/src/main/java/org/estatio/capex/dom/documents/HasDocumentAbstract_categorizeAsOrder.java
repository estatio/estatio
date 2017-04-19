package org.estatio.capex.dom.documents;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.documents.order.IncomingOrderViewModel;
import org.estatio.dom.invoice.DocumentTypeData;

@Mixin(method = "act")
public class HasDocumentAbstract_categorizeAsOrder extends HasDocumentAbstract_categorizeAbstract {

    public HasDocumentAbstract_categorizeAsOrder(final HasDocumentAbstract hasDocument) {
        super(hasDocument, DocumentTypeData.INCOMING_ORDER, IncomingOrderViewModel.class);
    }

}

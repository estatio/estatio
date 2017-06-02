package org.estatio.capex.dom.documents.categorisation.document;

import org.apache.isis.applib.annotation.Mixin;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.documents.categorisation.order.IncomingDocAsOrderViewModel;
import org.estatio.dom.invoice.DocumentTypeData;

@Mixin(method = "act")
public class Document_classifyAsOrder extends Document_classifyAsAbstract {

    protected final Document document;

    public Document_classifyAsOrder(final Document document) {
        super(document, DocumentTypeData.INCOMING_ORDER);
        this.document = document;
    }

    protected IncomingDocAsOrderViewModel doCreate() {
        return new IncomingDocAsOrderViewModel(getDocument());
    }

}

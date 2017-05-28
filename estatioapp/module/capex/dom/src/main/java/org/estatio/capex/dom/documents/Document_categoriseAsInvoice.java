package org.estatio.capex.dom.documents;

import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.services.eventbus.ActionDomainEvent;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.dom.asset.Property;
import org.estatio.dom.invoice.DocumentTypeData;

@Mixin(method = "act")
public class Document_categoriseAsInvoice extends Document_categoriseAsAbstract {

    // workaround for ISIS-1628
    private final Document document;

    public Document_categoriseAsInvoice(final Document document) {
        super(document, DocumentTypeData.INCOMING_INVOICE);
        this.document = document;
    }

    // workaround for ISIS-1628
    @Override
    public HasDocument act(Property property) {
        return super.act(property);
    }

}

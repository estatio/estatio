package org.estatio.capex.dom.documents;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.documents.invoice.IncomingInvoiceViewModel;
import org.estatio.dom.asset.Property;
import org.estatio.dom.invoice.DocumentTypeData;

@Mixin(method = "act")
public class HasDocumentAbstract_categoriseAsInvoice
        extends HasDocumentAbstract_categoriseAbstract {

    // workaround for https://issues.apache.org/jira/browse/ISIS-1628
    protected final HasDocument hasDocument;

    public HasDocumentAbstract_categoriseAsInvoice(final HasDocumentAbstract hasDocument) {
        super(hasDocument, DocumentTypeData.INCOMING_INVOICE, IncomingInvoiceViewModel.class);
        this.hasDocument = hasDocument;
    }

    // workaround for https://issues.apache.org/jira/browse/ISIS-1628
    @Override
    public HasDocument act(final Property property, final boolean goToNext) {
        return super.act(property, goToNext);
    }
}

package org.estatio.capex.dom.documents;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.dom.asset.Property;
import org.estatio.dom.invoice.DocumentTypeData;

@Mixin(method = "act")
public class HasDocument_classifyAsInvoice
        extends HasDocument_classifyAbstract {

    // workaround for https://issues.apache.org/jira/browse/ISIS-1628
    protected final HasDocument hasDocument;

    public HasDocument_classifyAsInvoice(final HasDocumentAbstract hasDocument) {
        super(hasDocument, DocumentTypeData.INCOMING_INVOICE);
        this.hasDocument = hasDocument;
    }

    // workaround for https://issues.apache.org/jira/browse/ISIS-1628
    @Override
    public HasDocumentAbstract act(final Property property, final boolean goToNext) {
        return super.act(property, goToNext);
    }

    @Override
    public Property default0Act() {
        return super.default0Act();
    }

    @Override
    public boolean default1Act() {
        return super.default1Act();
    }

    @Override
    public boolean hideAct() {
        return super.hideAct();
    }
}

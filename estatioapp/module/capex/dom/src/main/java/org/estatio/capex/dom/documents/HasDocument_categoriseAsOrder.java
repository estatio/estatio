package org.estatio.capex.dom.documents;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.dom.asset.Property;
import org.estatio.dom.invoice.DocumentTypeData;

@Mixin(method = "act")
public class HasDocument_categoriseAsOrder
        extends HasDocument_categoriseAbstract {

    // workaround for https://issues.apache.org/jira/browse/ISIS-1628
    private final HasDocument hasDocument;

    public HasDocument_categoriseAsOrder(final HasDocumentAbstract hasDocument) {
        super(hasDocument, DocumentTypeData.INCOMING_ORDER);
        this.hasDocument = hasDocument;
    }

    // workaround for https://issues.apache.org/jira/browse/ISIS-1628
    @Override
    public HasDocumentAbstract act(
            @Nullable final Property property,
            final boolean goToNext) {
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

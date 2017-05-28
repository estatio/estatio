package org.estatio.capex.dom.documents;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.EstatioCapexDomModule;
import org.estatio.dom.asset.Property;
import org.estatio.dom.invoice.DocumentTypeData;

public abstract class Document_categoriseAsAbstract extends DocumentOrHasDocument_categoriseAsAbstract {

    private final Document document;

    public Document_categoriseAsAbstract(
            final Document document,
            final DocumentTypeData documentTypeData) {
        super(documentTypeData);
        this.document = document;
    }

    @Override
    protected Document getDocument() {
        return document;
    }

    public static class DomainEvent extends EstatioCapexDomModule.ActionDomainEvent<Document> {}

    @Action(
            semantics = SemanticsOf.SAFE,
            domainEvent = DomainEvent.class
    )
    @ActionLayout(
            contributed= Contributed.AS_ACTION,
            cssClassFa = "folder-open-o"
    )
    public HasDocument act(final Property property) {
        final HasDocument viewModel = categoriseAndAttachPaperclip(property);
        return viewModel;
    }

    @Override
    public Property default0Act() {
        return super.default0Act();
    }

    @Override
    public boolean hideAct() {
        return super.hideAct();
    }

}

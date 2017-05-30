package org.estatio.capex.dom.documents;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.EstatioCapexDomModule;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType;
import org.estatio.dom.asset.Property;
import org.estatio.dom.invoice.DocumentTypeData;

public abstract class Document_classifyAsAbstract extends DocumentLike_categoriseAsAbstract  {

    private final Document document;

    public Document_classifyAsAbstract(
            final Document document,
            final DocumentTypeData documentTypeData) {
        super(documentTypeData, IncomingDocumentCategorisationStateTransitionType.CLASSIFY_AS_INVOICE_OR_ORDER);
        this.document = document;
    }

    @Programmatic
    @Override
    public Document getDomainObject() {
        return document;
    }

    public static class DomainEvent extends EstatioCapexDomModule.ActionDomainEvent<Document_classifyAsAbstract> {}

    @Action(
            semantics = SemanticsOf.SAFE,
            domainEvent = DomainEvent.class
    )
    @ActionLayout(
            contributed= Contributed.AS_ACTION,
            cssClassFa = "folder-open-o"
    )
    public Object act(final Property property) {
        final Object viewModel = categoriseAndAttachPaperclip(property);
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

package org.estatio.capex.dom.documents;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.document.dom.impl.docs.Document;

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

    @Programmatic
    @Override
    public Document getDomainObject() {
        return document;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(
            contributed= Contributed.AS_ACTION,
            cssClassFa = "folder-open-o"
    )
    public HasDocumentAbstract act(
            @Nullable final Property property,
            @Nullable final String comment) {
        final HasDocumentAbstract viewModel = categoriseAndAttachPaperclip(property);

        // to trigger state transition
        super.act(comment);

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

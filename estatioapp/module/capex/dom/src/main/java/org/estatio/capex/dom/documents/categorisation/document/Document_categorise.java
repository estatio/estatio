package org.estatio.capex.dom.documents.categorisation.document;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.dom.asset.Property;
import org.estatio.dom.invoice.DocumentTypeData;

@Mixin(method = "act")
public class Document_categorise extends DocOrIncomingDocViewModel_categoriseAsAbstract {

    private final Document document;

    public Document_categorise(final Document document) {
        super();
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
    public Object act(
            final DocumentTypeData documentTypeData,
            @Nullable final Property property,
            @Nullable final String comment) {
        categoriseAndAttachPaperclip(property, documentTypeData);

        trigger(comment);

        return viewModelFactory.createFor(document);
    }

    @Override
    public List<DocumentTypeData> choices0Act() {
        return super.choices0Act();
    }

    @Override
    public Property default1Act() {
        return super.default1Act();
    }

    @Override
    public boolean hideAct() {
        return super.hideAct();
    }

}

package org.incode.module.document.dom.impl.docs;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.document.DocumentModule;

@Mixin
public class DocumentTemplate_updatePreviewOnly {

    //region > constructor
    private final DocumentTemplate documentTemplate;

    public DocumentTemplate_updatePreviewOnly(final DocumentTemplate documentTemplate) {
        this.documentTemplate = documentTemplate;
    }
    //endregion


    public static class ActionDomainEvent extends DocumentModule.ActionDomainEvent<DocumentTemplate_updatePreviewOnly>  { }

    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            domainEvent = ActionDomainEvent.class
    )
    public DocumentTemplate $$(
            @ParameterLayout(named = "Preview only?")
            final boolean previewOnly) {
        documentTemplate.setPreviewOnly(previewOnly);
        return documentTemplate;
    }

    public boolean default0$$() {
        return documentTemplate.isPreviewOnly();
    }

}

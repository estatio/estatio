package org.incode.module.document.dom.impl.docs;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.document.dom.DocumentModule;

@Mixin
public class DocumentTemplate_updateNameText {

    //region > constructor
    private final DocumentTemplate documentTemplate;

    public DocumentTemplate_updateNameText(final DocumentTemplate documentTemplate) {
        this.documentTemplate = documentTemplate;
    }
    //endregion


    public static class ActionDomainEvent extends DocumentModule.ActionDomainEvent<DocumentTemplate_updateNameText>  { }
    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            domainEvent = ActionDomainEvent.class
    )
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public DocumentTemplate $$(
            @ParameterLayout(named = "Text", multiLine = DocumentModule.Constants.TEXT_MULTILINE)
            final String text
    ) {
        documentTemplate.setNameText(text);
        return documentTemplate;
    }

    public String default0$$() {
        return documentTemplate.getNameText();
    }



}

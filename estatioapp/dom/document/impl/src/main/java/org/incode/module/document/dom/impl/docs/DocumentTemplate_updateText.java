package org.incode.module.document.dom.impl.docs;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.document.dom.DocumentModule;

@Mixin
public class DocumentTemplate_updateText {

    //region > constructor
    private final DocumentTemplate documentTemplate;

    public DocumentTemplate_updateText(final DocumentTemplate documentTemplate) {
        this.documentTemplate = documentTemplate;
    }
    //endregion


    public static class ActionDomainEvent extends DocumentModule.ActionDomainEvent<DocumentTemplate_updateText>  { }
    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            domainEvent = ActionDomainEvent.class
    )
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public DocumentTemplate $$(
            @ParameterLayout(named = "Text", multiLine = DocumentModule.Constants.TEXT_MULTILINE)
            final String text
    ) {
        documentTemplate.setText(text);
        return documentTemplate;
    }

    public String default0$$() {
        return documentTemplate.getText();
    }

    public boolean hide$$() {
        return documentTemplate.getSort() != DocumentSort.TEXT;
    }


}

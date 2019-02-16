package org.incode.module.document.dom.impl.docs;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Clob;

import org.incode.module.document.DocumentModule;

@Mixin
public class DocumentTemplate_uploadClob {

    //region > constructor
    private final DocumentTemplate documentTemplate;

    public DocumentTemplate_uploadClob(final DocumentTemplate documentTemplate) {
        this.documentTemplate = documentTemplate;
    }
    //endregion


    public static class ActionDomainEvent extends DocumentModule.ActionDomainEvent<DocumentTemplate_uploadClob>  { }
    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            domainEvent = ActionDomainEvent.class
    )
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public DocumentTemplate $$(
            @ParameterLayout(named = "File")
            final Clob clob
    ) {
        documentTemplate.setClobChars(clob.getChars().toString());
        return documentTemplate;
    }

    public boolean hide$$() {
        return documentTemplate.getSort() != DocumentSort.CLOB;
    }

}

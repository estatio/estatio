package org.incode.module.document.dom.impl.docs;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Clob;

import org.incode.module.document.dom.DocumentModule;

@Mixin
public class DocumentAbstract_downloadTextAsClob {

    //region > constructor
    private final DocumentAbstract<?> document;

    public DocumentAbstract_downloadTextAsClob(final DocumentAbstract<?> document) {
        this.document = document;
    }
    //endregion

    public static class ActionDomainEvent extends DocumentModule.ActionDomainEvent<DocumentAbstract_downloadTextAsClob>  { }
    @Action(
            semantics = SemanticsOf.SAFE,
            domainEvent = ActionDomainEvent.class
    )
    @ActionLayout(named = "Download")
    public Clob $$() {
        return new Clob(document.getName(), document.getMimeType(), document.getText());
    }

    public boolean hide$$() {
        return document.getSort() != DocumentSort.TEXT;
    }


}

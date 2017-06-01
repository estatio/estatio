package org.estatio.capex.dom.documents.categorisation.document;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Mixin;

import org.incode.module.document.dom.impl.docs.Document;

@Mixin(method = "act")
public class Document_resetCategorisation extends DocOrIncomingDoc_resetAbstract {

    // workaround for ISIS-1628
    private final Document document;

    public Document_resetCategorisation(final Document document) {
        super();
        this.document = document;
    }

    @Override
    public Document getDomainObject() {
        return document;
    }

    @Override
    public Document act(@Nullable final String comment) {
        return super.act(comment);
    }

    @Override
    public boolean hideAct() {
        return super.hideAct();
    }

    @Override
    public String disableAct() {
        return super.disableAct();
    }
}

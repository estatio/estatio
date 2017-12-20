package org.incode.module.document.dom.impl.docs;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Clob;

import org.incode.module.document.dom.DocumentModule;
import org.incode.module.document.dom.spi.UrlDownloadService;

@Mixin
public class Document_downloadExternalUrlAsClob {

    //region > constructor
    private final Document document;

    public Document_downloadExternalUrlAsClob(final Document document) {
        this.document = document;
    }
    //endregion


    public static class ActionDomainEvent extends DocumentModule.ActionDomainEvent<Document_downloadExternalUrlAsClob> { }
    @Action(
            semantics = SemanticsOf.SAFE,
            domainEvent = ActionDomainEvent.class
    )
    @ActionLayout(named = "Download")
    public Clob $$() {
        return urlDownloadService.downloadAsClob(document);
    }

    public boolean hide$$() {
        return document.getSort() != DocumentSort.EXTERNAL_CLOB;
    }

    @Inject
    UrlDownloadService urlDownloadService;

}

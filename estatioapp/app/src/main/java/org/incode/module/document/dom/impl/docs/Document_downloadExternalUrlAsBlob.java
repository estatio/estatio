package org.incode.module.document.dom.impl.docs;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Blob;

import org.incode.module.document.DocumentModule;
import org.incode.module.document.dom.spi.UrlDownloadService;

@Mixin
public class Document_downloadExternalUrlAsBlob {

    //region > constructor
    private final Document document;

    public Document_downloadExternalUrlAsBlob(final Document document) {
        this.document = document;
    }
    //endregion


    public static class ActionDomainEvent extends DocumentModule.ActionDomainEvent<Document_downloadExternalUrlAsBlob> { }
    @Action(
            semantics = SemanticsOf.SAFE,
            domainEvent = ActionDomainEvent.class
    )
    @ActionLayout(named = "Download")
    public Blob $$() {
        return urlDownloadService.downloadAsBlob(document);
    }

    public boolean hide$$() {
        return document.getSort() != DocumentSort.EXTERNAL_BLOB;
    }



    @Inject
    UrlDownloadService urlDownloadService;

}

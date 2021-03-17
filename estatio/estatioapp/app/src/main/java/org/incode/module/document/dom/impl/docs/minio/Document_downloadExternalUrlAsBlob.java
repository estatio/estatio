package org.incode.module.document.dom.impl.docs.minio;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Blob;

import org.incode.module.document.DocumentModule;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentSort;
import org.incode.module.document.spi.minio.ExternalUrlDownloadService;

@Mixin(method="act")
public class Document_downloadExternalUrlAsBlob {

    private final Document document;
    public Document_downloadExternalUrlAsBlob(final Document document) {
        this.document = document;
    }


    public static class ActionDomainEvent extends DocumentModule.ActionDomainEvent<Document_downloadExternalUrlAsBlob> { }
    @Action(
            semantics = SemanticsOf.SAFE,
            domainEvent = ActionDomainEvent.class
    )
    @ActionLayout(named = "Download")
    public Blob act() {
        return externalUrlDownloadService.downloadAsBlob(document);
    }

    public boolean hideAct() {
        return document.getSort() != DocumentSort.EXTERNAL_BLOB;
    }


    @Inject
    ExternalUrlDownloadService externalUrlDownloadService;

}

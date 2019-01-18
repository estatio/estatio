package org.incode.module.document.dom.impl.docs.minio;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.message.MessageService2;
import org.apache.isis.applib.value.Blob;

import org.incode.module.document.DocumentModule;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.spi.minio.ExternalUrlDownloadService;

@Mixin(method = "act")
public class Document_restoreBlob {

    private final Document document;
    public Document_restoreBlob(final Document document) {
        this.document = document;
    }


    public static class ActionDomainEvent extends DocumentModule.ActionDomainEvent<Document_restoreBlob> { }
    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            domainEvent = ActionDomainEvent.class
    )
    public Document act() {

        final Blob archived = externalUrlDownloadService.downloadAsBlob(document);
        if(archived == null) {
            // service will have raised message
            return document;
        }
        document.setBlobBytes(archived.getBytes());

        return document;
    }

    public boolean hideAct() {
        return !document.getSort().isExternal();
    }
    public String disableAct() {
        return document.getBlob() != null ? "Not purged" : null;
    }

    @Inject
    ExternalUrlDownloadService externalUrlDownloadService;
    @Inject
    MessageService2 messageService;

}

package org.incode.module.document.dom.impl.docs.minio;

import java.util.Objects;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.message.MessageService2;
import org.apache.isis.applib.value.Blob;

import org.incode.module.document.DocumentModule;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.spi.minio.DocumentBlobPurgeService;
import org.incode.module.document.spi.minio.ExternalUrlDownloadService;

@Mixin(method = "act")
public class Document_purgeBlob {

    //region > constructor
    private final Document document;

    public Document_purgeBlob(final Document document) {
        this.document = document;
    }
    //endregion


    public static class ActionDomainEvent extends DocumentModule.ActionDomainEvent<Document_purgeBlob> { }

    /**
     * The idea is that this would be called by a background process, from {@link DocumentBlobPurgeService}.
     */
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

        final Blob current = document.getBlob();
        if (!Objects.equals(archived, current)) {
            messageService.warnUser(String.format(
                    "Archived blob differs, current: '%s' vs archived: '%s'", current, archived));
        } else {
            document.setBlobBytes(null);
        }

        return document;
    }

    public boolean hideAct() {
        return !document.getSort().isExternal();
    }
    public String disableAct() {
        if (document.getBlob() == null) {
            return "Blob has already been purged";
        }

        return null;
    }

    @Inject
    ExternalUrlDownloadService externalUrlDownloadService;
    @Inject
    MessageService2 messageService;

}

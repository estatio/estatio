package org.incode.module.document.dom.impl.docs.minio;

import java.util.Arrays;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.message.MessageService2;
import org.apache.isis.applib.value.Blob;

import org.incode.module.document.DocumentModule;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentSort;
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

        // check that the blob was indeed correctly archived.
        final Blob archived = externalUrlDownloadService.downloadAsBlob(document);
        if(archived == null) {
            // service will have raised message

            // and salvage the situation
            document.setSort(DocumentSort.BLOB);
            document.setExternalUrl(null);

            return document;
        }
        final byte[] archivedBytes = archived.getBytes();

        final byte[] currentBytes = document.getBlobBytes();
        if (!Arrays.equals(archivedBytes, currentBytes)) {
            messageService.warnUser(String.format(
                    "Archived blob differs, current: %d vs archived: %d", currentBytes.length, archivedBytes.length));
        } else {
            document.setBlobBytes(null);
        }

        return document;
    }

    public boolean hideAct() {
        return !document.getSort().isExternal();
    }
    public String disableAct() {
        if (document.getBlobBytes() == null) {
            return "Blob has already been purged";
        }

        return null;
    }

    @Inject
    ExternalUrlDownloadService externalUrlDownloadService;
    @Inject
    MessageService2 messageService;

}

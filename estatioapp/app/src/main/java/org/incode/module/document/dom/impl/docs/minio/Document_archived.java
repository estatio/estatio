package org.incode.module.document.dom.impl.docs.minio;

import java.util.Arrays;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.message.MessageService2;
import org.apache.isis.applib.value.Blob;

import org.incode.module.document.DocumentModule;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentSort;
import org.incode.module.document.spi.minio.DomainObjectPropertyProviderForDocument;
import org.incode.module.document.spi.minio.ExternalUrlDownloadService;
import org.incode.module.minio.dopserver.spi.DomainObjectProperty;

/**
 * Notifies a {@link Document} that its blob is now stored externally.
 *
 * <p>
 *     This is normally performed as part of the period archiving process, as mediated by
 *     {@link DomainObjectPropertyProviderForDocument#blobArchived(Object, DomainObjectProperty, String)}.
 * </p>
 *
 */
@Mixin(method = "act")
public class Document_archived {

    private final Document document;

    public Document_archived(final Document document) {
        this.document = document;
    }


    public static class ActionDomainEvent extends DocumentModule.ActionDomainEvent<Document_archived> { }

    /**
     *
     * <p>
     *     Such {@link Document}s will have their {@link Document#getSort() sort} set to {@link DocumentSort#EXTERNAL_BLOB}, but will still have a non-null {@link Document#getBlob() blob}.
     *     This action simply sets that property to <tt>null</tt>.
     * </p>
     */
    @Action(
            semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE,
            domainEvent = ActionDomainEvent.class,
            restrictTo = RestrictTo.PROTOTYPING
    )
    public Document act(
            @ParameterLayout(named = "External URL")
            final String externalUrl
    ) {

        // check that the blob was indeed correctly archived.
        final Blob archived = externalUrlDownloadService.downloadAsBlob(document.getName(), externalUrl);
        if(archived == null) {
            // service will have raised message
            return document;
        }
        final byte[] archivedBytes = archived.getBytes();

        final byte[] currentBytes = document.getBlobBytes();
        if (!Arrays.equals(archivedBytes, currentBytes)) {
            messageService.warnUser(String.format(
                    "Archived blob differs, current: %d vs archived: %d", currentBytes.length, archivedBytes.length));
        } else {
            document.setExternalUrl(externalUrl);

            final DocumentSort sort = document.getSort();
            document.setSort(sort.asExternal());
        }


        return document;
    }

    public boolean hideAct() {
        return document.getSort() == DocumentSort.EMPTY || document.getSort().isExternal();
    }

    @Inject
    ExternalUrlDownloadService externalUrlDownloadService;
    @Inject
    MessageService2 messageService;


}

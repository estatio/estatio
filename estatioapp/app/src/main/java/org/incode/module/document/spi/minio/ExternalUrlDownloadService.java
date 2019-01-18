package org.incode.module.document.spi.minio;

import java.io.IOException;

import javax.inject.Inject;

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.services.message.MessageService2;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.applib.value.Clob;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.minio.dopserver.dom.BlobClobDownloadService;

@DomainService(
        nature = NatureOfService.DOMAIN,
        objectType = "incodeDocuments.ExternalUrlDownloadService"
)
public class ExternalUrlDownloadService {

    @Programmatic
    public Blob downloadAsBlob(final Document document) {
        final String externalUrl = document.getExternalUrl();
        final String documentName = document.getName();
        try {
            return blobClobDownloadService.downloadBlob(documentName, externalUrl);

        } catch (ApplicationException ex) {
            messageService.warnUser(ex.getTranslatableMessage(), ex.getTranslationContext());
            return null;
        } catch (IOException e) {
            messageService.warnUser(
                    TranslatableString.tr(
                            "Could not download external URL: {url}",
                            "url", externalUrl),
                    ExternalUrlDownloadService.class, "download");
            return null;
        }
    }

    @Programmatic
    public Clob downloadAsClob(final Document document) {
        final String documentName = document.getName();
        final String externalUrl = document.getExternalUrl();
        try {
            return blobClobDownloadService.downloadClob(documentName, externalUrl);
        } catch (ApplicationException ex) {
            messageService.warnUser(ex.getTranslatableMessage(), ex.getTranslationContext());
            return null;
        } catch (IOException e) {
            messageService.warnUser(
                    TranslatableString.tr(
                            "Could not download external URL: {url}",
                            "url", externalUrl),
                    ExternalUrlDownloadService.class, "downloadExternalUrlAsClob");
            return null;
        }
    }

    @Inject
    BlobClobDownloadService blobClobDownloadService;
    @Inject
    MessageService2 messageService;

}

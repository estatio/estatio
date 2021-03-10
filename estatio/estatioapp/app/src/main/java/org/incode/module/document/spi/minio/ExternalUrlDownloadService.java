package org.incode.module.document.spi.minio;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.services.message.MessageService2;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.applib.value.Clob;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.minio.miniodownloader.MinioDownloadClient;

@DomainService(
        nature = NatureOfService.DOMAIN,
        objectType = "incodeDocuments.ExternalUrlDownloadService"
)
public class ExternalUrlDownloadService {

    private MinioDownloadClient minioDownloadClient;

    @PostConstruct
    public void init(final Map<String,String> properties) {
        minioDownloadClient = new MinioDownloadClient();
        minioDownloadClient.setUrl(read(properties, "estatio.minio.url", "http://minio.int.ecpnv.com:9000"));
        minioDownloadClient.setAccessKey(read(properties, "estatio.minio.accessKey", "minio"));
        minioDownloadClient.setSecretKey(read(properties, "estatio.minio.secretKey", "minio123"));
        minioDownloadClient.setBackoffNumAttempts(readInt(properties, "estatio.minio.backoffNumAttempts", 5));
        minioDownloadClient.setBackoffSleepMillis(readInt(properties, "estatio.minio.backoffSleepMillis", 200));

        minioDownloadClient.init();
    }

    private static String read(final Map<String, String> properties, final String key, final String fallback) {
        final String value = properties.get(key);
        return value != null ? value : fallback;
    }

    private static int readInt(
            final Map<String, String> properties, final String key, final int fallback) {
        final String value = properties.get(key);
        return value != null ? Integer.parseInt(value) : fallback;
    }

    @Programmatic
    public Blob downloadAsBlob(final Document document) {
        final String externalUrl = document.getExternalUrl();
        final String documentName = document.getName();
        return downloadAsBlob(documentName, externalUrl);
    }

    @Programmatic
    public Blob downloadAsBlob(final String documentName, final String externalUrl) {

        try {
            return minioDownloadClient.downloadBlob(documentName, externalUrl);

        } catch (Exception e) {
            messageService.warnUser(
                    TranslatableString.tr(
                            "Could not download blob from external URL: {url}",
                            "url", externalUrl),
                    ExternalUrlDownloadService.class, "download");
            return null;
        }
    }

    @Programmatic
    public Clob downloadAsClob(final Document document) {
        final String externalUrl = document.getExternalUrl();
        final String documentName = document.getName();
        return downloadAsClob(documentName, externalUrl);
    }

    @Programmatic
    public Clob downloadAsClob(final String documentName, final String externalUrl) {
        try {
            return minioDownloadClient.downloadClob(documentName, externalUrl);
        } catch (Exception e) {
            messageService.warnUser(
                    TranslatableString.tr(
                            "Could not download clob from external URL: {url}",
                            "url", externalUrl),
                    ExternalUrlDownloadService.class, "downloadExternalUrlAsClob");
            return null;
        }
    }

    @Inject
    MessageService2 messageService;

}

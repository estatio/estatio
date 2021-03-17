package org.incode.platform.dom.document.integtests.dom.document.dom.spiimpl;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.applib.value.Clob;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.spi.minio.ExternalUrlDownloadService;

@DomainService(
    nature = NatureOfService.DOMAIN,
    objectType = "incodeDocuments.ExternalUrlDownloadServiceForDemo",
    menuOrder = "100"
)
public class ExternalUrlDownloadServiceForDemo extends ExternalUrlDownloadService {

    @Override
    public Blob downloadAsBlob(final Document document) {
        return document.getBlob();
    }

    @Override
    public Clob downloadAsClob(final Document document) {
        return document.getClob();
    }
}

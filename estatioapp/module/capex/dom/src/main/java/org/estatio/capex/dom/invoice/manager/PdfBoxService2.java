package org.estatio.capex.dom.invoice.manager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

// TODO: to add into PdfBox service in the future...
@DomainService(nature = NatureOfService.DOMAIN)
public class PdfBoxService2 {

    @Programmatic
    public byte[] merge(final List<File> fileList) throws IOException {

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        final PDFMergerUtility ut = new PDFMergerUtility();
        for (File file : fileList) {
            ut.addSource(file);
        }

        ut.setDestinationStream(baos);
        ut.mergeDocuments(MemoryUsageSetting.setupTempFileOnly());

        return baos.toByteArray();
    }

    @Programmatic
    public byte[] merge(final File... files) throws IOException {
        return merge(Arrays.asList(files));
    }

}

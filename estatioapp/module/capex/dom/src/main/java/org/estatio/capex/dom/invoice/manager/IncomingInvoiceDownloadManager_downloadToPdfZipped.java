package org.estatio.capex.dom.invoice.manager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.google.common.io.Files;

import org.apache.isis.applib.FatalException;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Blob;

import lombok.Data;

@Mixin(method="act")
public class IncomingInvoiceDownloadManager_downloadToPdfZipped extends IncomingInvoiceDownloadManager_downloadToPdfAbstract {

    public IncomingInvoiceDownloadManager_downloadToPdfZipped(final IncomingInvoiceDownloadManager manager) {
        super(manager);
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(named = "Download all PDFs (zipped)")
    public Blob act(final String fileName) throws IOException {

        final List<DocumentPreparer> preparers = documentPreparersForInvoices();
        final List<ZipService.FileAndName> fileList = fileAndNamesFrom(preparers);

        final byte[] zipBytes = zipService.zip(fileList);

        preparers.forEach(DocumentPreparer::cleanup);

        return new Blob(fileName, "application/zip", zipBytes);
    }

    @Override
    public String disableAct() {
        return super.disableAct();
    }

    public String default0Act() {
        return manager.defaultFileNameWithSuffix(".zip");
    }



    private List<ZipService.FileAndName> fileAndNamesFrom(final List<DocumentPreparer> preparers) {
        return preparers.stream()
                .map(preparer -> new ZipService.FileAndName(preparer.getDocumentName(), preparer.stampUsing(pdfManipulator).getTempFile()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    @javax.inject.Inject
    ZipService zipService;


    // TODO: create a new incode-platform library for this in the future
    @DomainService(nature = NatureOfService.DOMAIN)
    public static class ZipService {

        @Data
        public static class FileAndName {
            private final String name;
            private final File file;
        }

        /**
         * Rather than use the name of the file (which might be temporary files, for example)
         * we explicitly provide the name to use (in the ZipEntry).
         *
         */
        @Programmatic
        public byte[] zip(final List<FileAndName> fileAndNameList) throws IOException {

            final byte[] bytes;
            try {
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                final ZipOutputStream zos = new ZipOutputStream(baos);

                for (FileAndName fan : fileAndNameList) {
                    zos.putNextEntry(new ZipEntry(fan.getName()));
                    zos.write(Files.toByteArray(fan.getFile()));
                    zos.closeEntry();
                }
                zos.close();
                bytes = baos.toByteArray();
            } catch (final IOException ex) {
                throw new FatalException("Unable to create zip of layouts", ex);
            }
            return bytes;
        }

        /**
         * As per {@link #zip(List)}, but using each file's name as the zip entry (rather than providing it).
         */
        @Programmatic
        public byte[] zipFiles(final List<File> fileList) throws IOException {
            return zip(fileList.stream()
                               .map(file -> new FileAndName(file.getName(), file))
                               .collect(Collectors.toList())
                    );
        }
    }

}

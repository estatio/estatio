package org.estatio.capex.dom.invoice.manager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.assertj.core.util.Lists;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Blob;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentAbstract;

import org.estatio.capex.dom.documents.LookupAttachedPdfService;
import org.estatio.capex.dom.pdfmanipulator.ExtractSpec;
import org.estatio.capex.dom.pdfmanipulator.PdfManipulator;
import org.estatio.capex.dom.pdfmanipulator.Stamp;

@Mixin(method="act")
public class IncomingInvoiceDownloadManager_downloadToPdf {

    public static class DownloadException extends RuntimeException {
        public DownloadException(final String message, final Throwable cause) {
            super(message, cause);
        }
    }


    private final IncomingInvoiceDownloadManager manager;
    public IncomingInvoiceDownloadManager_downloadToPdf(final IncomingInvoiceDownloadManager manager) {
        this.manager = manager;
    }

    @Action(semantics = SemanticsOf.SAFE)
    public Blob act(
            final String fileName,
            @ParameterLayout(named = "How many first pages of each invoice's PDF?")
            final Integer numFirstPages,
            @ParameterLayout(named = "How many final pages of each invoice's PDF?")
            final Integer numLastPages) throws IOException {

        final List<DocumentPreparer> preparers = manager.getInvoices().stream()
                .map(incomingInvoice -> lookupAttachedPdfService.lookupIncomingInvoicePdfFrom(incomingInvoice))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .sorted(Comparator.comparing(DocumentAbstract::getName))
                .map(document -> new DocumentPreparer(document, numFirstPages, numLastPages))
                .collect(Collectors.toList());

        final List<File> fileList = preparers.stream()
                .map(preparer -> preparer.stampUsing(pdfManipulator).getTempFile())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        final byte[] singlePdf = pdfBoxService2.merge(fileList);

        preparers.forEach(DocumentPreparer::cleanup);

        return new Blob(fileName, "application/pdf", singlePdf);
    }
    public String default0Act() {
        return manager.defaultFileNameWithSuffix(".pdf");
    }
    public Integer default1Act() {
        return 3;
    }
    public List<Integer> choices1Act() {
        return Lists.newArrayList(1,2,3,4,5,10,20,50);
    }
    public Integer default2Act() {
        return 1;
    }
    public List<Integer> choices2Act() {
        return Lists.newArrayList(0,1,2,3,4,5,10,20,50);
    }

    @javax.inject.Inject
    private PdfManipulator pdfManipulator;

    @javax.inject.Inject
    private PdfBoxService2 pdfBoxService2;

    @javax.inject.Inject
    private LookupAttachedPdfService lookupAttachedPdfService;



    static class DocumentPreparer {

        private final Document document;
        private final int numFirstPages;
        private final int numLastPages;

        File tempFile;

        DocumentPreparer(final Document document, final int numFirstPages, final int numLastPages) {
            this.document = document;
            this.numFirstPages = numFirstPages;
            this.numLastPages = numLastPages;
        }

        DocumentPreparer stampUsing(final PdfManipulator pdfManipulator) {
            try {
                final String documentName = document.getName();

                final List<String> leftLineTexts = Lists.newArrayList(documentName);
                final List<String> rightLineTexts = Collections.emptyList();
                final String hyperlink = null;

                byte[] bytes = pdfManipulator.extractAndStamp(document.getBlobBytes(),
                        new ExtractSpec(numFirstPages, numLastPages),
                        new Stamp(leftLineTexts, rightLineTexts, hyperlink));

                tempFile = File.createTempFile(documentName, "pdf");

                final FileOutputStream fos = new FileOutputStream(tempFile);
                fos.write(bytes);
                fos.close();

                return this;
            } catch (IOException e) {
                throw new DownloadException("Failed to prepare: " + document.getName(), e);
            }
        }

        File getTempFile() {
            return tempFile;
        }

        void cleanup() {
            if (tempFile != null) {
                try {
                    Files.delete(tempFile.toPath());
                    tempFile.delete();
                } catch (Exception e) {
                    // ignore
                }
            }
        }
    }


    // TODO: to add into PdfBox service in the future...
    @DomainService(nature = NatureOfService.DOMAIN)
    public static class PdfBoxService2 {

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
}

package org.estatio.capex.dom.invoice.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.assertj.core.util.Lists;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentAbstract;

import org.estatio.capex.dom.documents.LookupAttachedPdfService;
import org.estatio.capex.dom.pdfmanipulator.ExtractSpec;
import org.estatio.capex.dom.pdfmanipulator.PdfManipulator;
import org.estatio.capex.dom.pdfmanipulator.Stamp;

public abstract class IncomingInvoiceDownloadManager_downloadToPdfAbstract
        extends IncomingInvoiceDownloadManager_downloadAbstract {

    public static class DownloadException extends RuntimeException {
        public DownloadException(final String message, final Throwable cause) {
            super(message, cause);
        }
    }

    public IncomingInvoiceDownloadManager_downloadToPdfAbstract(final IncomingInvoiceDownloadManager manager) {
        super(manager);
    }

    protected List<DocumentPreparer> documentPreparersForInvoices() {
        return documentPreparersForInvoices(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    protected List<DocumentPreparer> documentPreparersForInvoices(
            Integer numFirstPages,
            Integer numLastPages) {
        return manager.getInvoices().stream()
                    .map(incomingInvoice -> lookupAttachedPdfService.lookupIncomingInvoicePdfFrom(incomingInvoice))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .sorted(Comparator.comparing(DocumentAbstract::getName))
                    .map(document -> new DocumentPreparer(document, numFirstPages, numLastPages))
                    .collect(Collectors.toList());
    }



    @javax.inject.Inject
    PdfManipulator pdfManipulator;

    @javax.inject.Inject
    LookupAttachedPdfService lookupAttachedPdfService;

    /**
     * Wraps a document and is able (using a provided {@link PdfManipulator} object) to extract a selection of pages
     * from each document and to stamp each page with additional info.
     */
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

        String getDocumentName() {
            return document.getName();
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
}

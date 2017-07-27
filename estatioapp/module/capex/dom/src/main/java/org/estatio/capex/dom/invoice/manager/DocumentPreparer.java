package org.estatio.capex.dom.invoice.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

import org.assertj.core.util.Lists;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.pdfmanipulator.ExtractSpec;
import org.estatio.capex.dom.pdfmanipulator.PdfManipulator;
import org.estatio.capex.dom.pdfmanipulator.Stamp;

class DocumentPreparer {

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
            throw new IncomingInvoiceDownloadManager.DownloadException("Failed to prepare: " + document.getName(), e);
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

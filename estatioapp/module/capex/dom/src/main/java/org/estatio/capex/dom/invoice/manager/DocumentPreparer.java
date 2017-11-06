package org.estatio.capex.dom.invoice.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.List;

import org.assertj.core.util.Lists;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.capex.dom.pdfmanipulator.ExtractSpec;
import org.estatio.capex.dom.pdfmanipulator.PdfManipulator;
import org.estatio.capex.dom.pdfmanipulator.Stamp;

/**
 * Wraps a document and is able (using a provided {@link PdfManipulator} object) to extract a selection of pages
 * from each document and to stamp each page with additional info.
 */
class DocumentPreparer {

    private final IncomingInvoice incomingInvoice;

    private final IncomingInvoiceApprovalStateTransition transitionIfAny;

    private final Document document;
    private final int numFirstPages;
    private final int numLastPages;

    File tempFile;

    DocumentPreparer(
            final IncomingInvoice incomingInvoice,
            final IncomingInvoiceApprovalStateTransition transitionIfAny,
            final Document document,
            final int numFirstPages,
            final int numLastPages) {
        this.incomingInvoice = incomingInvoice;
        this.transitionIfAny = transitionIfAny;
        this.document = document;
        this.numFirstPages = numFirstPages;
        this.numLastPages = numLastPages;
    }

    String getDocumentName() {
        return document != null ? document.getName() : null;
    }

    DocumentPreparer stampUsing(final PdfManipulator pdfManipulator) {
        try {
            final String documentName = document.getName();

            final List<String> leftLineTexts = Lists.newArrayList();

            leftLineTexts.add(documentName);
            if(transitionIfAny != null) {
                final String completedBy = transitionIfAny.getCompletedBy();
                leftLineTexts.add(String.format(
                        "approved by: %s",
                        completedBy != null ? completedBy : "(unknown)"));
                leftLineTexts.add("approved on: " + transitionIfAny.getCompletedOn().toString("dd-MMM-yyyy HH:mm"));
            } else {
                leftLineTexts.add("not yet approved");
            }

            final List<String> rightLineTexts = Lists.newArrayList();
            rightLineTexts.add(String.format("net Amt       : %s", new DecimalFormat("0.00").format(incomingInvoice.getNetAmount())));
            rightLineTexts.add(String.format("gross Amt     : %s", new DecimalFormat("0.00").format(incomingInvoice.getGrossAmount())));

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

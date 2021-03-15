package org.estatio.module.capex.app.invoicedownload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.List;

import com.google.common.collect.Lists;

import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.message.MessageService2;
import org.apache.isis.applib.value.Blob;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.minio.Document_cacheBlob;
import org.incode.module.document.spi.minio.ExternalUrlDownloadService;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.module.capex.platform.pdfmanipulator.ExtractSpec;
import org.estatio.module.capex.platform.pdfmanipulator.PdfManipulator;
import org.estatio.module.capex.platform.pdfmanipulator.Stamp;
import org.joda.time.LocalDateTime;

import javax.inject.Inject;

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

    DocumentPreparer stampUsing(
            final PdfManipulator pdfManipulator,
            final ExternalUrlDownloadService externalUrlDownloadService,
            final MessageService2 messageService) {
        try {
            final String documentName = document.getName();

            final List<String> leftLineTexts = Lists.newArrayList();

            leftLineTexts.add(documentName);
            if (transitionIfAny != null) {
                final String completedBy = transitionIfAny.getCompletedBy();
                leftLineTexts.add(String.format(
                        "approved by: %s",
                        completedBy != null ? completedBy : "(unknown)"));
                final LocalDateTime completedOn = transitionIfAny.getCompletedOn();
                leftLineTexts.add(String.format(
                        "approved on: %s",
                        completedOn != null ? completedOn.toString("dd-MMM-yyyy HH:mm") : "(unknown)"));
            } else {
                leftLineTexts.add("not yet approved");
            }

            final List<String> rightLineTexts = Lists.newArrayList();
            rightLineTexts.add(String.format("net Amt       : %s", new DecimalFormat("0.00").format(incomingInvoice.getNetAmount())));
            rightLineTexts.add(String.format("gross Amt     : %s", new DecimalFormat("0.00").format(incomingInvoice.getGrossAmount())));

            final String hyperlink = null;

            byte[] blobBytes = document.getBlobBytes();
            if (blobBytes == null) {
                final Blob archived = externalUrlDownloadService.downloadAsBlob(document);
                if (archived != null) {
                    blobBytes = archived.getBytes();
                } else {
                    messageService.raiseError(String.format("Failed to load bytes: Invoice %s has no cached bytes and unable to locate archived document", incomingInvoice.title()));
                }
            }

            byte[] bytes = pdfManipulator.extractAndStamp(blobBytes,
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

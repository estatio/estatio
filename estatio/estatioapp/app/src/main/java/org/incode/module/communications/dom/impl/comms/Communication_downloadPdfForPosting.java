package org.incode.module.communications.dom.impl.comms;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.pdfbox.dom.service.PdfBoxService;

import org.incode.module.base.dom.MimeTypeData;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelType;
import org.incode.module.communications.dom.mixins.DocumentConstants;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.minio.Document_downloadExternalUrlAsBlob;

@Mixin(method = "act")
public class Communication_downloadPdfForPosting {

    private final Communication communication;

    public Communication_downloadPdfForPosting(final Communication communication) {
        this.communication = communication;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(
            named = "Download PDF for posting",
            cssClassFa = "download"
    )
    public Blob act(
            @ParameterLayout(named = "File name")
            final String fileName) throws IOException {

        // the act of downloading implicitly sends the communication
        if(communication.getState() == CommunicationState.PENDING) {
            communication.sent();
        }

        final List<byte[]> pdfBytes = Lists.newArrayList();

        final Document primaryDoc = communication.getPrimaryDocument();
        appendBytes(primaryDoc, pdfBytes);

        // merge any and all attachments
        final List<Document> attachedDocuments = findAttachedPdfDocuments();
        attachedDocuments.sort(Ordering.natural().onResultOf(Document::getCreatedAt));
        for (final Document attachedDoc : attachedDocuments) {
            appendBytes(attachedDoc, pdfBytes);
        }

        final byte[] mergedBytes = pdfBoxService.merge(pdfBytes.toArray(new byte[][] {}));

        return new Blob(fileName, MimeTypeData.APPLICATION_PDF.asStr(), mergedBytes);
    }

    public boolean hideAct() {
        if(communication.getType() != CommunicationChannelType.POSTAL_ADDRESS) {
            return true;
        }
        return false;
    }

    public String disableAct() {
        Document primaryDocument = communication.getPrimaryDocument();
        return primaryDocument == null
                ? "Cannot locate the primary document for this communication"
                : null;
    }

    public String default0Act() {
        Document primaryDocument = communication.getPrimaryDocument();
        return primaryDocument != null ? primaryDocument.getName() : null;
    }

    private List<Document> findAttachedPdfDocuments() {
        return communication.findDocuments(
                DocumentConstants.PAPERCLIP_ROLE_ATTACHMENT,
                MimeTypeData.APPLICATION_PDF.asStr());
    }

    private Blob asBlob(final Document document) {
        switch (document.getSort()) {
        case BLOB:
            final Blob blob = document.getBlob();
            return blob;
        case EXTERNAL_BLOB:
            return factoryService.mixin(Document_downloadExternalUrlAsBlob.class, document).act();
        }
        return null;
    }


    private void appendBytes(final Document document, final List<byte[]> pdfBytes) {
        final Blob blob = asBlob(document);
        final byte[] bytes = blob.getBytes();
        pdfBytes.add(bytes);
    }

    @Inject
    FactoryService factoryService;
    @Inject
    PdfBoxService pdfBoxService;




}

package org.estatio.module.capex.contributions;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.value.Blob;

import org.incode.module.base.dom.MimeTypeData;
import org.incode.module.docrendering.gotenberg.dom.impl.GotenbergClientService;
import org.incode.module.document.dom.api.DocumentService;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;

import org.estatio.module.capex.dom.order.Order;

@Mixin(method = "act")
public class Order_uploadFinal {

    private final Order order;

    public Order_uploadFinal(final Order order) {
        this.order = order;
    }

    @Action(semantics = SemanticsOf.SAFE)
    public Order act(
            final DocumentTemplate documentTemplate,
            @Parameter(fileAccept = MimeTypeData.Str.APPLICATION_DOCX)
            final Blob finalDocument) {

        final Document document = documentService.createDocumentAndAttachPaperclips(order, documentTemplate);

        final byte[] pdfBytes = gotenbergClientService.convertToPdf(finalDocument.getBytes());
        final Blob pdfBlob = MimeTypeData.APPLICATION_PDF.newBlob(finalDocument.getName(), pdfBytes);
        document.modifyBlob(pdfBlob);

        return order;
    }

    public boolean hideAct() {
        return default0Act() == null;
    }
    public DocumentTemplate default0Act() {
        return choices0Act().isEmpty() ? null : choices0Act().get(0);
    }
    public List<DocumentTemplate> choices0Act() {
        Order_previewDocument previewDocument = factoryService.mixin(Order_previewDocument.class, order);
        return previewDocument.choices0Act()
                .stream()
                .filter(MimeTypeData.APPLICATION_DOCX::matches)
                .collect(Collectors.toList());
    }

    @Inject
    DocumentService documentService;

    @Inject
    GotenbergClientService gotenbergClientService;

    @Inject
    FactoryService factoryService;
}

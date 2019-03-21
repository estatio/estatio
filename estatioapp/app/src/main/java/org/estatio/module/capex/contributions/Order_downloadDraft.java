package org.estatio.module.capex.contributions;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.value.Blob;

import org.incode.module.base.dom.MimeTypeData;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;

import org.estatio.module.capex.dom.order.Order;

@Mixin(method = "act")
public class Order_downloadDraft {

    private final Order order;

    public Order_downloadDraft(final Order order) {
        this.order = order;
    }

    @Action(semantics = SemanticsOf.SAFE)
    public Blob act(final DocumentTemplate documentTemplate) {
        Order_previewDocument previewDocument = factoryService.mixin(Order_previewDocument.class, order);
        final DocumentPreviewForOrder preview =
                previewDocument.createPreview(documentTemplate);
        return preview.getBlob();
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
    FactoryService factoryService;
}

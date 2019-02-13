package org.estatio.module.capex.contributions;

import java.io.IOException;

import org.apache.isis.applib.annotation.Mixin;

import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.module.document.dom.mixins.T_preview;

import org.estatio.module.capex.dom.order.Order;

@Mixin(method = "act")
public class Order_previewDocument extends T_preview<Order, DocumentPreviewForOrder> {

    public Order_previewDocument(final Order order) {
        super(order);
    }

    @Override
    public DocumentPreviewForOrder act(final DocumentTemplate template) throws IOException {
        return super.act(template);
    }

    @Override
    protected DocumentPreviewForOrder newPreview() {
        return new DocumentPreviewForOrder();
    }
}

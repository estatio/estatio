package org.estatio.module.capex.contributions;

import java.io.IOException;
import java.net.URL;

import org.apache.isis.applib.annotation.Mixin;

import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.module.document.dom.mixins.T_preview;

import org.estatio.module.capex.dom.order.Order;

/**
 * TODO: this can probably be removed, replaced by {@link Order_downloadDraft} and {@link Order_uploadFinal}.
 */
@Mixin(method = "act")
public class Order_previewDocument extends T_preview<Order, DocumentPreviewForOrder> {

    public Order_previewDocument(final Order order) {
        super(order);
    }

    @Override
    protected DocumentPreviewForOrder newPreview() {
        return new DocumentPreviewForOrder();
    }

    @Override
    public URL act(final DocumentTemplate template) throws IOException {
        return super.act(template);
    }

    /**
     * TODO: this is hidden because it may no longer be needed.
     *   If do decide to reinstate, then we'll need a better implementation of the
     *   {@link org.estatio.module.base.spiimpl.urlencoding.UrlEncodingUsingBaseEncodingSupportLargeUrls}
     *   service to avoid memory leaks.
     */
    public boolean hideAct() {
        return true;
    }
}

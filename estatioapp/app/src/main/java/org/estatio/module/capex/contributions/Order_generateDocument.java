package org.estatio.module.capex.contributions;

import java.io.IOException;

import org.apache.isis.applib.annotation.Mixin;

import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.module.document.dom.mixins.T_createAndAttachDocumentAndRender;

import org.estatio.module.capex.dom.order.Order;

@Mixin(method = "act")
public class Order_generateDocument extends T_createAndAttachDocumentAndRender<Order> {

    public Order_generateDocument(final Order order) {
        super(order);
    }

    public Object act(DocumentTemplate template) throws IOException {
        super.act(template);
        return domainObject;
    }

}

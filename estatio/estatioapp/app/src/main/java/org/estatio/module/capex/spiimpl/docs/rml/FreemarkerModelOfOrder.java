package org.estatio.module.capex.spiimpl.docs.rml;

import org.incode.module.document.dom.impl.applicability.RendererModelFactoryAbstract;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;

import org.estatio.module.capex.dom.order.Order;

import lombok.Value;

public class FreemarkerModelOfOrder extends RendererModelFactoryAbstract<Order> {

    public FreemarkerModelOfOrder() {
        super(Order.class);
    }

    @Override
    protected Object doNewRendererModel(
            final DocumentTemplate documentTemplate, final Order demoObject) {
        return new DataModel(demoObject);
    }

    @Value
    public static class DataModel {
        Order order;
    }

}


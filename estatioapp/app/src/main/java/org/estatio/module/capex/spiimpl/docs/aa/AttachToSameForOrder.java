package org.estatio.module.capex.spiimpl.docs.aa;

import java.util.Collections;
import java.util.List;

import org.incode.module.document.dom.impl.applicability.AttachmentAdvisorAbstract;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;

import org.estatio.module.capex.dom.order.Order;

public class AttachToSameForOrder extends AttachmentAdvisorAbstract<Order> {

    public AttachToSameForOrder() {
        super(Order.class);
    }

    @Override
    protected List<PaperclipSpec> doAdvise(
            final DocumentTemplate documentTemplate,
            final Order order,
            final Document createdDocument) {
        return Collections.singletonList(new PaperclipSpec(null, order, createdDocument));
    }

}


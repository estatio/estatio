package org.estatio.dom.invoice;

import java.util.List;
import java.util.SortedSet;

import org.apache.isis.applib.Identifier;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;
import org.apache.isis.applib.services.publish.EventPayloadForActionInvocation;

public class InvoiceAndInvoiceItemPayload extends EventPayloadForActionInvocation<Invoice> {

    public InvoiceAndInvoiceItemPayload(final Identifier actionIdentifier, final Invoice target, final List<? extends Object> arguments, final Object result) {
        super(actionIdentifier, target, arguments, result);
    }

    
    @Override
    @Render(Type.EAGERLY)
    public Invoice getTarget() {
        return super.getTarget();
    }
    
    @Render(Type.EAGERLY)
    public SortedSet<InvoiceItem> getItems() {
        return getTarget().getItems();
    }

}

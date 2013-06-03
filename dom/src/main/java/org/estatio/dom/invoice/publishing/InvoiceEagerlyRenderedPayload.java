package org.estatio.dom.invoice.publishing;

import java.util.List;

import org.apache.isis.applib.Identifier;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;
import org.apache.isis.applib.services.publish.EventPayloadForActionInvocation;

import org.estatio.dom.invoice.Invoice;

public class InvoiceEagerlyRenderedPayload extends EventPayloadForActionInvocation<Invoice> {

    public InvoiceEagerlyRenderedPayload(final Identifier actionIdentifier, final Invoice target, final List<? extends Object> arguments, final Object result) {
        super(actionIdentifier, target, arguments, result);
    }

    @Override
    @Render(Type.EAGERLY)
    public Invoice getTarget() {
        return super.getTarget();
    }
    
}

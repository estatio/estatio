package org.estatio.dom.invoice.publishing;

import java.util.List;

import org.apache.isis.applib.Identifier;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.PublishedAction.PayloadFactory;
import org.apache.isis.applib.services.publish.EventPayload;

import org.estatio.dom.invoice.Invoice;

public class InvoiceEagerlyRenderedPayloadFactory implements PayloadFactory {

    @Override
    @Programmatic
    public EventPayload payloadFor(Identifier actionIdentifier, Object target, List<Object> arguments, Object result) {
        return new InvoiceEagerlyRenderedPayload(actionIdentifier, (Invoice)target, arguments, result);
    }

}

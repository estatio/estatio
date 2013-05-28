package org.estatio.dom.invoice;

import java.util.List;

import org.apache.isis.applib.Identifier;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.PublishedAction.PayloadFactory;
import org.apache.isis.applib.services.publish.EventPayload;

public class InvoiceEagerlyRenderedPayloadFactory implements PayloadFactory {

    @Override
    @Programmatic
    public EventPayload payloadFor(Identifier actionIdentifier, Object target, List<Object> arguments, Object result) {
        return new InvoiceEagerlyRenderedPayload(actionIdentifier, (Invoice)target, arguments, result);
    }

}

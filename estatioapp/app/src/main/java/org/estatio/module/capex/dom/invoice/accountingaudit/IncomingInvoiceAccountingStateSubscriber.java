package org.estatio.module.capex.dom.invoice.accountingaudit;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;

@DomainService(nature = NatureOfService.DOMAIN)
public class IncomingInvoiceAccountingStateSubscriber extends AbstractSubscriber {

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void on(IncomingInvoice.ObjectPersistingEvent ev) {
        final IncomingInvoice incomingInvoice = ev.getSource();
        if (incomingInvoice.getAccountingState() == null) {
            incomingInvoice.setAccountingState(IncomingInvoiceAccountingStateTransitionType.INSTANTIATE.getToState());
        }
    }

}

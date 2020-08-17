package org.estatio.module.capex.subscriptions;

import javax.inject.Inject;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.clock.ClockService;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.invoice.accountingaudit.triggers.IncomingInvoice_audit;
import org.estatio.module.capex.dom.invoice.accountingaudit.triggers.IncomingInvoice_reAudit;
import org.estatio.module.invoice.dom.InvoiceItem;

@DomainService(
        nature = NatureOfService.DOMAIN,
        objectType = "capex.subscriptions.IncomingInvoiceAuditedSubscriber"
)
public class IncomingInvoiceAuditedSubscriber extends AbstractSubscriber {

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void reportInvoiceWhenAudited(final IncomingInvoice_audit.ActionDomainEvent ev) {
        switch (ev.getEventPhase()) {
        case EXECUTED:
            if (ev.getSource()==null) return;
            final IncomingInvoice invoice = ev.getSource().getDomainObject();
            if (invoice==null) return;
            reportInvoiceItemsIfNotAlready(invoice);
        }
    }

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void reportInvoiceWhenReAudited(final IncomingInvoice_reAudit.ActionDomainEvent ev) {
        switch (ev.getEventPhase()) {
        case EXECUTED:
            if (ev.getSource()==null) return;
            final IncomingInvoice invoice = ev.getSource().getDomainObject();
            if (invoice==null) return;
            reportInvoiceItemsIfNotAlready(invoice);
        }
    }

    private void reportInvoiceItemsIfNotAlready(final IncomingInvoice incomingInvoice){
        for (InvoiceItem item : incomingInvoice.getItems()){
            IncomingInvoiceItem castedItem = (IncomingInvoiceItem) item;
            if (castedItem.getReportedDate()==null){
                castedItem.setReportedDate(clockService.now());
            }
        }
    }

    @Inject ClockService clockService;

}

package org.estatio.module.capex.subscriptions;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;

@DomainService(
        nature = NatureOfService.DOMAIN,
        objectType = "capex.subscriptions.IncomingInvoiceAttachPaperclipSubscriber"
)
public class IncomingInvoiceAttachPaperclipSubscriber extends AbstractSubscriber {

    /**
     * This method tries to update {@Link IncomingInvoice#barcode} when a document is attached
     * @param ev
     */
    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void updateBarcodeOnIncomingInvoice(final PaperclipRepository.PaperclipAttachDomainEvent ev) {
        switch (ev.getEventPhase()) {
        case EXECUTED:
            final Paperclip paperclip = ev.getSource();
            if (paperclip.getAttachedTo().getClass().isAssignableFrom(IncomingInvoice.class)){
                IncomingInvoice invoice = (IncomingInvoice) paperclip.getAttachedTo();
                final String barcode = deriveBarcodeFromDocumentName(paperclip.getDocument().getName());
                invoice.setBarcode(barcode);
            }
        }
    }

    String deriveBarcodeFromDocumentName(final String documentName){
        return documentName.replaceAll("(?i)\\.pdf", "");
    }
}

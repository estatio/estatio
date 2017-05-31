package org.estatio.capex.dom.documents.categorisation;

import javax.inject.Inject;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.documents.DocumentMenu;
import org.estatio.capex.dom.state.StateTransitionService;

@DomainService(nature = NatureOfService.DOMAIN)
public class IncomingDocumentCategorisationStateSubscriber extends AbstractSubscriber {

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void toInstantiateWhen(DocumentMenu.UploadDomainEvent ev) {
        switch (ev.getEventPhase()) {
        case EXECUTED:
            final Document document = (Document) ev.getReturnValue();
            stateTransitionService.trigger(
                    document, IncomingDocumentCategorisationStateTransitionType.INSTANTIATE, null);
            break;
        }
    }


//    // these now subclass from DomainObject_triggerAbstract ?

//    @Programmatic
//    @com.google.common.eventbus.Subscribe
//    @org.axonframework.eventhandling.annotation.EventHandler
//    public void toAssociateWithDomainEntityWhen(IncomingInvoiceViewmodel_saveInvoice.DomainEvent ev) {
//        switch (ev.getEventPhase()) {
//        case EXECUTED:
//            final IncomingInvoiceViewModel viewModel = (IncomingInvoiceViewModel) ev.getMixedIn();
//            final Document document = viewModel.getDocument();
//            stateTransitionService.trigger(document, CLASSIFY_AS_INVOICE_OR_ORDER, null);
//        }
//    }
//
//    @Programmatic
//    @com.google.common.eventbus.Subscribe
//    @org.axonframework.eventhandling.annotation.EventHandler
//    public void toAssociateWithDomainEntityWhen(IncomingOrderViewmodel_saveOrder.DomainEvent ev) {
//        switch (ev.getEventPhase()) {
//        case EXECUTED:
//            final IncomingOrderViewModel viewModel = (IncomingOrderViewModel) ev.getMixedIn();
//            final Document document = viewModel.getDocument();
//            stateTransitionService.trigger(document, CLASSIFY_AS_INVOICE_OR_ORDER, null);
//        }
//    }
//
//    @Inject
//    IncomingDocumentCategorisationStateTransition.Repository repository;

    @Inject
    StateTransitionService stateTransitionService;


}

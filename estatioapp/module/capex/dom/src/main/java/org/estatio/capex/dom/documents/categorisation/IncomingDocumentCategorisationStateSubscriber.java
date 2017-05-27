package org.estatio.capex.dom.documents.categorisation;

import javax.inject.Inject;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.documents.DocumentMenu;
import org.estatio.capex.dom.documents.HasDocument;
import org.estatio.capex.dom.documents.HasDocumentAbstract;
import org.estatio.capex.dom.documents.HasDocumentAbstract_categoriseAsInvoice;
import org.estatio.capex.dom.documents.HasDocumentAbstract_resetCategorisation;
import org.estatio.capex.dom.documents.invoice.IncomingInvoiceViewModel;
import org.estatio.capex.dom.documents.invoice.IncomingInvoiceViewmodel_saveInvoice;
import org.estatio.capex.dom.documents.order.IncomingOrderViewModel;
import org.estatio.capex.dom.documents.order.IncomingOrderViewmodel_saveOrder;
import org.estatio.capex.dom.state.StateTransitionService;

import static org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType.ASSOCIATE_WITH_DOMAIN_ENTITY;
import static org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType.CATEGORISE_DOCUMENT_TYPE_AND_ASSOCIATE_WITH_PROPERTY;

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

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void toCategoriseDocumentTypeWhen(HasDocumentAbstract_categoriseAsInvoice.DomainEvent ev) {
        switch (ev.getEventPhase()) {
        case EXECUTED:
            final HasDocumentAbstract hasDocument = (HasDocumentAbstract) ev.getMixedIn();
            final Document document = hasDocument.getDocument();
            stateTransitionService.trigger(document, CATEGORISE_DOCUMENT_TYPE_AND_ASSOCIATE_WITH_PROPERTY, null);
        }
    }

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void toAssociateWithDomainEntityWhen(IncomingInvoiceViewmodel_saveInvoice.ActionDomainEvent ev) {
        switch (ev.getEventPhase()) {
        case EXECUTED:
            final IncomingInvoiceViewModel viewModel = (IncomingInvoiceViewModel) ev.getMixedIn();
            final Document document = viewModel.getDocument();
            stateTransitionService.trigger(document, ASSOCIATE_WITH_DOMAIN_ENTITY, null);
        }
    }

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void toAssociateWithDomainEntityWhen(IncomingOrderViewmodel_saveOrder.DomainEvent ev) {
        switch (ev.getEventPhase()) {
        case EXECUTED:
            final IncomingOrderViewModel viewModel = (IncomingOrderViewModel) ev.getMixedIn();
            final Document document = viewModel.getDocument();
            stateTransitionService.trigger(document, ASSOCIATE_WITH_DOMAIN_ENTITY, null);
        }
    }

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void toResetWhen(HasDocumentAbstract_resetCategorisation.DomainEvent ev) {
        switch (ev.getEventPhase()) {
        case EXECUTED:
            final HasDocument hasDocument = (HasDocument) ev.getMixedIn();
            final Document document = hasDocument.getDocument();
            stateTransitionService.trigger(document, IncomingDocumentCategorisationStateTransitionType.RESET, null);
        }
    }


    @Inject
    IncomingDocumentCategorisationStateTransition.Repository repository;

    @Inject
    StateTransitionService stateTransitionService;


}

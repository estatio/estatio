package org.estatio.capex.dom.order.viewmodel;

import java.util.Optional;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.documents.LookupAttachedPdfService;
import org.estatio.capex.dom.order.Order;

/**
 * REVIEW: this could be inlined as a mixin, however would result in: domain layer -> app layer  ??
 */
@Mixin(method = "act")
public class Order_switchView {

    private final Order order;

    public Order_switchView(Order order) {
        this.order = order;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(
            contributed = Contributed.AS_ACTION,
            cssClassFa = "fa-exchange" // not sure why this isn't being picked up from isis-non-changing.properties
    )
    @MemberOrder(sequence = "1")
    public IncomingDocAsOrderViewModel act() {
        Optional<Document> documentIfAny = lookupAttachedPdfService.lookupOrderPdfFrom(order);
        Document document = documentIfAny.get();
        final IncomingDocAsOrderViewModel viewModel = new IncomingDocAsOrderViewModel(order, document);
        serviceRegistry2.injectServicesInto(viewModel);
        viewModel.init();
        return viewModel;
    }

    public boolean hideAct() {
        Optional<Document> documentIfAny = lookupAttachedPdfService.lookupOrderPdfFrom(order);
        return !documentIfAny.isPresent();
    }

    public String disableAct() {
        switch (order.getItems().size()) {
        case 0:
        case 1:
            return null;
        default:
            return "Can only switch view for orders with a single item";
        }
    }

    @Inject
    LookupAttachedPdfService lookupAttachedPdfService;

    @Inject
    ServiceRegistry2 serviceRegistry2;
}

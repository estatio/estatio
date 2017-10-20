package org.estatio.capex.dom.invoice.viewmodel;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.IncomingInvoiceItem;

/**
 * REVIEW: this could be inlined as a mixin, however would result in: domain layer -> app layer  ??
 */
@Mixin(method = "act")
public class IncomingInvoice_switchView {

    private final IncomingInvoice incomingInvoice;

    public IncomingInvoice_switchView(IncomingInvoice incomingInvoice) {
        this.incomingInvoice = incomingInvoice;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(
            contributed = Contributed.AS_ACTION,
            cssClassFa = "fa-exchange" // not sure why this isn't being picked up from isis-non-changing.properties
    )
    @MemberOrder(sequence = "1")
    public IncomingDocAsInvoiceViewModel act() {
        Optional<Document> documentIfAny = lookupAttachedPdfService.lookupIncomingInvoicePdfFrom(incomingInvoice);
        Document document = documentIfAny.get(); // guaranteed to return, hidden if none
        final IncomingDocAsInvoiceViewModel viewModel = new IncomingDocAsInvoiceViewModel(incomingInvoice, document);
        serviceRegistry2.injectServicesInto(viewModel);
        viewModel.init();
        return viewModel;
    }

    public boolean hideAct() {
        Optional<Document> documentIfAny = lookupAttachedPdfService.lookupIncomingInvoicePdfFrom(incomingInvoice);
        return !documentIfAny.isPresent();
    }

    public String disableAct() {
        final List<IncomingInvoiceItem> items =
                incomingInvoice.getItems().stream()
                        .filter(IncomingInvoiceItem.class::isInstance)
                        .map(IncomingInvoiceItem.class::cast)
                        .collect(Collectors.toList());

        switch (items.size()) {
        case 0:
        case 1:
            return null;
        default:
            return "Can only switch view for invoices with a single item";
        }
    }

    @Inject
    LookupAttachedPdfService lookupAttachedPdfService;

    @Inject
    ServiceRegistry2 serviceRegistry2;
}

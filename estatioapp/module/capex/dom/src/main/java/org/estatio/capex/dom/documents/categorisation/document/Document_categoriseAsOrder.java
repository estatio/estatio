package org.estatio.capex.dom.documents.categorisation.document;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationState;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType;
import org.estatio.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.capex.dom.order.Order;
import org.estatio.capex.dom.order.OrderRepository;
import org.estatio.capex.dom.triggers.DomainObject_triggerAbstract;
import org.estatio.dom.asset.Property;
import org.estatio.dom.invoice.DocumentTypeData;

@Mixin(method = "act")
public class Document_categoriseAsOrder
        extends DomainObject_triggerAbstract<
                                            Document,
                                            IncomingDocumentCategorisationStateTransition,
                                            IncomingDocumentCategorisationStateTransitionType,
                                            IncomingDocumentCategorisationState> {

    public Document_categoriseAsOrder(final Document document) {
        super(document, IncomingDocumentCategorisationStateTransition.class,
              Arrays.asList( IncomingDocumentCategorisationState.NEW), IncomingDocumentCategorisationStateTransitionType.CATEGORISE);
    }

    @Action(
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(cssClassFa = "folder-open-o")
    public Object act(
            @Nullable final Property property,
            @Nullable final String comment) {

        final Document document = getDomainObject();

        document.setType(DocumentTypeData.INCOMING_ORDER.findUsing(documentTypeRepository));

        // create order
        final Order order = orderRepository.create(
                property,
                null, // order number
                null, //sellerOrderReference
                clockService.now(), // entryDate
                null, // orderDate
                null, // seller
                null, // buyer
                document.getAtPath()
        );

        paperclipRepository.attach(document, null, order);

        trigger(comment);

        return order;
    }

    public List<DocumentTypeData> choices0Act() {
        return Arrays.asList(DocumentTypeData.INCOMING_ORDER, DocumentTypeData.INCOMING_INVOICE);
    }

    public String validateAct(
            final Property property,
            final String comment) {

        return null;
    }


    public boolean hideAct() {
        if(cannotTransition()) {
            return true;
        }
        final Document document = getDomainObject();
        return !DocumentTypeData.hasIncomingType(document);
    }

    @Inject
    DocumentTypeRepository documentTypeRepository;

    @Inject
    PaperclipRepository paperclipRepository;

    @Inject
    IncomingInvoiceRepository incomingInvoiceRepository;

    @Inject
    OrderRepository orderRepository;

    @Inject
    ClockService clockService;

}

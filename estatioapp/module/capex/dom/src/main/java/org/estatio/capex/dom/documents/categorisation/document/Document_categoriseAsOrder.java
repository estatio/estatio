package org.estatio.capex.dom.documents.categorisation.document;

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

import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType;
import org.estatio.capex.dom.order.Order;
import org.estatio.capex.dom.order.OrderRepository;
import org.estatio.dom.asset.Property;
import org.estatio.dom.invoice.DocumentTypeData;

@Mixin(method = "act")
public class Document_categoriseAsOrder
        extends Document_triggerAbstract {

    private final Document document;

    public Document_categoriseAsOrder(final Document document) {
        super(document, IncomingDocumentCategorisationStateTransitionType.CATEGORISE);
        this.document = document;
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
                document.getAtPath(),
                null  // approval state... will cause state transition to be created automatically by subscriber
        );

        paperclipRepository.attach(document, null, order);

        trigger(comment);

        return order;
    }

    public String validateAct(final Property property, final String comment) {
        return null;
    }

    public boolean hideAct() {
        if(cannotTransition()) {
            return true;
        }
        final Document document = getDomainObject();
        return !DocumentTypeData.hasIncomingType(document);
    }

    public String disableAct() {
        return reasonGuardNotSatisified();
    }

    @Inject
    DocumentTypeRepository documentTypeRepository;

    @Inject
    PaperclipRepository paperclipRepository;

    @Inject
    OrderRepository orderRepository;

    @Inject
    ClockService clockService;

}

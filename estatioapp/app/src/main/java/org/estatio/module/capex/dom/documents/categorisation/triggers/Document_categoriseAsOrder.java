package org.estatio.module.capex.dom.documents.categorisation.triggers;

import java.util.Arrays;

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

import org.estatio.module.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.OrderRepository;
import org.estatio.module.asset.dom.Property;
import org.estatio.module.invoice.dom.DocumentTypeData;

/**
 * This cannot be inlined (needs to be a mixin) because Document is part of the incode platform and
 * does not know about its orders
 */
@Mixin(method = "act")
public class Document_categoriseAsOrder
        extends Document_triggerAbstract {

    private final Document document;

    public Document_categoriseAsOrder(final Document document) {
        super(document, IncomingDocumentCategorisationStateTransitionType.CATEGORISE);
        this.document = document;
    }

    public static class ActionDomainEvent
            extends Document_triggerAbstract.ActionDomainEvent<Document_categoriseAsOrder> {}

    @Action(
            domainEvent = ActionDomainEvent.class,
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(cssClassFa = "folder-open-o")
    public Object act(
            @Nullable final Property property,
            final IncomingInvoiceType orderType,
            @Nullable final String comment) {

        final Document document = getDomainObject();

        document.setType(DocumentTypeData.INCOMING_ORDER.findUsing(documentTypeRepository));

        // create order
        final Order order = orderRepository.create(
                property,
                null, null, clockService.now(), null, null, buyerFinder.buyerDerivedFromDocumentName(document),
                orderType,
                // order number
                //sellerOrderReference
                // entryDate
                // orderDate
                // seller
                // buyer
                document.getAtPath(),
                null  // approval state... will cause state transition to be created automatically by subscriber
        );

        paperclipRepository.attach(document, null, order);

        trigger(comment, null);

        return order;
    }

    public String validateAct(final Property property, final IncomingInvoiceType orderType, final String comment) {
        if (Arrays.asList(IncomingInvoiceType.CAPEX, IncomingInvoiceType.SERVICE_CHARGES, IncomingInvoiceType.PROPERTY_EXPENSES).contains(orderType)){
            if (property==null){
                return String.format("Property is mandatory for type %s", orderType.name());
            }
        }
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

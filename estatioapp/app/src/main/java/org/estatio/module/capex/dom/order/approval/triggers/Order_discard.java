package org.estatio.module.capex.dom.order.approval.triggers;

import java.util.Optional;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.module.capex.dom.documents.LookupAttachedPdfService;
import org.estatio.module.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition;
import org.estatio.module.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType;
import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.approval.OrderApprovalStateTransitionType;
import org.estatio.module.capex.dom.orderinvoice.OrderItemInvoiceItemLinkRepository;
import org.estatio.module.base.platform.applib.ReasonBuffer2;

/**
 * This mixin cannot (easily) be inlined because it inherits functionality from its superclass, and in any case
 * this follows a common pattern applicable for all domain objects that have an associated state transition machine.
 */
@Mixin(method="act")
public class Order_discard extends
        Order_triggerAbstract {

    private final Order order;

    public Order_discard(Order order) {
        super(order, OrderApprovalStateTransitionType.DISCARD);
        this.order = order;
    }

    public static class ActionDomainEvent extends Order_triggerAbstract.ActionDomainEvent<Order_discard> {}

    @Action(
            domainEvent = ActionDomainEvent.class,
            semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE
    )
    @ActionLayout(cssClassFa = "trash-o")
    public Order act(@Nullable final String comment) {

        Optional<Document> documentIfAny = lookupAttachedPdfService.lookupOrderPdfFrom(order);
        documentIfAny.ifPresent(document ->
                stateTransitionService.trigger(
                        document,
                        IncomingDocumentCategorisationStateTransition.class,
                        IncomingDocumentCategorisationStateTransitionType.DISCARD_ASSOCIATED,
                        comment, comment));

        trigger(comment, comment);
        return getDomainObject();
    }

    public boolean hideAct() {
        return cannotTransition();
    }

    public String disableAct() {
        ReasonBuffer2 buf = ReasonBuffer2.forSingle();
        buf.append(!linkRepository.findByOrder(order).isEmpty(), "One or more items have been linked to an invoice");
        buf.append(reasonGuardNotSatisified());
        return buf.getReason();
    }

    @Inject
    LookupAttachedPdfService lookupAttachedPdfService;
    @Inject OrderItemInvoiceItemLinkRepository linkRepository;

}

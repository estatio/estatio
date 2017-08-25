package org.estatio.capex.dom.orderinvoice;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.invoice.IncomingInvoiceItemRepository;
import org.estatio.capex.dom.order.OrderItem;
import org.estatio.capex.dom.order.OrderItemInvoiceItemLinkValidationService;

@Mixin(method="act")
abstract class OrderItem_abstractMixinInvoiceItemLinks {

    final OrderItem mixee;
    OrderItem_abstractMixinInvoiceItemLinks(final OrderItem mixee) {
        this.mixee = mixee;
    }


    @Inject
    IncomingInvoiceItemRepository invoiceItemRepository;

    @Inject
    OrderItemInvoiceItemLinkRepository orderItemInvoiceItemLinkRepository;

    @Inject
    OrderItemInvoiceItemLinkValidationService linkValidationService;

}

package org.estatio.capex.dom.orderinvoice;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.order.OrderItem;

@Mixin(method="act")
abstract class OrderItem_abstractMixinInvoiceItemLinks extends AbstractMixinOrderItemLinks<OrderItem> {

    OrderItem_abstractMixinInvoiceItemLinks(final OrderItem mixee) { super(mixee); }


}

package org.estatio.module.capex.dom.orderinvoice;

import org.estatio.module.capex.dom.order.OrderItem;

/**
 * @see IncomingInvoiceItem_abstractMixinOrderItemLinks
 */
abstract class OrderItem_abstractMixinInvoiceItemLinks extends AbstractMixinOrderItemLinks<OrderItem> {

    OrderItem_abstractMixinInvoiceItemLinks(final OrderItem mixee) { super(mixee); }


}

package org.estatio.capex.dom.orderinvoice;

import org.estatio.capex.dom.order.OrderItem;

/**
 * @see IncomingInvoiceItem_abstractMixinOrderItemLinks
 */
abstract class OrderItem_abstractMixinInvoiceItemLinks extends AbstractMixinOrderItemLinks<OrderItem> {

    OrderItem_abstractMixinInvoiceItemLinks(final OrderItem mixee) { super(mixee); }


}

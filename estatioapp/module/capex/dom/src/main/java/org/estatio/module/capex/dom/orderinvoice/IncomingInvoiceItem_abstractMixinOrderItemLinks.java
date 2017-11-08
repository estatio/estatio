package org.estatio.module.capex.dom.orderinvoice;

import java.math.BigDecimal;

import org.estatio.capex.dom.invoice.IncomingInvoiceItem;

/**
 * @see OrderItem_abstractMixinInvoiceItemLinks
 */
abstract class IncomingInvoiceItem_abstractMixinOrderItemLinks extends AbstractMixinOrderItemLinks<IncomingInvoiceItem> {

    IncomingInvoiceItem_abstractMixinOrderItemLinks(final IncomingInvoiceItem mixee) { super(mixee); }

    String validateLinkAmount(
            final BigDecimal currentNetAmount,
            final BigDecimal proposedNetAmount) {
        return super.validateLinkAmount(currentNetAmount, proposedNetAmount, mixee);
    }

}

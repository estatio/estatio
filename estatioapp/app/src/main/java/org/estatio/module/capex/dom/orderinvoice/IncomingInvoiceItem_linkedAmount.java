package org.estatio.module.capex.dom.orderinvoice;

import java.math.BigDecimal;
import java.util.Optional;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;

/**
 * @see OrderItem_invoiceItemLinks (one-to-many)
 */
@Mixin(method="prop")
public class IncomingInvoiceItem_linkedAmount extends IncomingInvoiceItem_abstractMixinOrderItemLinks {
    public IncomingInvoiceItem_linkedAmount(final IncomingInvoiceItem mixee) { super(mixee); }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed= Contributed.AS_ASSOCIATION)
    public BigDecimal prop() {
        final Optional<BigDecimal> items = orderItemInvoiceItemLinkRepository.findByInvoiceItem(mixee)
                        .map(OrderItemInvoiceItemLink::getNetAmount);
        return items.orElse(null);
    }
}

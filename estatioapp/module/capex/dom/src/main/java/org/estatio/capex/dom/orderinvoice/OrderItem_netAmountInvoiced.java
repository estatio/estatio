package org.estatio.capex.dom.orderinvoice;

import java.math.BigDecimal;

import javax.validation.constraints.Digits;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.order.OrderItem;

@Mixin(method="prop")
public class OrderItem_netAmountInvoiced extends OrderItem_abstractMixinInvoiceItemLinks {
    public OrderItem_netAmountInvoiced(final OrderItem mixee) { super(mixee); }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed= Contributed.AS_ASSOCIATION)
    @Digits(integer = 13, fraction = 2)
    public BigDecimal prop() {
        return orderItemInvoiceItemLinkRepository.calculateNetAmountLinkedToOrderItem(mixee);
    }
}

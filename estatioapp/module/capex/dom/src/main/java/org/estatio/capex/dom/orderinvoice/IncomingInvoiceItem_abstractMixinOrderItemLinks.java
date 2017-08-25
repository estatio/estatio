package org.estatio.capex.dom.orderinvoice;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.capex.dom.order.OrderItemInvoiceItemLinkValidationService;
import org.estatio.capex.dom.order.OrderItemRepository;

@Mixin(method="act")
abstract class IncomingInvoiceItem_abstractMixinOrderItemLinks {

    final IncomingInvoiceItem mixee;
    IncomingInvoiceItem_abstractMixinOrderItemLinks(final IncomingInvoiceItem mixee) {
        this.mixee = mixee;
    }

    String validateLinkAmount(
            final BigDecimal currentNetAmount,
            final BigDecimal proposedNetAmount) {
        if(proposedNetAmount == null) return null; // shouldn't occur, I think.
        if(proposedNetAmount.compareTo(BigDecimal.ZERO) <= 0) return "Must be a positive amount";
        final BigDecimal netAmountAvailableToLink =
                orderItemInvoiceItemLinkRepository.calculateNetAmountNotLinkedFromInvoiceItem(mixee);
        final BigDecimal netAmountAvailableTakingCurrentIntoAccount =
                netAmountAvailableToLink.add(currentNetAmount);
        if(proposedNetAmount.compareTo(netAmountAvailableTakingCurrentIntoAccount) > 0) {
            return "Cannot exceed remaining amount to be linked (" + netAmountAvailableTakingCurrentIntoAccount + ")";
        }
        return null;
    }

    @Inject
    OrderItemRepository orderItemRepository;

    @Inject
    OrderItemInvoiceItemLinkRepository orderItemInvoiceItemLinkRepository;

    @Inject
    OrderItemInvoiceItemLinkValidationService linkValidationService;

}

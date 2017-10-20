package org.estatio.capex.dom.orderinvoice;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.estatio.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.capex.dom.invoice.IncomingInvoiceItemRepository;
import org.estatio.capex.dom.order.OrderItemRepository;

abstract class AbstractMixinOrderItemLinks<T> {

    final T mixee;

    AbstractMixinOrderItemLinks(final T mixee) {
        this.mixee = mixee;
    }

    String validateLinkAmount(
            final BigDecimal currentNetAmount,
            final BigDecimal proposedNetAmount,
            final IncomingInvoiceItem invoiceItem) {

        if(proposedNetAmount == null) return null; // shouldn't occur, I think.

        if(proposedNetAmount.compareTo(BigDecimal.ZERO) <= 0) return "Must be a positive amount";

        final BigDecimal netAmountAvailableToLink =
                orderItemInvoiceItemLinkRepository.calculateNetAmountNotLinkedFromInvoiceItem(invoiceItem);
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
    IncomingInvoiceItemRepository invoiceItemRepository;

    @Inject
    OrderItemInvoiceItemLinkRepository orderItemInvoiceItemLinkRepository;

}

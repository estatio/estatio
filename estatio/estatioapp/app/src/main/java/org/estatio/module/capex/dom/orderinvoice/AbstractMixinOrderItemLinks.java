package org.estatio.module.capex.dom.orderinvoice;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItemRepository;
import org.estatio.module.capex.dom.order.OrderItemRepository;

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

        if(proposedNetAmount.compareTo(BigDecimal.ZERO) == 0) return "Cannot be zero";

        final BigDecimal netAmountAvailableToLink =
                orderItemInvoiceItemLinkRepository.calculateNetAmountNotLinkedFromInvoiceItem(invoiceItem);
        final BigDecimal netAmountAvailableTakingCurrentIntoAccount =
                netAmountAvailableToLink.add(currentNetAmount);


        // A new requirement to insert orders with negative amouts adds some complexity

        if (netAmountAvailableTakingCurrentIntoAccount.signum() >= 0){
            //it's a positve number
            if(proposedNetAmount.compareTo(netAmountAvailableTakingCurrentIntoAccount) > 0) {
                return "Cannot exceed remaining amount to be linked (" + netAmountAvailableTakingCurrentIntoAccount + ")";
            }
        } else {
            if(proposedNetAmount.compareTo(netAmountAvailableTakingCurrentIntoAccount) < 0) {
                return "Cannot exceed remaining amount to be linked (" + netAmountAvailableTakingCurrentIntoAccount + ")";
            }
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

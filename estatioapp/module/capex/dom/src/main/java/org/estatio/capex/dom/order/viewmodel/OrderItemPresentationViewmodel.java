package org.estatio.capex.dom.order.viewmodel;

import java.math.BigDecimal;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;

import org.estatio.capex.dom.order.OrderItem;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.capex.dom.order.viewmodel.OrderItemPresentationViewmodel")
public class OrderItemPresentationViewmodel {

        public OrderItemPresentationViewmodel(){}

        public OrderItemPresentationViewmodel(
                final OrderItem orderItem
        ){
            this.orderItem = orderItem;
            this.outstandingAmount = orderItem.netAmountOutstanding();
            this.netAmount = orderItem.getNetAmount();
            this.sellerOrderReference = orderItem.getOrdr().getSellerOrderReference();
            this.description = orderItem.getDescription();
        }

    @Getter @Setter
    private String sellerOrderReference;

    @Getter @Setter
    private String description;

    @Getter @Setter
    private BigDecimal outstandingAmount;

    @Getter @Setter
    private BigDecimal netAmount;

    @Getter @Setter
    private OrderItem orderItem;

}

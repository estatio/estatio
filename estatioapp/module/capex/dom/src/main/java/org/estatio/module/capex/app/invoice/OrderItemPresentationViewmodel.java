package org.estatio.module.capex.app.invoice;

import java.math.BigDecimal;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Nature;

import org.estatio.module.capex.dom.order.OrderItem;

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
    @MemberOrder(sequence = "1")
    private String sellerOrderReference;

    @Getter @Setter
    @MemberOrder(sequence = "4")
    private String description;

    @Getter @Setter
    @MemberOrder(sequence = "2")
    private BigDecimal outstandingAmount;

    @Getter @Setter
    @MemberOrder(sequence = "3")
    private BigDecimal netAmount;

    @Getter @Setter
    @MemberOrder(sequence = "5")
    private OrderItem orderItem;

}

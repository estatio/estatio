package org.estatio.module.capex.app.orderinvoice;

import java.math.BigDecimal;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;

import org.estatio.module.capex.dom.order.OrderItem;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.capex.dom.orderinvoice.viewmodel.LinkedOrderItemViewModel")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class LinkedOrderItemViewModel {

    private OrderItem orderItem;

    private BigDecimal netAmount;

    private BigDecimal netAmountInvoiced;

}

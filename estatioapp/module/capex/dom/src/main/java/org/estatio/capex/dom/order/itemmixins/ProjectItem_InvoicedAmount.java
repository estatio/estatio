package org.estatio.capex.dom.order.itemmixins;

import java.math.BigDecimal;
import java.util.function.Function;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.order.OrderItem;
import org.estatio.capex.dom.order.OrderItemRepository;
import org.estatio.capex.dom.project.ProjectItem;

@Mixin
public class ProjectItem_InvoicedAmount {

    private final ProjectItem projectItem;
    public ProjectItem_InvoicedAmount(ProjectItem projectItem){
        this.projectItem = projectItem;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public BigDecimal invoicedAmount(){
        return sum(OrderItem::getNetAmountInvoiced);
    }

    private BigDecimal sum(final Function<OrderItem, BigDecimal> x) {
        return orderItemRepository.findByProjectAndCharge(projectItem.getProject(), projectItem.getCharge()).stream()
                .map(x)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Inject
    OrderItemRepository orderItemRepository;
}

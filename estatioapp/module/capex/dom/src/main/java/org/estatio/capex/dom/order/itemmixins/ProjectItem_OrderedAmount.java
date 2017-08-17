package org.estatio.capex.dom.order.itemmixins;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.Function;

import javax.inject.Inject;
import javax.jdo.annotations.Column;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.order.OrderItem;
import org.estatio.capex.dom.order.OrderItemRepository;
import org.estatio.capex.dom.project.ProjectItem;

/**
 * This cannot be inlined (needs to be a mixin) because Project does not know about orders.
 */
@Mixin
public class ProjectItem_OrderedAmount {

    private final ProjectItem projectItem;
    public ProjectItem_OrderedAmount(ProjectItem projectItem){
        this.projectItem = projectItem;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @Column(scale = 2)
    public BigDecimal orderedAmount(){
        return sum(OrderItem::getNetAmount);
    }

    private BigDecimal sum(final Function<OrderItem, BigDecimal> x) {
        return orderItemRepository.findByProjectAndCharge(projectItem.getProject(), projectItem.getCharge()).stream()
                .map(x)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Inject
    OrderItemRepository orderItemRepository;
}

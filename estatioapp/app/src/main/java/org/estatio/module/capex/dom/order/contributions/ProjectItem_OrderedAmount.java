package org.estatio.module.capex.dom.order.contributions;

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

import org.estatio.module.capex.dom.order.OrderItem;
import org.estatio.module.capex.dom.order.OrderItemRepository;
import org.estatio.module.capex.dom.project.ProjectItem;

/**
 * TODO: although this could currently be inlined, we expect to factor out project from orders, in which case this will be a typical contribution across modules.
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

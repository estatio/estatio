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
import org.estatio.capex.dom.project.Project;

@Mixin
public class Project_OrderedAmount {

    private final Project project;
    public Project_OrderedAmount(Project project){
        this.project = project;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @Column(scale = 2)
    public BigDecimal orderedAmount(){
        return sum(OrderItem::getNetAmount);
    }

    private BigDecimal sum(final Function<OrderItem, BigDecimal> x) {
        return orderItemRepository.findByProject(project).stream()
                .map(x)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Inject
    OrderItemRepository orderItemRepository;
}

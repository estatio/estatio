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
import org.apache.isis.applib.services.wrapper.WrapperFactory;

import org.estatio.module.capex.dom.order.OrderItem;
import org.estatio.module.capex.dom.order.OrderItemRepository;
import org.estatio.module.capex.dom.order.approval.OrderApprovalState;
import org.estatio.module.capex.dom.project.Project;

/**
 * TODO: although this could currently be inlined, we expect to factor out project from orders, in which case this will be a typical contribution across modules.
 */
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
        return project.isParentProject() ? amountWhenParentProject() : sum(OrderItem::getNetAmount);
    }

    private BigDecimal sum(final Function<OrderItem, BigDecimal> x) {
        return orderItemRepository.findByProject(project).stream()
                .filter(oi->oi.getOrdr().getApprovalState()!=OrderApprovalState.DISCARDED)
                .map(x)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal amountWhenParentProject(){
        BigDecimal result = BigDecimal.ZERO;
        for (Project child : project.getChildren()){
            result = result.add(wrapperFactory.wrap(new Project_OrderedAmount(child)).orderedAmount());
        }
        return result;
    }

    @Inject
    OrderItemRepository orderItemRepository;

    @Inject
    WrapperFactory wrapperFactory;

}

package org.estatio.module.capex.dom.order.contributions;

import java.util.List;

import javax.inject.Inject;

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
public class ProjectItem_OrderItems {

    private final ProjectItem projectItem;
    public ProjectItem_OrderItems(ProjectItem projectItem){
        this.projectItem = projectItem;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<OrderItem> orderItems() {
        return orderItemRepository.findByProjectAndCharge(projectItem.getProject(), projectItem.getCharge());
    }

    @Inject
    private OrderItemRepository orderItemRepository;
}

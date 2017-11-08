package org.estatio.capex.dom.order.itemmixins;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.order.OrderItem;
import org.estatio.capex.dom.order.OrderItemRepository;
import org.estatio.module.capex.dom.project.ProjectItem;

/**
 * This cannot be inlined (needs to be a mixin) because Project does not know about orders.
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

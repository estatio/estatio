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
import org.estatio.capex.dom.project.Project;

@Mixin
public class Project_OrderItemsNotOnProjectItem {

    private final Project project;
    public Project_OrderItemsNotOnProjectItem(Project project){
        this.project = project;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<OrderItem> orderItemsNotOnProjectItem() {
        return orderItemRepository.orderItemsNotOnProjectItem(project);
    }

    @Inject
    OrderItemRepository orderItemRepository;
}

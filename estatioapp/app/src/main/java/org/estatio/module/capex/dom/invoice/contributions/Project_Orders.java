package org.estatio.module.capex.dom.invoice.contributions;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.OrderItemRepository;
import org.estatio.module.capex.dom.project.Project;

@Mixin
public class Project_Orders {

    private final Project project;
    public Project_Orders(Project project){
        this.project = project;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(defaultView = "hide")
    public List<Order> orders(){
        return orderItemRepository.findByProject(project).stream()
                .map(oi->oi.getOrdr())
                .distinct()
                .collect(Collectors.toList());
    }

    @Inject
    OrderItemRepository orderItemRepository;

}

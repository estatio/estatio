package org.estatio.module.capex.app.project;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.user.UserService;
import org.apache.isis.applib.services.wrapper.WrapperFactory;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.base.dom.EstatioRole;
import org.estatio.module.capex.dom.invoice.contributions.Project_InvoiceItemsNotOnProjectItem;
import org.estatio.module.capex.dom.order.contributions.Project_OrderItemsNotOnProjectItem;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.dom.project.ProjectBudget;
import org.estatio.module.capex.dom.project.ProjectBudgetRepository;
import org.estatio.module.capex.dom.project.ProjectItem;
import org.estatio.module.charge.dom.Charge;

@Mixin
public class Project_migrateBudget {

    private final Project project;

    public Project_migrateBudget(Project project) {
        this.project = project;
    }

    @Action()
    public Project $$() {
        if (!projectBudgetRepository.findByProject(project).isEmpty()) return project;

        final ProjectBudget budget = projectBudgetRepository.findOrCreate(project, 1);
        Lists.newArrayList(budget.getItems()).forEach(bi->{
            final ProjectItem projectItemIfAny = Lists.newArrayList(project.getItems()).stream()
                    .filter(pi -> pi.equals(bi.getProjectItem())).findFirst().orElse(null);
            if (projectItemIfAny !=null && projectItemIfAny.getBudgetedAmountOld()!=null) bi.setAmount(projectItemIfAny.getBudgetedAmountOld());
        });

        return project;
    }

    public String disable$$(){
        if (!projectBudgetRepository.findByProject(project).isEmpty()) return "This project has a budget already";
        return null;
    }

    @Inject
    private ProjectBudgetRepository projectBudgetRepository;

}

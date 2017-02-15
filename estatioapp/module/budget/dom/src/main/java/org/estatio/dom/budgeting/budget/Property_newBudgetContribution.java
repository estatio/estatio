package org.estatio.dom.budgeting.budget;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.InvokeOn;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.asset.Property;

@DomainService(
        nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY
)
@DomainServiceLayout()
public class Property_newBudgetContribution {

    @Action(semantics = SemanticsOf.SAFE, invokeOn = InvokeOn.OBJECT_ONLY)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(render = RenderType.LAZILY)
    public List<Budget> budgets(Property property) {
        return budgetRepository.findByProperty(property);
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(name = "budgets", sequence = "1")
    public Budget newBudget(
            final Property property,
            final int year) {
        Budget budget = budgetRepository.newBudget(property, new LocalDate(year, 01, 01), new LocalDate(year, 12, 31));
        budget.findOrCreatePartitioningForBudgeting();
        return budget;
    }

    public String validateNewBudget(
            final Property property,
            final int year) {
        return budgetRepository.validateNewBudget(property, year);
    }

    @Inject
    private BudgetRepository budgetRepository;

}

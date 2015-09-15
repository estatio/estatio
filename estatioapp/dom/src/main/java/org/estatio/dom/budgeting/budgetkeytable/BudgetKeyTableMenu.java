package org.estatio.dom.budgeting.budgetkeytable;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.asset.Property;

/**
 * Created by jodo on 14/09/15.
 */
@DomainService(nature = NatureOfService.VIEW_MENU_ONLY)
@DomainServiceLayout(menuBar = DomainServiceLayout.MenuBar.PRIMARY, named = "Budgets")
public class BudgetKeyTableMenu {

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public BudgetKeyTable newBudgetKeyTable(
            final @ParameterLayout(named = "Property") Property property,
            final @ParameterLayout(named = "Name") String name,
            final @ParameterLayout(named = "Start Date") LocalDate startDate,
            final @ParameterLayout(named = "End Date") LocalDate endDate,
            final @ParameterLayout(named = "Foundation Value Type") BudgetFoundationValueType foundationValueType,
            final @ParameterLayout(named = "Key Value Method") BudgetKeyValueMethod keyValueMethod,
            final @ParameterLayout(named = "Number Of Digits") Integer numberOfDigits) {
        return budgetKeyTables.newBudgetKeyTable(property, name, startDate, endDate, foundationValueType, keyValueMethod, numberOfDigits);
    }

    public String validateNewBudgetKeyTable(
            final Property property,
            final String name,
            final LocalDate startDate,
            final LocalDate endDate,
            final BudgetFoundationValueType foundationValueType,
            final BudgetKeyValueMethod keyValueMethod,
            final Integer numberOfDigits) {
        return budgetKeyTables.validateNewBudgetKeyTable(property, name, startDate, endDate, foundationValueType, keyValueMethod, numberOfDigits);
    }

    @Action(semantics = SemanticsOf.SAFE)
    @CollectionLayout(render = RenderType.EAGERLY)
    public List<BudgetKeyTable> findBudgetKeyTableByProperty(Property property) {
        return budgetKeyTables.findBudgetKeyTableByProperty(property);
    }

    @Inject
    private BudgetKeyTables budgetKeyTables;
}

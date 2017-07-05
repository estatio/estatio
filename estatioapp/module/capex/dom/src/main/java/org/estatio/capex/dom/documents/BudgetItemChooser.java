package org.estatio.capex.dom.documents;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.asset.Property;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budget.BudgetRepository;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.budgeting.budgetitem.BudgetItemRepository;
import org.estatio.dom.charge.Charge;

@DomainService(nature = NatureOfService.DOMAIN)
public class BudgetItemChooser {

    @Programmatic
    public List<BudgetItem> choicesBudgetItemFor(final Property property, final Charge charge) {
        List<BudgetItem> result = Lists.newArrayList();
        boolean hasProperty = property != null;
        boolean hasCharge = charge != null;
        if (hasProperty) {
            for (Budget budget : budgetRepository.findByProperty(property)) {
                if (hasCharge) {
                    result.add(budgetItemRepository.findByBudgetAndCharge(budget, charge));
                } else {
                    result.addAll(budget.getItems());
                }
            }
        } else {
            if (hasCharge) {
                result = budgetItemRepository.allBudgetItems().stream()
                        .filter(x -> Objects
                                .equals(x.getCharge(), charge))
                        .collect(Collectors.toList());
            } else {
                result = budgetItemRepository.allBudgetItems();
            }
        }
        return result;
    }

    @Inject
    BudgetRepository budgetRepository;

    @Inject
    BudgetItemRepository budgetItemRepository;

}

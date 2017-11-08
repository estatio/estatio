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
import org.estatio.module.budgeting.dom.budgetitem.BudgetItem;
import org.estatio.module.budgeting.dom.budgetitem.BudgetItemRepository;
import org.estatio.module.charge.dom.Charge;

@DomainService(nature = NatureOfService.DOMAIN)
public class BudgetItemChooser {

    @Programmatic
    public List<BudgetItem> choicesBudgetItemFor(final Property property, final Charge charge) {
        List<BudgetItem> result = Lists.newArrayList();
        boolean hasProperty = property != null;
        boolean hasCharge = charge != null;
        if (hasProperty) {
            for (BudgetItem budgetItem : budgetItemRepository.findByProperty(property)) {
                if (hasCharge && charge==budgetItem.getCharge()) {
                    result.add(budgetItem);
                } else {
                    result.add(budgetItem);
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
    BudgetItemRepository budgetItemRepository;

}

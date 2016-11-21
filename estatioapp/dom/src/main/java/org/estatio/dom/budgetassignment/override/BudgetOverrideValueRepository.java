package org.estatio.dom.budgetassignment.override;

import java.math.BigDecimal;
import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationType;
import org.estatio.dom.budgeting.budgetcalculation.Status;

@DomainService(repositoryFor = BudgetOverrideValue.class, nature = NatureOfService.DOMAIN)
public class BudgetOverrideValueRepository extends UdoDomainRepositoryAndFactory<BudgetOverrideValue> {

    public BudgetOverrideValueRepository() {
        super(BudgetOverrideValueRepository.class, BudgetOverrideValue.class);
    }

    public BudgetOverrideValue newBudgetOverrideValue(
            final BigDecimal value,
            final BudgetOverride budgetOverride,
            final BudgetCalculationType type){
        BudgetOverrideValue newValue = newTransientInstance(BudgetOverrideValue.class);
        newValue.setValue(value);
        newValue.setBudgetOverride(budgetOverride);
        newValue.setType(type);
        newValue.setStatus(Status.NEW);
        persistIfNotAlready(newValue);
        return newValue;
    }

    public List<BudgetOverrideValue> allBudgetOverrideCalculations(){
        return allInstances();
    }

}

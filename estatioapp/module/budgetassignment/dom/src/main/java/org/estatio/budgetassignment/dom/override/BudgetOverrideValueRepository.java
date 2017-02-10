package org.estatio.budgetassignment.dom.override;

import java.math.BigDecimal;
import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.budget.dom.budgetcalculation.Status;

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
        getContainer().flush(); // needed!!! (BudgetOverrideIntegrationTest@line70 fails otherwise)
        return newValue;
    }

    public BudgetOverrideValue findOrCreateOverrideValue(
            final BigDecimal value,
            final BudgetOverride budgetOverride,
            final BudgetCalculationType type){
        return findUnique(budgetOverride, type)==null ?
                newBudgetOverrideValue(value, budgetOverride, type) :
                findUnique(budgetOverride, type);
    }

    public BudgetOverrideValue findUnique(
            final BudgetOverride override,
            final BudgetCalculationType type
            ){
        return uniqueMatch("findUnique", "budgetOverride", override, "type", type);
    }

    public List<BudgetOverrideValue> allBudgetOverrideValues(){
        return allInstances();
    }

}

package org.estatio.module.budget.contributions;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budget.BudgetRepository;
import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.budget.dom.keytable.DirectCostTable;
import org.estatio.module.budget.dom.keytable.FoundationValueType;
import org.estatio.module.budget.dom.keytable.KeyTable;
import org.estatio.module.budget.dom.keytable.KeyValueMethod;
import org.estatio.module.budget.dom.partioning.PartitionItemRepository;
import org.estatio.module.budget.dom.partioning.Partitioning;
import org.estatio.module.charge.dom.Applicability;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class PropertyService {

    public List<Budget> budgets(Property property) {
        return budgetRepository.findByProperty(property);
    }

    public Budget newBudget(
            final Property property,
            final int year) {
        Budget budget = budgetRepository.newBudget(property, new LocalDate(year, 01, 01), new LocalDate(year, 12, 31));
        final Partitioning partitioningForBudgeting = budget.findOrCreatePartitioningForBudgeting();
        final KeyTable a = budget.createKeyTable("A", FoundationValueType.AREA, KeyValueMethod.PROMILLE);
        final DirectCostTable dc = budget.createDirectCostTable("DC");
        a.generateItems();
        dc.generateItems();
        final Charge charge = chargeRepository.chargesForCountry(property.getAtPath()).stream()
                .filter(c -> c.getApplicability() == Applicability.INCOMING).findFirst().orElse(null);
        if (charge!=null) {
            final BudgetItem item1 = budget.newBudgetItem(new BigDecimal("123.45"), charge);
            partitionItemRepository.newPartitionItem(partitioningForBudgeting, charge, a,item1, new BigDecimal("90.00"), null, null);
            partitionItemRepository.newPartitionItem(partitioningForBudgeting, charge, dc, item1, new BigDecimal("10.00"), null, null);
        }
        return budget;
    }

    public String validateNewBudget(
            final Property property,
            final int year) {
        return budgetRepository.validateNewBudget(property, year);
    }

    @Inject
    private BudgetRepository budgetRepository;

    @Inject PartitionItemRepository partitionItemRepository;

    @Inject ChargeRepository chargeRepository;

}

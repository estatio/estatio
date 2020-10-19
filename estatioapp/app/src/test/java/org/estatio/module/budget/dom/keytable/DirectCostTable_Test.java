package org.estatio.module.budget.dom.keytable;

import java.math.BigDecimal;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationRepository;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.budget.dom.keyitem.DirectCost;
import org.estatio.module.budget.dom.partioning.PartitionItem;

public class DirectCostTable_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    BudgetCalculationRepository mockBudgetCalculationRepository;

    @Test
    public void calculateFor_works() {

        // given
        DirectCostTable directCostTable = new DirectCostTable();
        directCostTable.budgetCalculationRepository = mockBudgetCalculationRepository;
        DirectCost directCost1 = new DirectCost();
        directCost1.setBudgetedCost(new BigDecimal("123.45"));
        directCostTable.getItems().add(directCost1);

        DirectCost directCost2 = new DirectCost();
        directCost2.setBudgetedCost(new BigDecimal("234.56"));
        directCostTable.getItems().add(directCost2);

        PartitionItem partitionItem = new PartitionItem();

        LocalDate calcStartDate = new LocalDate(2019,1,1);
        LocalDate calcEndDate = new LocalDate(2019,12,31);

        // expect
        context.checking(new Expectations(){{
            oneOf(mockBudgetCalculationRepository).findOrCreateBudgetCalculation(
                    partitionItem, directCost1, directCost1.getBudgetedCost(), BudgetCalculationType.BUDGETED, calcStartDate, calcEndDate
            );
            oneOf(mockBudgetCalculationRepository).findOrCreateBudgetCalculation(
                    partitionItem, directCost2, directCost2.getBudgetedCost(), BudgetCalculationType.BUDGETED, calcStartDate, calcEndDate
            );
        }});

        // when
        directCostTable.calculateFor(partitionItem, null, BudgetCalculationType.BUDGETED, calcStartDate, calcEndDate);

    }
}
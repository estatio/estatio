package org.estatio.module.budget.integtests.budget;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budget.BudgetRepository;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculation;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationRepository;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationService;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.budget.dom.budgetcalculation.InMemBudgetCalculation;
import org.estatio.module.budget.dom.keyitem.KeyItem;
import org.estatio.module.budget.dom.keytable.KeyTable;
import org.estatio.module.budget.dom.partioning.PartitionItem;
import org.estatio.module.budget.dom.partioning.PartitionItemRepository;
import org.estatio.module.budget.fixtures.partitioning.enums.Partitioning_enum;
import org.estatio.module.budget.integtests.BudgetModuleIntegTestAbstract;

import static org.assertj.core.api.Assertions.assertThat;
import static org.estatio.module.budget.fixtures.budgets.enums.Budget_enum.OxfBudget2015;

public class BudgetCalculationRepository_IntegTest extends BudgetModuleIntegTestAbstract {

    @Inject
    BudgetCalculationRepository budgetCalculationRepository;

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    BudgetRepository budgetRepository;

    @Inject
    PartitionItemRepository partitionItemRepository;

    @Inject
    BudgetCalculationService budgetCalculationService;

    @Before
    public void setupData() {
        runFixtureScript(Partitioning_enum.OxfPartitioning2015.builder());
    }

    public static class FindUnique extends BudgetCalculationRepository_IntegTest {

        @Test
        public void happyCase() throws Exception {
            // given
            Budget oxfBudget2015 = OxfBudget2015.findUsing(serviceRegistry);
            final LocalDate calculationStartDate = oxfBudget2015.getStartDate();
            final LocalDate calculationEndDate = oxfBudget2015.getEndDate();

            PartitionItem partitionItem = partitionItemRepository.allPartitionItems().get(0);
            final KeyTable keyTable = (KeyTable) partitionItem.getPartitioningTable();
            KeyItem keyItem = keyTable.getItems().first();
            BudgetCalculation newBudgetCalculation = budgetCalculationRepository.createBudgetCalculation(partitionItem, keyItem, BigDecimal.ZERO, BudgetCalculationType.BUDGETED,
                    calculationStartDate, calculationEndDate);

            // when
            BudgetCalculation budgetCalculation = budgetCalculationRepository.findUnique(partitionItem, keyItem, BudgetCalculationType.BUDGETED, calculationStartDate, calculationEndDate);

            // then
            assertThat(budgetCalculation).isEqualTo(newBudgetCalculation);

        }

    }

    public static class FindByBudgetAndUnitAndInvoiceChargeAndType extends BudgetCalculationRepository_IntegTest {

        @Test
        public void happyCase() throws Exception {

            // given
            Property property = Property_enum.OxfGb.findUsing(serviceRegistry);
            Budget budget = OxfBudget2015.findUsing(serviceRegistry);
            PartitionItem partitionItem = budget.getItems().first().getPartitionItems().get(0);
            budgetCalculationService.calculate(budget, BudgetCalculationType.BUDGETED, budget.getStartDate(), budget.getEndDate(), true);

            // when
            List<BudgetCalculation> budgetCalculations = budgetCalculationRepository.findByBudgetAndUnitAndInvoiceChargeAndType(budget, property.getUnits().first(), partitionItem.getCharge(), BudgetCalculationType.BUDGETED);

            // then
            assertThat(budgetCalculations.size()).isEqualTo(4);

        }
    }

    public static class FindByBudgetAndUnitAndInvoiceChargeAndInomingChargeAndType extends
            BudgetCalculationRepository_IntegTest {

        @Test
        public void happyCase() throws Exception {

            // given
            Property property = Property_enum.OxfGb.findUsing(serviceRegistry);
            Budget budget = OxfBudget2015.findUsing(serviceRegistry);
            PartitionItem partitionItem = budget.getItems().first().getPartitionItems().get(0);
            budgetCalculationService.calculate(budget, BudgetCalculationType.BUDGETED, budget.getStartDate(), budget.getEndDate(), true);

            // when
            List<BudgetCalculation> budgetCalculations = budgetCalculationRepository.findByBudgetAndUnitAndInvoiceChargeAndIncomingChargeAndType(budget, property.getUnits().first(), partitionItem.getCharge(), partitionItem.getBudgetItem().getCharge(), BudgetCalculationType.BUDGETED);

            // then
            assertThat(budgetCalculations.size()).isEqualTo(2);

        }
    }

    public static class OtherTests extends BudgetCalculationRepository_IntegTest {

        @Test
        public void findOrCreateBudgetCalculation_works() throws Exception {

            Budget budget = OxfBudget2015.findUsing(serviceRegistry);
            PartitionItem partitionItem = budget.getItems().first().getPartitionItems().get(0);
            final KeyTable keyTable = (KeyTable) partitionItem.getPartitioningTable();
            KeyItem keyItem = keyTable.getItems().first();

            final BigDecimal value = new BigDecimal("1234.56");
            final LocalDate calculationStartDate = new LocalDate(2020, 1, 1);
            final LocalDate calculationEndDate = new LocalDate(2020, 10, 15);
            final BudgetCalculationType budgetCalculationType = BudgetCalculationType.BUDGETED;
            InMemBudgetCalculation inMemCalc = new InMemBudgetCalculation(
                    value,
                    calculationStartDate,
                    calculationEndDate,
                    partitionItem,
                    keyItem,
                    budgetCalculationType,
                    null,
                    null,
                   null,
                    null,
                    null
            );
            Assertions.assertThat(budgetCalculationRepository.allBudgetCalculations()).isEmpty();

            // when
            final BudgetCalculation calculation = budgetCalculationRepository
                    .findOrCreateBudgetCalculation(inMemCalc);

            // then
            Assertions.assertThat(budgetCalculationRepository.allBudgetCalculations()).hasSize(1);
            Assertions.assertThat(budgetCalculationRepository.allBudgetCalculations()).contains(calculation);
            Assertions.assertThat(calculation.getValue()).isEqualTo(value);
            Assertions.assertThat(calculation.getCalculationStartDate()).isEqualTo(calculationStartDate);
            Assertions.assertThat(calculation.getCalculationEndDate()).isEqualTo(calculationEndDate);
            Assertions.assertThat(calculation.getPartitionItem()).isEqualTo(partitionItem);
            Assertions.assertThat(calculation.getTableItem()).isEqualTo(keyItem);
            Assertions.assertThat(calculation.getCalculationType()).isEqualTo(budgetCalculationType);
        }
    }



}

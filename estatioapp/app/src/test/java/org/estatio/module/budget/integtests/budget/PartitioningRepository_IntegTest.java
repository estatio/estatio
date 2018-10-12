package org.estatio.module.budget.integtests.budget;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budget.BudgetRepository;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.budget.dom.partioning.Partitioning;
import org.estatio.module.budget.dom.partioning.PartitioningRepository;
import org.estatio.module.budget.fixtures.budgets.enums.Budget_enum;
import org.estatio.module.budget.integtests.BudgetModuleIntegTestAbstract;

import static org.assertj.core.api.Assertions.assertThat;

public class PartitioningRepository_IntegTest extends BudgetModuleIntegTestAbstract {

    @Inject
    PartitioningRepository partitioningRepository;

    @Inject
    BudgetRepository budgetRepository;

    @Inject
    PropertyRepository propertyRepository;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, Budget_enum.OxfBudget2015.builder());
                executionContext.executeChild(this, Budget_enum.OxfBudget2016.builder());

            }
        });
    }

    public static class NewPartitioning extends PartitioningRepository_IntegTest {

        @Rule
        public ExpectedException expectedException = ExpectedException.none();

        @Test
        public void newPartitioningTest() throws Exception{

            // given
            Property property = Property_enum.OxfGb.findUsing(serviceRegistry);
            Budget budget = budgetRepository.findByPropertyAndStartDate(property,
                    Budget_enum.OxfBudget2015.getStartDate());
            assertThat(budget.getPartitionings().size()).isEqualTo(0);

            // when
            partitioningRepository.newPartitioning(budget, budget.getStartDate(), budget.getEndDate(), BudgetCalculationType.BUDGETED);
            Partitioning partitioning = budget.getPartitioningForBudgeting();

            // then
            assertThat(budget.getPartitionings().size()).isEqualTo(1);
            assertThat(budget.getPartitionings().first()).isEqualTo(partitioning);
            assertThat(partitioning.getBudget()).isEqualTo(budget);
            assertThat(partitioning.getStartDate()).isEqualTo(budget.getStartDate());
            assertThat(partitioning.getEndDate()).isEqualTo(budget.getEndDate());
            assertThat(partitioning.getType()).isEqualTo(BudgetCalculationType.BUDGETED);

        }

        @Test
        public void validatePartitioningTest() throws Exception {

            // given
            Property property = Property_enum.OxfGb.findUsing(serviceRegistry);
            Budget budget = budgetRepository.findByPropertyAndStartDate(property,
                    Budget_enum.OxfBudget2015.getStartDate());
            partitioningRepository.newPartitioning(budget, budget.getStartDate().plusDays(1), budget.getStartDate().plusDays(2), BudgetCalculationType.BUDGETED);
            transactionService.flushTransaction();
            assertThat(budget.getPartitionings().size()).isEqualTo(1);


            // when again
            final String reason = partitioningRepository
                    .validateNewPartitioning(budget, budget.getStartDate().plusDays(3), budget.getEndDate(),
                            BudgetCalculationType.BUDGETED);

            // then
            assertThat(reason).isEqualTo("Only one partitioning of type BUDGETED is supported");

        }

        @Test
        public void validatePartitioningTest2() throws Exception {

            // given
            Property property = Property_enum.OxfGb.findUsing(serviceRegistry);
            Budget budget = budgetRepository.findByPropertyAndStartDate(property,
                    Budget_enum.OxfBudget2015.getStartDate());
            partitioningRepository.newPartitioning(budget, budget.getStartDate(), budget.getEndDate(), BudgetCalculationType.ACTUAL);

            // when again
            final String reason = partitioningRepository
                    .validateNewPartitioning(budget, budget.getStartDate(), budget.getEndDate(),
                            BudgetCalculationType.ACTUAL);

            // then
            assertThat(reason).isEqualTo("This partitioning already exists");

        }


    }


}

package org.estatio.integtests.budget;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.wrapper.InvalidException;

import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budget.BudgetRepository;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationType;
import org.estatio.dom.budgeting.partioning.Partitioning;
import org.estatio.dom.budgeting.partioning.PartitioningRepository;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForOxfGb;
import org.estatio.fixture.budget.BudgetsForOxf;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class PartitioningRepository_IntegTest extends EstatioIntegrationTest {

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
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, new BudgetsForOxf());
            }
        });
    }

    public static class NewPartitioning extends PartitioningRepository_IntegTest {

        @Rule
        public ExpectedException expectedException = ExpectedException.none();

        @Test
        public void newPartitioningTest() throws Exception{

            // given
            Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
            Budget budget = budgetRepository.findByPropertyAndStartDate(property, BudgetsForOxf.BUDGET_2015_START_DATE);
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
            Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
            Budget budget = budgetRepository.findByPropertyAndStartDate(property, BudgetsForOxf.BUDGET_2015_START_DATE);
            wrap(partitioningRepository).newPartitioning(budget, budget.getStartDate().plusDays(1), budget.getStartDate().plusDays(2), BudgetCalculationType.BUDGETED);
            assertThat(budget.getPartitionings().size()).isEqualTo(1);

            // and expect
            expectedException.expect(InvalidException.class);
            expectedException.expectMessage("Reason: Only one partitioning of type BUDGETED is supported");

            // when again
            wrap(partitioningRepository).newPartitioning(budget, budget.getStartDate().plusDays(3), budget.getEndDate(), BudgetCalculationType.BUDGETED);

        }

        @Test
        public void validatePartitioningTest2() throws Exception {

            // given
            Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
            Budget budget = budgetRepository.findByPropertyAndStartDate(property, BudgetsForOxf.BUDGET_2015_START_DATE);
            wrap(partitioningRepository).newPartitioning(budget, budget.getStartDate(), budget.getEndDate(), BudgetCalculationType.ACTUAL);

            // and expect
            expectedException.expect(InvalidException.class);
            expectedException.expectMessage("Reason: This partitioning already exists");

            // when again
            wrap(partitioningRepository).newPartitioning(budget, budget.getStartDate(), budget.getEndDate(), BudgetCalculationType.ACTUAL);

        }


    }


}

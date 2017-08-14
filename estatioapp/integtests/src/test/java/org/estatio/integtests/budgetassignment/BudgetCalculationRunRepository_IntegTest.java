package org.estatio.integtests.budgetassignment;

import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.budgetassignment.calculationresult.BudgetCalculationRun;
import org.estatio.dom.budgetassignment.calculationresult.BudgetCalculationRunRepository;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budget.BudgetRepository;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationType;
import org.estatio.dom.budgeting.budgetcalculation.Status;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseRepository;
import org.estatio.fixture.asset.PropertyForOxfGb;
import org.estatio.fixture.budget.BudgetBaseLineFixture;
import org.estatio.fixture.budget.BudgetsForOxf;
import org.estatio.fixture.lease.LeaseForOxfTopModel001Gb;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class BudgetCalculationRunRepository_IntegTest extends EstatioIntegrationTest {

    @Inject
    BudgetRepository budgetRepository;

    @Inject
    BudgetCalculationRunRepository budgetCalculationRunRepository;

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    LeaseRepository leaseRepository;

    Property propertyOxf;
    List<Budget> budgetsForOxf;
    Budget budget2015;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, new BudgetBaseLineFixture());
                executionContext.executeChild(this, new BudgetsForOxf());
                if (leaseRepository.findLeaseByReference(LeaseForOxfTopModel001Gb.REF)==null) {
                    executionContext.executeChild(this, new LeaseForOxfTopModel001Gb());
                }
            }
        });
        propertyOxf = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
        budgetsForOxf = budgetRepository.findByProperty(propertyOxf);
        budget2015 = budgetRepository.findByPropertyAndStartDate(propertyOxf, BudgetsForOxf.BUDGET_2015_START_DATE);
    }

    public static class FindOrCreate extends BudgetCalculationRunRepository_IntegTest {

        @Test
        public void test() {

            Lease leaseTopModel;

            // given
            leaseTopModel = leaseRepository.findLeaseByReference(LeaseForOxfTopModel001Gb.REF);
            assertThat(budgetCalculationRunRepository.allBudgetCalculationRuns().size()).isEqualTo(0);

            // when
            BudgetCalculationRun run = wrap(budgetCalculationRunRepository).findOrCreateNewBudgetCalculationRun(leaseTopModel, budget2015, BudgetCalculationType.BUDGETED);

            // then
            assertThat(budgetCalculationRunRepository.allBudgetCalculationRuns().size()).isEqualTo(1);
            assertThat(budgetCalculationRunRepository.allBudgetCalculationRuns().get(0)).isEqualTo(run);
            assertThat(run.getBudget()).isEqualTo(budget2015);
            assertThat(run.getLease()).isEqualTo(leaseTopModel);
            assertThat(run.getType()).isEqualTo(BudgetCalculationType.BUDGETED);
            assertThat(run.getStatus()).isEqualTo(Status.NEW);

            // and when again
            wrap(budgetCalculationRunRepository).findOrCreateNewBudgetCalculationRun(leaseTopModel, budget2015, BudgetCalculationType.BUDGETED);

            // then is idemPotent
            assertThat(budgetCalculationRunRepository.allBudgetCalculationRuns().size()).isEqualTo(1);

            // and when again
            run = wrap(budgetCalculationRunRepository).findOrCreateNewBudgetCalculationRun(leaseTopModel, budget2015, BudgetCalculationType.ACTUAL);

            // then
            assertThat(budgetCalculationRunRepository.allBudgetCalculationRuns().size()).isEqualTo(2);
            assertThat(run.getType()).isEqualTo(BudgetCalculationType.ACTUAL);

        }
    }

    public static class FindByLease extends BudgetCalculationRunRepository_IntegTest {

        @Test
        public void findByLease() {

            Lease leaseTopModel;

            // given
            leaseTopModel = leaseRepository.findLeaseByReference(LeaseForOxfTopModel001Gb.REF);
            assertThat(budgetCalculationRunRepository.findByLease(leaseTopModel).size()).isEqualTo(0);

            // when
            wrap(budgetCalculationRunRepository).findOrCreateNewBudgetCalculationRun(leaseTopModel, budget2015, BudgetCalculationType.BUDGETED);

            // then
            assertThat(budgetCalculationRunRepository.findByLease(leaseTopModel).size()).isEqualTo(1);

        }

    }

    public static class FindByBudget extends BudgetCalculationRunRepository_IntegTest {

        @Test
        public void findByBudget() {

            // given
            Lease leaseTopModel = leaseRepository.findLeaseByReference(LeaseForOxfTopModel001Gb.REF);

            // when
            wrap(budgetCalculationRunRepository).findOrCreateNewBudgetCalculationRun(leaseTopModel, budget2015, BudgetCalculationType.BUDGETED);

            // then
            assertThat(budgetCalculationRunRepository.findByBudget(budget2015).size()).isEqualTo(1);

        }

    }

    public static class FindByBudgetAndType extends BudgetCalculationRunRepository_IntegTest {

        @Test
        public void findByBudgetAndType() {

            // given
            Lease leaseTopModel = leaseRepository.findLeaseByReference(LeaseForOxfTopModel001Gb.REF);

            // when
            wrap(budgetCalculationRunRepository).findOrCreateNewBudgetCalculationRun(leaseTopModel, budget2015, BudgetCalculationType.BUDGETED);

            // then
            assertThat(budgetCalculationRunRepository.findByBudgetAndType(budget2015, BudgetCalculationType.BUDGETED).size()).isEqualTo(1);
            assertThat(budgetCalculationRunRepository.findByBudgetAndType(budget2015, BudgetCalculationType.ACTUAL).size()).isEqualTo(0);

        }

    }

    public static class FindByBudgetAndTypeAndStatus extends BudgetCalculationRunRepository_IntegTest {

        @Test
        public void findByBudgetAndTypeStatus() {

            // given
            Lease leaseTopModel = leaseRepository.findLeaseByReference(LeaseForOxfTopModel001Gb.REF);

            // when
            wrap(budgetCalculationRunRepository).findOrCreateNewBudgetCalculationRun(leaseTopModel, budget2015, BudgetCalculationType.BUDGETED);

            // then
            assertThat(budgetCalculationRunRepository.findByBudgetAndTypeAndStatus(budget2015, BudgetCalculationType.BUDGETED, Status.NEW).size()).isEqualTo(1);
            assertThat(budgetCalculationRunRepository.findByBudgetAndTypeAndStatus(budget2015, BudgetCalculationType.BUDGETED, Status.ASSIGNED).size()).isEqualTo(0);

        }

    }

}

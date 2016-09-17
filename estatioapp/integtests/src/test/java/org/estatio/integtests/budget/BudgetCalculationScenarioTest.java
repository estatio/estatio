package org.estatio.integtests.budget;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.budgetassignment.BudgetAssignmentService;
import org.estatio.dom.budgetassignment.BudgetCalculationLinkRepository;
import org.estatio.dom.budgetassignment.ServiceChargeItemRepository;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budget.BudgetRepository;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationRepository;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationService;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationStatus;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationType;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForOxfGb;
import org.estatio.fixture.budget.BudgetItemAllocationsForOxf;
import org.estatio.fixture.budget.BudgetsForOxf;
import org.estatio.fixture.lease.LeaseItemForServiceChargeBudgetedForOxfTopModel001Gb;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class

BudgetCalculationScenarioTest extends EstatioIntegrationTest {

    @Inject
    BudgetCalculationRepository budgetCalculationRepository;

    @Inject
    BudgetCalculationLinkRepository budgetCalculationLinkRepository;

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    BudgetRepository budgetRepository;

    @Inject
    BudgetCalculationService budgetCalculationService;

    @Inject
    BudgetAssignmentService budgetAssignmentService;

    @Inject
    ServiceChargeItemRepository serviceChargeItemRepository;


    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, new BudgetItemAllocationsForOxf());
                executionContext.executeChild(this, new LeaseItemForServiceChargeBudgetedForOxfTopModel001Gb());
            }
        });
    }


    public static class Calculate extends BudgetCalculationScenarioTest {

        Property property;
        Budget budget;

        @Before
        public void setup() {
            // given
            property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
            budget = budgetRepository.findByPropertyAndStartDate(property, BudgetsForOxf.BUDGET_2015_START_DATE);
        }

        @Test
        public void CalculateAndAssign() throws Exception {
            calculation();
            assignBudget();
            assignBudgetWhenUpdated();
            assignBudgetWhenAudited();
            assignBudgetWhenAuditedAndUpdated();
            assignBudgetWhenAuditedAndUpdatedWithEmptyAuditedValueOnBudgetItem();
            assignBudgetWhenOccupationEndsInBudgetYear();
        }

        public void calculation() throws Exception {


            // when
            budget.calculate();

            // then
            assertThat(budgetCalculationRepository.findByBudget(budget).size()).isEqualTo(75);
            assertThat(budgetCalculationRepository.findByBudgetItemAndStatusAndCalculationType(budget.getItems().first(), BudgetCalculationStatus.TEMPORARY, BudgetCalculationType.BUDGETED).size()).isEqualTo(25);
            assertThat(serviceChargeItemRepository.allServiceChargeItems().size()).isEqualTo(0);
            assertThat(budgetAssignmentService.getShortFallAmountBudgeted(budget)).isEqualTo(new BigDecimal("69490.13"));
            assertThat(budgetAssignmentService.getShortFallAmountAudited(budget)).isEqualTo(new BigDecimal("0.00"));

        }

        public void assignBudget() throws Exception {


        }

        public void assignBudgetWhenUpdated() throws Exception {

        }

        public void assignBudgetWhenAudited() throws Exception {

        }

        public void assignBudgetWhenAuditedAndUpdated() throws Exception {

        }

        public void assignBudgetWhenAuditedAndUpdatedWithEmptyAuditedValueOnBudgetItem() throws Exception {

        }

        public void assignBudgetWhenOccupationEndsInBudgetYear() throws Exception {

        }

    }

}

package org.estatio.integtests.budget;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.assertj.core.api.Assertions;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budget.BudgetRepository;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationLinkRepository;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationRepository;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationService;
import org.estatio.dom.budgeting.budgetcalculation.CalculationType;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.lease.LeaseTermForServiceCharge;
import org.estatio.dom.lease.LeaseTermFrequency;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForOxfGb;
import org.estatio.fixture.budget.BudgetItemAllocationsForOxf;
import org.estatio.fixture.lease.LeaseItemForServiceChargeBudgetedForOxfTopModel001Gb;
import org.estatio.integtests.EstatioIntegrationTest;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.math.BigDecimal;

public class BudgetCalculationScenarioTest extends EstatioIntegrationTest {

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
            budget = budgetRepository.findByProperty(property).get(0);
        }

        @Test
        public void CalculateAndAssign() throws Exception {
            calculation();
            assignToLeases();
            assignToLeasesWhenUpdated();
            assignToLeasesWhenAudited();
            assignToLeasesWhenAuditedAndUpdated();
            assignToLeasesWhenAuditedAndUpdatedWithEmptyAuditedValueOnBudgetItem();
        }

        public void calculation() throws Exception {


            // when
            budgetCalculationRepository
                    .resetAndUpdateOrCreateBudgetCalculations(
                            budget,
                            budgetCalculationService.calculate(budget));

            // then
            Assertions.assertThat(budgetCalculationRepository.findByBudget(budget).size()).isEqualTo(75);
            Assertions.assertThat(budgetCalculationRepository.findByBudgetItemAndCalculationType(budget.getItems().first(), CalculationType.BUDGETED).size()).isEqualTo(25);

        }

        public void assignToLeases() throws Exception {


            // when
            budgetCalculationService.assignBudgetCalculationsToLeases(budget);

            // then
            Assertions.assertThat(budgetCalculationLinkRepository.allBudgetCalculationLinks().size()).isEqualTo(3);

            Assertions.assertThat(budgetCalculationLinkRepository
                    .allBudgetCalculationLinks().get(0)
                    .getBudgetCalculation()
                    .getValue())
                    .isEqualTo(new BigDecimal("92.31"));
            Assertions.assertThat(budgetCalculationLinkRepository
                    .allBudgetCalculationLinks().get(1)
                    .getBudgetCalculation()
                    .getValue())
                    .isEqualTo(new BigDecimal("98.46"));
            Assertions.assertThat(budgetCalculationLinkRepository
                    .allBudgetCalculationLinks().get(2)
                    .getBudgetCalculation()
                    .getValue())
                    .isEqualTo(new BigDecimal("320.00"));

            LeaseTermForServiceCharge createdTerm = budgetCalculationLinkRepository
                    .allBudgetCalculationLinks().get(0)
                    .getLeaseTerm();

            Assertions.assertThat(
                    createdTerm.getBudgetedValue())
                    .isEqualTo(new BigDecimal("510.77"));

            Assertions.assertThat(
                    createdTerm.getInterval())
                    .isEqualTo(budget.getInterval());

            Assertions.assertThat(
                    createdTerm.getFrequency())
                    .isEqualTo(LeaseTermFrequency.NO_FREQUENCY);

            Assertions.assertThat(budgetCalculationRepository.findByBudget(budget).size()).isEqualTo(75);
            Assertions.assertThat(budgetCalculationLinkRepository.allBudgetCalculationLinks().size()).isEqualTo(3);

        }

        public void assignToLeasesWhenUpdated() throws Exception {

            // given
            BudgetItem updatedItem = budget.getItems().first();
            updatedItem.setBudgetedValue(new BigDecimal("45000.00"));
            LeaseTermForServiceCharge existingTerm = budgetCalculationLinkRepository
                    .allBudgetCalculationLinks().get(0)
                    .getLeaseTerm();

            // when
            budgetCalculationRepository
                    .resetAndUpdateOrCreateBudgetCalculations(
                            budget,
                            budgetCalculationService.calculate(budget));
            budgetCalculationService.assignBudgetCalculationsToLeases(budget);

            // then
            Assertions.assertThat(existingTerm.getBudgetedValue())
                    .isEqualTo(new BigDecimal("556.93"));
            Assertions.assertThat(budgetCalculationRepository.findByBudget(budget).size()).isEqualTo(75);
            Assertions.assertThat(budgetCalculationLinkRepository.allBudgetCalculationLinks().size()).isEqualTo(3);
        }

        public void assignToLeasesWhenAudited() throws Exception {

            // given
            BudgetItem auditedItem = budget.getItems().last();
            auditedItem.setAuditedValue(new BigDecimal("45000.00"));
            LeaseTermForServiceCharge existingTerm = budgetCalculationLinkRepository
                    .allBudgetCalculationLinks().get(0)
                    .getLeaseTerm();
            Assertions.assertThat(existingTerm.getBudgetedValue())
                    .isEqualTo(new BigDecimal("556.93"));
            Assertions.assertThat(existingTerm.getAuditedValue()).isEqualTo(BigDecimal.ZERO);
            Assertions.assertThat(existingTerm.getInterval())
                    .isEqualTo(budget.getInterval());

            // when
            budgetCalculationRepository
                    .resetAndUpdateOrCreateBudgetCalculations(
                            budget,
                            budgetCalculationService.calculate(budget));
            budgetCalculationService.assignBudgetCalculationsToLeases(budget);

            // then
            Assertions.assertThat(existingTerm.getAuditedValue())
                    .isEqualTo(new BigDecimal("470.77"));
            Assertions.assertThat(budgetCalculationRepository.findByBudget(budget).size()).isEqualTo(125);
            Assertions.assertThat(budgetCalculationRepository.findByBudgetAndCalculationType(budget, CalculationType.AUDITED).size()).isEqualTo(50);
            Assertions.assertThat(budgetCalculationRepository.findByBudgetAndCalculationType(budget, CalculationType.BUDGETED).size()).isEqualTo(75);
            Assertions.assertThat(budgetCalculationLinkRepository.allBudgetCalculationLinks().size()).isEqualTo(5);
        }

        public void assignToLeasesWhenAuditedAndUpdated() throws Exception {

            // given
            BudgetItem auditedAndUpdatedItem = budget.getItems().last();
            auditedAndUpdatedItem.setAuditedValue(new BigDecimal("46000.00"));
            LeaseTermForServiceCharge existingTerm = budgetCalculationLinkRepository
                    .allBudgetCalculationLinks().get(0)
                    .getLeaseTerm();
            Assertions.assertThat(existingTerm.getBudgetedValue())
                    .isEqualTo(new BigDecimal("556.93"));
            Assertions.assertThat(existingTerm.getAuditedValue()).isEqualTo(new BigDecimal("470.77"));

            // when
            budgetCalculationRepository
                    .resetAndUpdateOrCreateBudgetCalculations(
                            budget,
                            budgetCalculationService.calculate(budget));
            budgetCalculationService.assignBudgetCalculationsToLeases(budget);

            // then
            Assertions.assertThat(existingTerm.getAuditedValue())
                    .isEqualTo(new BigDecimal("481.23"));

            Assertions.assertThat(budgetCalculationRepository.findByBudget(budget).size()).isEqualTo(125);
            Assertions.assertThat(budgetCalculationLinkRepository.allBudgetCalculationLinks().size()).isEqualTo(5);
        }

        public void assignToLeasesWhenAuditedAndUpdatedWithEmptyAuditedValueOnBudgetItem() throws Exception {

            // given
            BudgetItem auditedAndUpdatedItem = budget.getItems().last();
            auditedAndUpdatedItem.setAuditedValue(null);
            LeaseTermForServiceCharge existingTerm = budgetCalculationLinkRepository
                    .allBudgetCalculationLinks().get(0)
                    .getLeaseTerm();
            Assertions.assertThat(existingTerm.getBudgetedValue())
                    .isEqualTo(new BigDecimal("556.93"));
            Assertions.assertThat(existingTerm.getAuditedValue()).isEqualTo(new BigDecimal("481.23"));

            // when
            budgetCalculationRepository
                    .resetAndUpdateOrCreateBudgetCalculations(
                            budget,
                            budgetCalculationService.calculate(budget));
            budgetCalculationService.assignBudgetCalculationsToLeases(budget);

            // then
            Assertions.assertThat(existingTerm.getAuditedValue())
                    .isEqualTo(BigDecimal.ZERO);

            Assertions.assertThat(budgetCalculationRepository.findByBudget(budget).size()).isEqualTo(125);
            Assertions.assertThat(budgetCalculationRepository.findByBudgetAndCalculationType(budget, CalculationType.AUDITED).size()).isEqualTo(50);
            Assertions.assertThat(budgetCalculationRepository.findByBudgetAndCalculationType(budget, CalculationType.AUDITED).get(0).getValue()).isEqualTo(BigDecimal.ZERO);
            Assertions.assertThat(budgetCalculationLinkRepository.allBudgetCalculationLinks().size()).isEqualTo(5);
        }

    }

}

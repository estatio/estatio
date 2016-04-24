package org.estatio.integtests.budget;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.budgeting.allocation.BudgetItemAllocation;
import org.estatio.dom.budgeting.allocation.BudgetItemAllocationRepository;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budget.BudgetRepository;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculation;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationRepository;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationService;
import org.estatio.dom.budgeting.budgetcalculation.CalculationType;
import org.estatio.dom.budgeting.keyitem.KeyItem;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.Charges;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForOxfGb;
import org.estatio.fixture.budget.BudgetItemAllocationsForOxf;
import org.estatio.fixture.budget.BudgetsForOxf;
import org.estatio.fixture.charge.ChargeRefData;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class BudgetCalculationRepositoryTest extends EstatioIntegrationTest {

    @Inject
    BudgetCalculationRepository budgetCalculationRepository;

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    BudgetRepository budgetRepository;

    @Inject
    BudgetItemAllocationRepository budgetItemAllocationRepository;

    @Inject
    BudgetCalculationService budgetCalculationService;

    @Inject
    Charges charges;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, new BudgetItemAllocationsForOxf());
            }
        });
    }

    public static class FindByBudgetItemAllocationAndKeyItem extends BudgetCalculationRepositoryTest {

        @Test
        public void happyCase() throws Exception {
            // given
            BudgetItemAllocation budgetItemAllocation = budgetItemAllocationRepository.allBudgetItemAllocations().get(0);
            KeyItem keyItem = budgetItemAllocation.getKeyTable().getItems().first();
            BudgetCalculation newBudgetCalculation = budgetCalculationRepository.updateOrCreateBudgetCalculation(budgetItemAllocation, keyItem, BigDecimal.ZERO, BigDecimal.ZERO, CalculationType.BUDGETED);

            // when
            BudgetCalculation budgetCalculation = budgetCalculationRepository.findByBudgetItemAllocationAndKeyItemAndCalculationType(budgetItemAllocation, keyItem, CalculationType.BUDGETED);

            // then
            assertThat(budgetCalculation).isEqualTo(newBudgetCalculation);

        }

    }

    public static class FindByBudgetItemAllocation extends BudgetCalculationRepositoryTest {

        @Test
        public void happyCase() throws Exception {
            // given
            BudgetItemAllocation budgetItemAllocation = budgetItemAllocationRepository.allBudgetItemAllocations().get(0);
            KeyItem keyItem = budgetItemAllocation.getKeyTable().getItems().first();
            BudgetCalculation newBudgetCalculation = budgetCalculationRepository.updateOrCreateBudgetCalculation(budgetItemAllocation, keyItem, BigDecimal.ZERO, BigDecimal.ZERO, CalculationType.BUDGETED);

            // when
            List<BudgetCalculation> budgetCalculations = budgetCalculationRepository.findByBudgetItemAllocation(budgetItemAllocation);

            // then
            assertThat(budgetCalculations.size()).isEqualTo(1);
            assertThat(budgetCalculations.get(0)).isEqualTo(newBudgetCalculation);

        }

    }

    public static class FindByBudgetAndCharge extends BudgetCalculationRepositoryTest {

        @Test
        public void happyCase() throws Exception {

            // given
            Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
            Budget budget = budgetRepository.findByPropertyAndStartDate(property, BudgetsForOxf.BUDGET_2015_START_DATE);
            Charge charge = charges.findByReference(ChargeRefData.GB_SERVICE_CHARGE);
            Charge chargeNotToBeFound = charges.findByReference(ChargeRefData.GB_SERVICE_CHARGE_ONBUDGET1);
            budgetCalculationRepository.resetAndUpdateOrCreateBudgetCalculations(budget, budgetCalculationService.calculate(budget));


            // when
            List<BudgetCalculation> budgetCalculationsForCharge = budgetCalculationRepository.findByBudgetAndCharge(budget, charge);

            // then
            assertThat(budgetCalculationsForCharge.size()).isEqualTo(75);

            // and when
            budgetCalculationsForCharge = budgetCalculationRepository.findByBudgetAndCharge(budget, chargeNotToBeFound);

            // then
            assertThat(budgetCalculationsForCharge.size()).isEqualTo(0);

        }
    }

}

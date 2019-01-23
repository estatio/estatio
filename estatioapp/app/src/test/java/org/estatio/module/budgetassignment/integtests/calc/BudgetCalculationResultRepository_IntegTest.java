package org.estatio.module.budgetassignment.integtests.calc;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.budget.fixtures.budgets.enums.Budget_enum;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResult;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResultRepository;
import org.estatio.module.budgetassignment.integtests.BudgetAssignmentModuleIntegTestAbstract;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.charge.fixtures.charges.enums.Charge_enum;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;

import static org.assertj.core.api.Assertions.assertThat;

public class BudgetCalculationResultRepository_IntegTest extends BudgetAssignmentModuleIntegTestAbstract {

    @Inject
    BudgetCalculationResultRepository budgetCalculationResultRepository;

    @Inject
    ChargeRepository chargeRepository;

    Property propertyOxf;
    List<Budget> budgetsForOxf;
    Budget budget2015;
    Lease leaseTopModel;


    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, Budget_enum.OxfBudget2015.builder());
                executionContext.executeChild(this, Lease_enum.OxfTopModel001Gb.builder());
            }
        });
        budget2015 = Budget_enum.OxfBudget2015.findUsing(serviceRegistry);
        leaseTopModel = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
    }

    // TODO: finish after refactoring if still needed


    @Test
    public void upsert_works() {

        // given
        Charge invoiceCharge = Charge_enum.GbServiceCharge.findUsing(serviceRegistry);
        assertThat(budgetCalculationResultRepository.allBudgetCalculationResults()).isEmpty();

        // when
        final BigDecimal value = new BigDecimal("123.45");
        final Occupancy occupancy = leaseTopModel.getOccupancies().first();
        budgetCalculationResultRepository.upsertBudgetCalculationResult(budget2015, occupancy, invoiceCharge, BudgetCalculationType.BUDGETED, value);

        // then
        assertThat(budgetCalculationResultRepository.allBudgetCalculationResults()).hasSize(1);
        final BudgetCalculationResult result = budgetCalculationResultRepository.allBudgetCalculationResults().get(0);
        assertThat(result.getBudget()).isEqualTo(budget2015);
        assertThat(result.getLeaseTerm()).isNull();
        assertThat(result.getValue()).isEqualTo(value);
        assertThat(result.getInvoiceCharge()).isEqualTo(invoiceCharge);
        assertThat(result.getOccupancy()).isEqualTo(occupancy);
        assertThat(result.getType()).isEqualTo(BudgetCalculationType.BUDGETED);

        // and when
        final BigDecimal updatedValue = new BigDecimal("200.00");
        budgetCalculationResultRepository.upsertBudgetCalculationResult(budget2015, occupancy, invoiceCharge, BudgetCalculationType.BUDGETED, updatedValue);
        // then
        assertThat(budgetCalculationResultRepository.allBudgetCalculationResults()).hasSize(1);
        final BudgetCalculationResult updatedResult = budgetCalculationResultRepository.allBudgetCalculationResults().get(0);
        assertThat(updatedResult.getValue()).isEqualTo(updatedValue);
        // and no changes further
        assertThat(updatedResult.getBudget()).isEqualTo(budget2015);
        assertThat(updatedResult.getLeaseTerm()).isNull();
        assertThat(updatedResult.getInvoiceCharge()).isEqualTo(invoiceCharge);
        assertThat(updatedResult.getOccupancy()).isEqualTo(occupancy);
        assertThat(updatedResult.getType()).isEqualTo(BudgetCalculationType.BUDGETED);

    }


}

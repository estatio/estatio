package org.estatio.module.budgetassignment.integtests.calc;

import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budget.BudgetRepository;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.budget.fixtures.budgets.enums.Budget_enum;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResult;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResultRepository;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationRun;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationRunRepository;
import org.estatio.module.budgetassignment.integtests.BudgetAssignmentModuleIntegTestAbstract;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.charge.fixtures.charges.refdata.ChargeRefData;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;

import static org.assertj.core.api.Assertions.assertThat;

public class BudgetCalculationResultRepository_IntegTest extends BudgetAssignmentModuleIntegTestAbstract {

    @Inject
    BudgetRepository budgetRepository;

    @Inject
    BudgetCalculationRunRepository budgetCalculationRunRepository;

    @Inject
    BudgetCalculationResultRepository budgetCalculationResultRepository;

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    LeaseRepository leaseRepository;

    @Inject
    ChargeRepository chargeRepository;

    Property propertyOxf;
    List<Budget> budgetsForOxf;
    Budget budget2015;
    Lease leaseTopModel;
    BudgetCalculationRun run;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, Budget_enum.OxfBudget2015.toFixtureScript());
                executionContext.executeChild(this, Budget_enum.OxfBudget2016.toFixtureScript());

                executionContext.executeChild(this, Lease_enum.OxfTopModel001Gb.toFixtureScript());
            }
        });
        propertyOxf = Property_enum.OxfGb.findUsing(serviceRegistry);
        budgetsForOxf = budgetRepository.findByProperty(propertyOxf);
        budget2015 = budgetRepository.findByPropertyAndStartDate(propertyOxf, Budget_enum.OxfBudget2015.getStartDate());
        leaseTopModel = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
        run = wrap(budgetCalculationRunRepository).findOrCreateNewBudgetCalculationRun(leaseTopModel, budget2015, BudgetCalculationType.BUDGETED);
    }

    public static class FindOrCreate extends BudgetCalculationResultRepository_IntegTest {

        @Test
        public void test() {

            // given
            assertThat(budgetCalculationResultRepository.allBudgetCalculationResults().size()).isEqualTo(0);
            Charge invoiceCharge = chargeRepository.findByReference(ChargeRefData.GB_SERVICE_CHARGE);

            // when
            BudgetCalculationResult result = wrap(budgetCalculationResultRepository).findOrCreateBudgetCalculationResult(run, invoiceCharge);

            // then
            assertThat(budgetCalculationResultRepository.allBudgetCalculationResults().size()).isEqualTo(1);
            assertThat(result.getBudgetCalculationRun()).isEqualTo(run);
            assertThat(result.getInvoiceCharge()).isEqualTo(invoiceCharge);

            // and when again
            wrap(budgetCalculationResultRepository).findOrCreateBudgetCalculationResult(run, invoiceCharge);

            // then is idemPotent
            assertThat(budgetCalculationResultRepository.allBudgetCalculationResults().size()).isEqualTo(1);

        }
    }

}

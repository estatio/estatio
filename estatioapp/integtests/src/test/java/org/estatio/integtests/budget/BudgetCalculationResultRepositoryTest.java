package org.estatio.integtests.budget;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.budgetassignment.calculationresult.BudgetCalculationResult;
import org.estatio.dom.budgetassignment.calculationresult.BudgetCalculationResultRepository;
import org.estatio.dom.budgetassignment.calculationresult.BudgetCalculationRun;
import org.estatio.dom.budgetassignment.calculationresult.BudgetCalculationRunRepository;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budget.BudgetRepository;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationType;
import org.estatio.dom.budgeting.budgetcalculation.Status;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeRepository;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseRepository;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForOxfGb;
import org.estatio.fixture.budget.BudgetsForOxf;
import org.estatio.fixture.charge.ChargeRefData;
import org.estatio.fixture.lease.LeaseForOxfTopModel001Gb;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class BudgetCalculationResultRepositoryTest extends EstatioIntegrationTest {

    @Inject
    BudgetCalculationResultRepository budgetCalculationResultRepository;

    @Inject
    BudgetCalculationRunRepository budgetCalculationRunRepository;

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    BudgetRepository budgetRepository;

    @Inject
    ChargeRepository chargeRepository;

    @Inject
    LeaseRepository leaseRepository;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, new LeaseForOxfTopModel001Gb());
                executionContext.executeChild(this, new BudgetsForOxf());
            }
        });
    }

    public static class FindOrCreate extends BudgetCalculationResultRepositoryTest {

        @Test
        public void happyCase() throws Exception {
            // given
            Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
            Budget budget = budgetRepository.findByPropertyAndDate(property, BudgetsForOxf.BUDGET_2015_START_DATE);
            Charge invoiceCharge = chargeRepository.findByReference(ChargeRefData.GB_SERVICE_CHARGE);
            Lease leaseForTopModel = leaseRepository.findLeaseByReference(LeaseForOxfTopModel001Gb.REF);
            BudgetCalculationRun run = budgetCalculationRunRepository.createBudgetCalculationRun(leaseForTopModel, budget, BudgetCalculationType.BUDGETED, Status.NEW );

            // when
            BudgetCalculationResult result = wrap(budgetCalculationResultRepository).findOrCreateBudgetCalculationResult(run, invoiceCharge);

            // then
            assertThat(budgetCalculationResultRepository.allBudgetCalculationResults().size()).isEqualTo(1);
            assertThat(budgetCalculationResultRepository.allBudgetCalculationResults().get(0)).isEqualTo(result);
            assertThat(budgetCalculationResultRepository.allBudgetCalculationResults().get(0).getBudgetCalculationRun()).isEqualTo(run);
            assertThat(budgetCalculationResultRepository.allBudgetCalculationResults().get(0).getInvoiceCharge()).isEqualTo(invoiceCharge);
            assertThat(budgetCalculationResultRepository.allBudgetCalculationResults().get(0).getApplicationTenancy()).isEqualTo(run.getApplicationTenancy());

            // and when
            result = wrap(budgetCalculationResultRepository).findOrCreateBudgetCalculationResult(run, invoiceCharge);

            // then still
            assertThat(budgetCalculationResultRepository.allBudgetCalculationResults().size()).isEqualTo(1);

        }

    }

}

package org.estatio.integtests.budgetassignment;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.asset.dom.Property;
import org.estatio.asset.dom.PropertyRepository;
import org.estatio.budgetassignment.dom.calculationresult.BudgetCalculationResult;
import org.estatio.budgetassignment.dom.calculationresult.BudgetCalculationResultLink;
import org.estatio.budgetassignment.dom.calculationresult.BudgetCalculationResultLinkRepository;
import org.estatio.budgetassignment.dom.calculationresult.BudgetCalculationRun;
import org.estatio.budgetassignment.dom.calculationresult.BudgetCalculationRunRepository;
import org.estatio.budget.dom.budget.Budget;
import org.estatio.budget.dom.budget.BudgetRepository;
import org.estatio.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.budget.dom.budgetcalculation.Status;
import org.estatio.charge.dom.Charge;
import org.estatio.charge.dom.ChargeRepository;
import org.estatio.invoice.dom.PaymentMethod;
import org.estatio.dom.lease.InvoicingFrequency;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.lease.LeaseRepository;
import org.estatio.dom.lease.LeaseTermForServiceCharge;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForOxfGb;
import org.estatio.fixture.budget.BudgetsForOxf;
import org.estatio.fixture.charge.ChargeRefData;
import org.estatio.fixture.lease.LeaseForOxfTopModel001Gb;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class BudgetCalculationResultLinkRepository_IntegTest extends EstatioIntegrationTest {

    @Inject
    BudgetRepository budgetRepository;

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    BudgetCalculationRunRepository budgetCalculationRunRepository;

    @Inject
    BudgetCalculationResultLinkRepository budgetCalculationResultLinkRepository;

    @Inject
    ChargeRepository chargeRepository;

    @Inject
    LeaseRepository leaseRepository;

    Property propertyOxf;
    Lease leaseTopModel;
    Budget budget2015;
    Charge charge;
    BudgetCalculationRun run;
    LeaseItem leaseItem;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, new BudgetsForOxf());
                executionContext.executeChild(this, new LeaseForOxfTopModel001Gb());
            }
        });
        propertyOxf = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
        budget2015 = budgetRepository.findByPropertyAndStartDate(propertyOxf, BudgetsForOxf.BUDGET_2015_START_DATE);
        charge = chargeRepository.findByReference(ChargeRefData.GB_SERVICE_CHARGE);
        leaseTopModel = leaseRepository.findLeaseByReference(LeaseForOxfTopModel001Gb.REF);
        run = budgetCalculationRunRepository.createBudgetCalculationRun(leaseTopModel, budget2015, BudgetCalculationType.BUDGETED, Status.NEW);
        leaseItem = leaseTopModel.newItem(LeaseItemType.SERVICE_CHARGE_BUDGETED, charge, InvoicingFrequency.MONTHLY_IN_ADVANCE, PaymentMethod.DIRECT_DEBIT, leaseTopModel.getStartDate());
    }

    public static class NewLink extends BudgetCalculationResultLinkRepository_IntegTest {

        @Test
        public void testNewLink() {

            // given
            BudgetCalculationResult result = run.createCalculationResult(charge);
            LeaseTermForServiceCharge leaseTerm = (LeaseTermForServiceCharge) leaseItem.newTerm(budget2015.getStartDate(), budget2015.getEndDate());
            assertThat(budgetCalculationResultLinkRepository.allBudgetCalculationResultLinks().size()).isEqualTo(0);

            // when
            BudgetCalculationResultLink link = wrap(budgetCalculationResultLinkRepository).createBudgetCalculationResultLink(result, leaseTerm);

            // then
            assertThat(budgetCalculationResultLinkRepository.allBudgetCalculationResultLinks().size()).isEqualTo(1);
            assertThat(link.getBudgetCalculationResult()).isEqualTo(result);
            assertThat(link.getLeaseTermForServiceCharge()).isEqualTo(leaseTerm);

        }
    }

    public static class FindUnique extends BudgetCalculationResultLinkRepository_IntegTest {

        @Test
        public void findUnique() {

            // given
            BudgetCalculationResult result = run.createCalculationResult(charge);
            LeaseTermForServiceCharge leaseTerm = (LeaseTermForServiceCharge) leaseItem.newTerm(budget2015.getStartDate(), budget2015.getEndDate());
            assertThat(budgetCalculationResultLinkRepository.allBudgetCalculationResultLinks().size()).isEqualTo(0);

            // when
            BudgetCalculationResultLink link = wrap(budgetCalculationResultLinkRepository).createBudgetCalculationResultLink(result, leaseTerm);

            // then
            assertThat(budgetCalculationResultLinkRepository.findUnique(result, leaseTerm)).isEqualTo(link);

        }

    }

    public static class FindByCalculationResult extends BudgetCalculationResultLinkRepository_IntegTest {

        @Test
        public void findByCalculationResult() {

            // given
            BudgetCalculationResult result = run.createCalculationResult(charge);
            LeaseTermForServiceCharge leaseTerm = (LeaseTermForServiceCharge) leaseItem.newTerm(budget2015.getStartDate(), budget2015.getEndDate());
            assertThat(budgetCalculationResultLinkRepository.allBudgetCalculationResultLinks().size()).isEqualTo(0);

            // when
            BudgetCalculationResultLink link = wrap(budgetCalculationResultLinkRepository).createBudgetCalculationResultLink(result, leaseTerm);

            // then
            assertThat(budgetCalculationResultLinkRepository.findByCalculationResult(result).size()).isEqualTo(1);
        }

    }

    public static class FindByLeaseTerm extends BudgetCalculationResultLinkRepository_IntegTest {

        @Test
        public void findByLeaseTerm() {
            // given
            BudgetCalculationResult result = run.createCalculationResult(charge);
            LeaseTermForServiceCharge leaseTerm = (LeaseTermForServiceCharge) leaseItem.newTerm(budget2015.getStartDate(), budget2015.getEndDate());
            assertThat(budgetCalculationResultLinkRepository.allBudgetCalculationResultLinks().size()).isEqualTo(0);

            // when
            BudgetCalculationResultLink link = wrap(budgetCalculationResultLinkRepository).createBudgetCalculationResultLink(result, leaseTerm);

            // then
            assertThat(budgetCalculationResultLinkRepository.findByLeaseTerm(leaseTerm).size()).isEqualTo(1);

        }

    }

}

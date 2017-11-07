package org.estatio.integtests.budgetassignment;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.budgetassignment.calculationresult.BudgetCalculationResult;
import org.estatio.dom.budgetassignment.calculationresult.BudgetCalculationResultLink;
import org.estatio.dom.budgetassignment.calculationresult.BudgetCalculationResultLinkRepository;
import org.estatio.dom.budgetassignment.calculationresult.BudgetCalculationRun;
import org.estatio.dom.budgetassignment.calculationresult.BudgetCalculationRunRepository;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budget.BudgetRepository;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationType;
import org.estatio.dom.budgeting.budgetcalculation.Status;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeRepository;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.lease.dom.InvoicingFrequency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.LeaseTermForServiceCharge;
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
        leaseItem = leaseTopModel.newItem(LeaseItemType.SERVICE_CHARGE_BUDGETED, LeaseAgreementRoleTypeEnum.LANDLORD, charge, InvoicingFrequency.MONTHLY_IN_ADVANCE, PaymentMethod.DIRECT_DEBIT, leaseTopModel.getStartDate());
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

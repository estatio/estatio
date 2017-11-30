package org.estatio.module.budgetassignment.integtests.calc;

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
import org.estatio.module.budget.dom.budgetcalculation.Status;
import org.estatio.module.budget.fixtures.budgets.enums.Budget_enum;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResult;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResultLink;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResultLinkRepository;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationRun;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationRunRepository;
import org.estatio.module.budgetassignment.integtests.BudgetAssignmentModuleIntegTestAbstract;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.charge.fixtures.charges.enums.Charge_enum;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.lease.dom.InvoicingFrequency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.LeaseTermForServiceCharge;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;

import static org.assertj.core.api.Assertions.assertThat;

public class BudgetCalculationResultLinkRepository_IntegTest extends BudgetAssignmentModuleIntegTestAbstract {

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
                executionContext.executeChild(this, Budget_enum.OxfBudget2015.toFixtureScript());
                executionContext.executeChild(this, Budget_enum.OxfBudget2016.toFixtureScript());

                executionContext.executeChild(this, Lease_enum.OxfTopModel001Gb.toFixtureScript());
            }
        });
        propertyOxf = Property_enum.OxfGb.findUsing(serviceRegistry);
        budget2015 = budgetRepository.findByPropertyAndStartDate(propertyOxf, Budget_enum.OxfBudget2015.getStartDate());
        charge = Charge_enum.GbServiceCharge.findUsing(serviceRegistry);
        leaseTopModel = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
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

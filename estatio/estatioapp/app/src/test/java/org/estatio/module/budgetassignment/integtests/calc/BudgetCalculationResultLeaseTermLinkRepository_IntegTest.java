package org.estatio.module.budgetassignment.integtests.calc;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.budget.fixtures.budgets.enums.Budget_enum;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResult;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResultLeaseTermLink;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResultLeaseTermLinkRepository;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResultRepository;
import org.estatio.module.budgetassignment.integtests.BudgetAssignmentModuleIntegTestAbstract;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.fixtures.charges.enums.Charge_enum;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseTermForServiceCharge;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForServiceCharge_enum;

import static org.assertj.core.api.Assertions.assertThat;

public class BudgetCalculationResultLeaseTermLinkRepository_IntegTest extends BudgetAssignmentModuleIntegTestAbstract {

    @Inject
    BudgetCalculationResultRepository budgetCalculationResultRepository;
    @Inject BudgetCalculationResultLeaseTermLinkRepository budgetCalculationResultLeaseTermLinkRepository;

    Budget budget2015;
    Lease leaseTopModel;


    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, Budget_enum.OxfBudget2015.builder());
                executionContext.executeChild(this, LeaseItemForServiceCharge_enum.OxfTopModel001Gb.builder());
            }
        });
        budget2015 = Budget_enum.OxfBudget2015.findUsing(serviceRegistry);
        leaseTopModel = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
    }


    @Test
    public void find_or_create_and_finders_work() {

        // given
        Charge invoiceCharge = Charge_enum.GbServiceCharge.findUsing(serviceRegistry);
        assertThat(budgetCalculationResultRepository.allBudgetCalculationResults()).isEmpty();
        final BigDecimal value = new BigDecimal("123.45");
        final Occupancy occupancy = leaseTopModel.getOccupancies().first();
        final BudgetCalculationResult budgetCalculationResult = budgetCalculationResultRepository
                .upsertBudgetCalculationResult(budget2015, occupancy, invoiceCharge, BudgetCalculationType.BUDGETED,
                        value);
        final LeaseItem serviceChargeItem = LeaseItemForServiceCharge_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
        serviceChargeItem.verifyUntil(new LocalDate(2015,12,31));
        final LeaseTermForServiceCharge leaseTerm = (LeaseTermForServiceCharge) serviceChargeItem.getTerms().last();
        assertThat(budgetCalculationResultLeaseTermLinkRepository.listAll()).isEmpty();

        // when
        final BudgetCalculationResultLeaseTermLink link = budgetCalculationResultLeaseTermLinkRepository
                .findOrCreate(budgetCalculationResult, leaseTerm);
        transactionService.nextTransaction();

        // then
        final List<BudgetCalculationResultLeaseTermLink> allLinks = budgetCalculationResultLeaseTermLinkRepository
                .listAll();
        assertThat(allLinks).hasSize(1);
        BudgetCalculationResultLeaseTermLink link1 = allLinks.get(0);
        assertThat(link1).isEqualTo(link);
        assertThat(link1.getBudgetCalculationResult()).isEqualTo(budgetCalculationResult);
        assertThat(link1.getLeaseTerm()).isEqualTo(leaseTerm);
        assertThat(budgetCalculationResultLeaseTermLinkRepository.findByLeaseTerm(leaseTerm).get(0)).isEqualTo(link1);
        assertThat(budgetCalculationResultLeaseTermLinkRepository.findByBudgetCalculationResult(budgetCalculationResult).get(0)).isEqualTo(link1);

        // and when again
        final BudgetCalculationResultLeaseTermLink link2 = budgetCalculationResultLeaseTermLinkRepository
                .findOrCreate(budgetCalculationResult, leaseTerm);
        transactionService.nextTransaction();
        // then idempotent
        assertThat(budgetCalculationResultLeaseTermLinkRepository.listAll()).hasSize(1);
        assertThat(link2).isEqualTo(link);

    }


}

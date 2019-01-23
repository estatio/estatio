package org.estatio.module.budgetassignment.integtests.scenario;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.wrapper.DisabledException;

import org.estatio.module.asset.dom.Unit;
import org.estatio.module.asset.dom.UnitRepository;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculation;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationRepository;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.budget.dom.budgetcalculation.Status;
import org.estatio.module.budget.dom.keytable.KeyTable;
import org.estatio.module.budget.fixtures.budgets.enums.Budget_enum;
import org.estatio.module.budget.fixtures.partitioning.enums.Partitioning_enum;
import org.estatio.module.budgetassignment.contributions.Budget_Calculate;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResult;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResultRepository;
import org.estatio.module.budgetassignment.integtests.BudgetAssignmentModuleIntegTestAbstract;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.fixtures.charges.enums.Charge_enum;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseTermForServiceCharge;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import static org.assertj.core.api.Assertions.assertThat;

public class ServiceChargeBudgetScenario_IntegTest extends BudgetAssignmentModuleIntegTestAbstract {

    @Inject
    BudgetCalculationResultRepository budgetCalculationResultRepository;

    @Inject
    BudgetCalculationRepository budgetCalculationRepository;

    @Inject
    UnitRepository unitRepository;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, Partitioning_enum.BudPartitioning2015.builder());

                executionContext.executeChildT(this, Lease_enum.BudPoison001Nl.builder());
                executionContext.executeChildT(this, Lease_enum.BudMiracle002Nl.builder());
                executionContext.executeChildT(this, Lease_enum.BudHello003Nl.builder());
                executionContext.executeChildT(this, Lease_enum.BudDago004Nl.builder());
                executionContext.executeChildT(this, Lease_enum.BudNlBank004Nl.builder());
                executionContext.executeChildT(this, Lease_enum.BudHyper005Nl.builder());
                executionContext.executeChildT(this, Lease_enum.BudHello006Nl.builder());
          }
        });
    }

    Budget budget;
    List<BudgetCalculation> calculations;

    Lease leasePoison;
    Lease leaseMiracle;
    Lease leaseHello3;
    Lease leaseDago;
    Lease leaseNlBank;
    Lease leaseHyper;
    Lease leaseHello6;
    Charge invoiceCharge1;
    Charge invoiceCharge2;
    Charge incomingCharge;

    @Before
    public void setup() {
        // given
        budget = Budget_enum.BudBudget2015.findUsing(serviceRegistry);

        //**IMPORTANT!** truncate keytable
        KeyTable key2 = budget.getKeyTables().last();
        key2.getItems().last().deleteBudgetKeyItem();
        transactionService.nextTransaction();

        leasePoison = Lease_enum.BudPoison001Nl.findUsing(serviceRegistry);
        leaseMiracle = Lease_enum.BudMiracle002Nl.findUsing(serviceRegistry);
        leaseHello3 = Lease_enum.BudHello003Nl.findUsing(serviceRegistry);
        leaseDago = Lease_enum.BudDago004Nl.findUsing(serviceRegistry);
        leaseNlBank = Lease_enum.BudNlBank004Nl.findUsing(serviceRegistry);
        leaseHyper = Lease_enum.BudHyper005Nl.findUsing(serviceRegistry);
        leaseHello6 = Lease_enum.BudHello006Nl.findUsing(serviceRegistry);
        invoiceCharge1 = Charge_enum.NlServiceCharge.findUsing(serviceRegistry);
        invoiceCharge2 = Charge_enum.NlServiceCharge2.findUsing(serviceRegistry);
        incomingCharge = Charge_enum.NlIncomingCharge1.findUsing(serviceRegistry);
    }


    // TODO: finish after refactoring
    @Test
    public void fullScenarioTest() throws Exception {
        calculate_budgeted();
        when_not_final_and_calculating_again();
        finalCalculation_budgeted();
        when_trying_to_calculate_again();
//            assignBudgetWhenUpdated();
//            assignBudgetWhenAudited();
//            assignBudgetWhenAuditedAndUpdated();
    }

    public static BigDecimal U1_BVAL_1 = new BigDecimal("1928.57");
    public static BigDecimal U1_BVAL_2 = new BigDecimal("964.29");
    public static BigDecimal U2_BVAL_1 = new BigDecimal("2857.14");
    public static BigDecimal U2_BVAL_2 = new BigDecimal("1928.57");
    public static BigDecimal U3_BVAL_1 = new BigDecimal("3785.71");
    public static BigDecimal U3_BVAL_2 = new BigDecimal("2892.86");
    public static BigDecimal U4_BVAL_1 = new BigDecimal("4714.29");
    public static BigDecimal U4_BVAL_2 = new BigDecimal("3857.14");
    public static BigDecimal U5_BVAL_1 = new BigDecimal("5642.86");
    public static BigDecimal U5_BVAL_2 = new BigDecimal("4821.43");
    public static BigDecimal U6_BVAL_1 = new BigDecimal("6571.43");
    public static BigDecimal U6_BVAL_2 = new BigDecimal("5785.71");
    public static BigDecimal U7_BVAL_1 = new BigDecimal("6500.00");
    public static BigDecimal U7_BVAL_2 = new BigDecimal("6750.00");


    public void calculate_budgeted() throws Exception {

        // given
        // when
        wrap(mixin(Budget_Calculate.class, budget)).calculate(false);
        calculations = budgetCalculationRepository.findByBudget(budget);

        // then
        assertThat(calculations.size()).isEqualTo(33);
        final Unit u1 = leasePoison.getOccupancies().first().getUnit();
        testCalculationsForUnit(u1, invoiceCharge1, new ExpectedUnitResult(4, U1_BVAL_1));
        testCalculationsForUnit(u1, invoiceCharge2, new ExpectedUnitResult(1, U1_BVAL_2));

        final Unit u2 = leaseMiracle.getOccupancies().first().getUnit();
        testCalculationsForUnit(u2, invoiceCharge1, new ExpectedUnitResult(4, U2_BVAL_1));
        testCalculationsForUnit(u2, invoiceCharge2, new ExpectedUnitResult(1, U2_BVAL_2));

        final Unit u3 = leaseHello3.getOccupancies().first().getUnit();
        testCalculationsForUnit(u3, invoiceCharge1, new ExpectedUnitResult(4, U3_BVAL_1));
        testCalculationsForUnit(u3, invoiceCharge2, new ExpectedUnitResult(1, U3_BVAL_2));

        final Unit u4 = leaseDago.getOccupancies().first().getUnit();
        testCalculationsForUnit(u4, invoiceCharge1, new ExpectedUnitResult(4, U4_BVAL_1));
        testCalculationsForUnit(u4, invoiceCharge2, new ExpectedUnitResult(1, U4_BVAL_2));

        final Unit u5 = leaseHyper.getOccupancies().first().getUnit();
        testCalculationsForUnit(u5, invoiceCharge1, new ExpectedUnitResult(4, U5_BVAL_1));
        testCalculationsForUnit(u5, invoiceCharge2, new ExpectedUnitResult(1, U5_BVAL_2));

        // Dago occupies 2 units at the beginning of the budget year (u4 and u7)
        final Unit u7 = leaseDago.getOccupancies().last().getUnit();
        testCalculationsForUnit(u7, invoiceCharge1, new ExpectedUnitResult(2, U7_BVAL_1)); // unit 7 has no entry in keytable 2
        testCalculationsForUnit(u7, invoiceCharge2, new ExpectedUnitResult(1, U7_BVAL_2));

        // NlBank occupies u4 after Dago
        assertThat(leaseNlBank.getOccupancies().last().getUnit()).isEqualTo(leaseDago.getOccupancies().first().getUnit());

        // u6 is not occupied during budget year
        Unit u6 = unitRepository.findUnitByReference("BUD-006");
        testCalculationsForUnit(u6, invoiceCharge1, new ExpectedUnitResult(4, U6_BVAL_1));
        testCalculationsForUnit(u6, invoiceCharge2, new ExpectedUnitResult(1, U6_BVAL_2));

        // no calculations are assigned (final) yet
        calculations.stream().forEach(x->assertThat(x.getStatus()).isEqualTo(Status.NEW));

    }

    public void when_not_final_and_calculating_again() {

        // given
        // when
        wrap(mixin(Budget_Calculate.class, budget)).calculate(false);
        calculations = budgetCalculationRepository.findByBudget(budget);

        // then still
        assertThat(calculations.size()).isEqualTo(33);
        calculations.stream().forEach(x->assertThat(x.getStatus()).isEqualTo(Status.NEW));

    }

    public void finalCalculation_budgeted() throws Exception {

        // given
        // when
        wrap(mixin(Budget_Calculate.class, budget)).calculate(true);
        transactionService.nextTransaction();
        calculations = budgetCalculationRepository.findByBudget(budget);

        // then - regarding calculations
        assertThat(calculations.size()).isEqualTo(33);

        final List<BudgetCalculation> assignedCalculations = budgetCalculationRepository.findByBudgetAndTypeAndStatus(budget, BudgetCalculationType.BUDGETED, Status.ASSIGNED);
        assertThat(assignedCalculations).hasSize(28);

        final List<BudgetCalculation> unassignedCalculations = budgetCalculationRepository.findByBudgetAndTypeAndStatus(budget, BudgetCalculationType.BUDGETED, Status.NEW);
        assertThat(unassignedCalculations).hasSize(5);
        for (BudgetCalculation calc : unassignedCalculations){
            assertThat(calc.getUnit().getReference()).isEqualTo("BUD-006");
        }

        // then - regarding calculation results and lease terms
        final List<BudgetCalculationResult> calculationResults = budgetCalculationResultRepository.allBudgetCalculationResults();
        assertThat(calculationResults).hasSize(14);

        checkResultsAndTermsForLease(leasePoison, new ExpectedTestResult(2, Arrays.asList(U1_BVAL_1,U1_BVAL_2), Arrays.asList(invoiceCharge1, invoiceCharge2)));
        checkResultsAndTermsForLease(leaseMiracle, new ExpectedTestResult(2, Arrays.asList(U2_BVAL_1,U2_BVAL_2), Arrays.asList(invoiceCharge1, invoiceCharge2)));
        checkResultsAndTermsForLease(leaseHello3, new ExpectedTestResult(2, Arrays.asList(U3_BVAL_1,U3_BVAL_2), Arrays.asList(invoiceCharge1, invoiceCharge2)));
        checkResultsAndTermsForLease(leaseDago, new ExpectedTestResult(4, Arrays.asList(U4_BVAL_1.add(U7_BVAL_1),U4_BVAL_2.add(U7_BVAL_2), U4_BVAL_1.add(U7_BVAL_1), U4_BVAL_2.add(U7_BVAL_2)), Arrays.asList(invoiceCharge1, invoiceCharge2, invoiceCharge1, invoiceCharge2)));
        checkResultsAndTermsForLease(leaseNlBank, new ExpectedTestResult(2, Arrays.asList(U4_BVAL_1,U4_BVAL_2), Arrays.asList(invoiceCharge1, invoiceCharge2)));
        checkResultsAndTermsForLease(leaseHyper, new ExpectedTestResult(2, Arrays.asList(U5_BVAL_1,U5_BVAL_2), Arrays.asList(invoiceCharge1, invoiceCharge2)));
        checkResultsAndTermsForLease(leaseHello6, new ExpectedTestResult(0, Arrays.asList(), Arrays.asList()));


        // just to be explicit for lease with 2 occupancies
        assertThat(leaseDago.findItemsOfType(LeaseItemType.SERVICE_CHARGE)).hasSize(2);
        final LeaseItem firstScItem = leaseDago.findItemsOfType(LeaseItemType.SERVICE_CHARGE).get(0);
        assertThat(firstScItem.getCharge()).isEqualTo(invoiceCharge1);
        assertThat(firstScItem.getTerms()).hasSize(1);
        final LeaseTermForServiceCharge term = (LeaseTermForServiceCharge) firstScItem.getTerms().first();
        assertThat(term.getBudgetedValue()).isEqualTo(U4_BVAL_1.add(U7_BVAL_1));
        assertThat(budgetCalculationResultRepository.findByLeaseTerm(term)).hasSize(2);

        final LeaseItem secondScItem = leaseDago.findItemsOfType(LeaseItemType.SERVICE_CHARGE).get(1);
        assertThat(secondScItem.getCharge()).isEqualTo(invoiceCharge2);
        assertThat(secondScItem.getTerms()).hasSize(1);
        final LeaseTermForServiceCharge term2 = (LeaseTermForServiceCharge) secondScItem.getTerms().first();
        assertThat(term2.getBudgetedValue()).isEqualTo(U4_BVAL_2.add(U7_BVAL_2));
        assertThat(budgetCalculationResultRepository.findByLeaseTerm(term2)).hasSize(2);

    }

    public void when_trying_to_calculate_again() throws Exception {

        // expect
        expectedExceptions.expect(DisabledException.class);
        expectedExceptions.expectMessage("All items are calculated and assigned already");

        // when
        wrap(mixin(Budget_Calculate.class, budget)).calculate(false);

    }

    public void assignBudgetWhenUpdated() throws Exception {

    }

    public void assignBudgetWhenAudited() throws Exception {

    }

    public void assignBudgetWhenAuditedAndUpdated() throws Exception {

    }

    private BigDecimal budgetedAmountFor(final Unit unit, final Charge invoiceCharge){

        BigDecimal resultValue = BigDecimal.ZERO;

        for (BudgetCalculation result : budgetCalculationRepository.findByBudgetAndUnitAndInvoiceChargeAndType(budget, unit, invoiceCharge, BudgetCalculationType.BUDGETED)){
            resultValue = resultValue.add(result.getValue());
        }

        return resultValue.setScale(2, RoundingMode.HALF_UP);
    }

    private void testCalculationsForUnit(final Unit unit, final Charge charge, final ExpectedUnitResult result){
        assertThat(budgetedAmountFor(unit, charge)).isEqualTo(result.getAmount());
        assertThat(budgetCalculationRepository.findByBudgetAndUnitAndInvoiceChargeAndType(budget, unit, charge, BudgetCalculationType.BUDGETED)).hasSize(result.getNumberOfCalculations());
    }

    @Getter
    @AllArgsConstructor
    private class ExpectedUnitResult {


        private int numberOfCalculations;

        private BigDecimal amount;

    }

    private void checkResultsAndTermsForLease(final Lease lease, final ExpectedTestResult result){

        final List<BudgetCalculationResult> calcsForLease = budgetCalculationResultRepository.allBudgetCalculationResults().stream().filter(r -> r.getOccupancy().getLease().equals(lease)).collect(Collectors.toList());
        assertThat(calcsForLease).hasSize(result.getNumberOfResults());

        for (int i=0; i < result.numberOfResults; i++){
            final LeaseTermForServiceCharge term = calcsForLease.get(i).getLeaseTerm();
            assertThat(term.getBudgetedValue()).isEqualTo(result.getBudgetedValues().get(i));
            assertThat(term.getAuditedValue()).isNull();
            assertThat(term.getLeaseItem().getCharge()).isEqualTo(result.getInvoiceCharges().get(i));
            assertThat(term.getStartDate()).isEqualTo(budget.getStartDate());
            assertThat(term.getEndDate()).isEqualTo(budget.getEndDate());
        }

    }

    @Getter
    @AllArgsConstructor
    private class ExpectedTestResult {

        private int numberOfResults;

        private List<BigDecimal> budgetedValues;

        private List<Charge> invoiceCharges;

    }

}

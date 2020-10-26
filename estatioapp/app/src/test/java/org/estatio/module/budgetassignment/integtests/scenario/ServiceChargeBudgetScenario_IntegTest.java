package org.estatio.module.budgetassignment.integtests.scenario;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.asset.dom.Unit;
import org.estatio.module.asset.dom.UnitRepository;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculation;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationRepository;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationService;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.budget.dom.budgetcalculation.Status;
import org.estatio.module.budget.dom.keytable.KeyTable;
import org.estatio.module.budget.fixtures.budgets.enums.Budget_enum;
import org.estatio.module.budget.fixtures.partitioning.enums.Partitioning_enum;
import org.estatio.module.budgetassignment.contributions.Budget_assign;
import org.estatio.module.budgetassignment.contributions.Budget_reconcile;
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
        key2.getItems().last().delete();
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
        assign_budget();
        calculate_audited();
        reconcile_budget();
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
        budgetCalculationService.calculate(budget, BudgetCalculationType.BUDGETED, budget.getStartDate(), budget.getEndDate(), true);

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
        budgetCalculationService.calculate(budget, BudgetCalculationType.BUDGETED, budget.getStartDate(), budget.getEndDate(), true);

        calculations = budgetCalculationRepository.findByBudget(budget);

        // then still
        assertThat(calculations.size()).isEqualTo(33);
        calculations.stream().forEach(x->assertThat(x.getStatus()).isEqualTo(Status.NEW));

    }

    public void assign_budget() throws Exception {

        // given
        // when
        wrap(mixin(Budget_assign.class, budget)).assign(false);
        transactionService.nextTransaction();
        calculations = budgetCalculationRepository.findByBudget(budget);

        // then - regarding calculations
        assertThat(budget.getStatus()).isEqualTo(org.estatio.module.budget.dom.budget.Status.ASSIGNED);
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

        checkResultsAndTermsForLease(leasePoison, BudgetCalculationType.BUDGETED, new ExpectedTestResult(2, Arrays.asList(U1_BVAL_1,U1_BVAL_2), Arrays.asList(null,null), Arrays.asList(invoiceCharge1, invoiceCharge2)));
        checkResultsAndTermsForLease(leaseMiracle, BudgetCalculationType.BUDGETED, new ExpectedTestResult(2, Arrays.asList(U2_BVAL_1,U2_BVAL_2), Arrays.asList(null,null), Arrays.asList(invoiceCharge1, invoiceCharge2)));
        checkResultsAndTermsForLease(leaseHello3, BudgetCalculationType.BUDGETED, new ExpectedTestResult(2, Arrays.asList(U3_BVAL_1,U3_BVAL_2), Arrays.asList(null,null), Arrays.asList(invoiceCharge1, invoiceCharge2)));
        checkResultsAndTermsForLease(leaseDago, BudgetCalculationType.BUDGETED, new ExpectedTestResult(4, Arrays.asList(U4_BVAL_1.add(U7_BVAL_1),U4_BVAL_2.add(U7_BVAL_2), U4_BVAL_1.add(U7_BVAL_1), U4_BVAL_2.add(U7_BVAL_2)), Arrays.asList(null,null,null,null), Arrays.asList(invoiceCharge1, invoiceCharge2, invoiceCharge1, invoiceCharge2)));
        checkResultsAndTermsForLease(leaseNlBank, BudgetCalculationType.BUDGETED, new ExpectedTestResult(2, Arrays.asList(U4_BVAL_1,U4_BVAL_2), Arrays.asList(null,null), Arrays.asList(invoiceCharge1, invoiceCharge2)));
        checkResultsAndTermsForLease(leaseHyper, BudgetCalculationType.BUDGETED, new ExpectedTestResult(2, Arrays.asList(U5_BVAL_1,U5_BVAL_2), Arrays.asList(null,null), Arrays.asList(invoiceCharge1, invoiceCharge2)));
        checkResultsAndTermsForLease(leaseHello6, BudgetCalculationType.BUDGETED, new ExpectedTestResult(0, Arrays.asList(), Arrays.asList(), Arrays.asList()));


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

    public void calculate_audited() throws Exception {

        // given
        budgetCalculationService.calculate(budget, BudgetCalculationType.AUDITED, budget.getStartDate(), budget.getEndDate(), true);
        calculations = budgetCalculationRepository.findByBudget(budget);
        assertThat(calculations).hasSize(33);

        // when creating partitioning of type audited
        budget.newPartitioning();
        budgetCalculationService.calculate(budget, BudgetCalculationType.AUDITED, budget.getStartDate(), budget.getEndDate(), true);
        calculations = budgetCalculationRepository.findByBudget(budget);

        // then still
        assertThat(calculations).hasSize(33);

        // when adding audited values to budget items
        Lists.newArrayList(budget.getItems()).forEach(i->{
            i.upsertValue(i.getBudgetedValue(), budget.getStartDate(), BudgetCalculationType.AUDITED);
        });
        budgetCalculationService.calculate(budget, BudgetCalculationType.AUDITED, budget.getStartDate(), budget.getEndDate(), true);
        calculations = budgetCalculationRepository.findByBudget(budget);

        // then
        assertThat(calculations.size()).isEqualTo(66);
        final List<BudgetCalculation> budgetedCalculations = calculations.stream().filter(c -> c.getCalculationType() == BudgetCalculationType.BUDGETED).collect(Collectors.toList());
        assertThat(budgetedCalculations).hasSize(33); // still
        assertThat(budgetedCalculations.stream().filter(c->c.getStatus()==Status.ASSIGNED).collect(Collectors.toList())).hasSize(28); // still
        assertThat(budgetedCalculations.stream().filter(c->c.getStatus()==Status.NEW).collect(Collectors.toList())).hasSize(5); // still

        final List<BudgetCalculation> auditedCalculations = calculations.stream().filter(c -> c.getCalculationType() == BudgetCalculationType.AUDITED).collect(Collectors.toList());
        assertThat(auditedCalculations).hasSize(33);
            auditedCalculations.forEach(c->{
            assertThat(c.getStatus()==Status.NEW);
        });


    }

    public void reconcile_budget() throws Exception {

        // when
        wrap(mixin(Budget_reconcile.class, budget)).reconcile(false);
        transactionService.nextTransaction(); // done for rounding to be applied

        // then
        final List<BudgetCalculation> assignedAuditedCalculations = budgetCalculationRepository.findByBudgetAndTypeAndStatus(budget, BudgetCalculationType.AUDITED, Status.ASSIGNED);
        assertThat(assignedAuditedCalculations).hasSize(28);

        final List<BudgetCalculation> newAuditedCalculations = budgetCalculationRepository.findByBudgetAndTypeAndStatus(budget, BudgetCalculationType.AUDITED, Status.NEW);
        assertThat(newAuditedCalculations).hasSize(5);

        final List<BudgetCalculationResult> allResults = budgetCalculationResultRepository.findByBudget(budget);
        final List<BudgetCalculationResult> budgetedResults = allResults.stream().filter(r->r.getType()==BudgetCalculationType.BUDGETED).collect(Collectors.toList());
        assertThat(budgetedResults).hasSize(14); // still
        final List<BudgetCalculationResult> auditedResults = allResults.stream().filter(r->r.getType()==BudgetCalculationType.AUDITED).collect(Collectors.toList());
        assertThat(auditedResults).hasSize(14);

        // check lease poison
        LeaseItem item1 = leasePoison.findFirstItemOfTypeAndCharge(LeaseItemType.SERVICE_CHARGE, invoiceCharge1);
        LeaseTermForServiceCharge term1 = (LeaseTermForServiceCharge) item1.getTerms().first();
        LeaseItem item2 = leasePoison.findFirstItemOfTypeAndCharge(LeaseItemType.SERVICE_CHARGE, invoiceCharge2);
        LeaseTermForServiceCharge term2 = (LeaseTermForServiceCharge) item2.getTerms().first();

        assertThat(term1.getBudgetedValue()).isEqualTo(U1_BVAL_1);
        assertThat(term1.getAuditedValue()).isEqualTo(U1_BVAL_1.subtract(BigDecimal.valueOf(357.14))); // because partition on budget item 1 has fixed budgeted amount, but no fixed audited amount
        assertThat(term2.getBudgetedValue()).isEqualTo(U1_BVAL_2);
        assertThat(term2.getAuditedValue()).isEqualTo(U1_BVAL_2);

        checkResultsAndTermsForLease(leasePoison, BudgetCalculationType.AUDITED, new ExpectedTestResult(2, Arrays.asList(U1_BVAL_1,U1_BVAL_2), Arrays.asList(U1_BVAL_1.subtract(BigDecimal.valueOf(357.14)),U1_BVAL_2), Arrays.asList(invoiceCharge1, invoiceCharge2)));
        checkResultsAndTermsForLease(leaseHello6, BudgetCalculationType.AUDITED, new ExpectedTestResult(0, Arrays.asList(), Arrays.asList(), Arrays.asList()));

        // all other occupancies do no cover the budget period and therefore the lease terms are not touched
        checkLeaseTermsNotTouchedForReconciliation(leaseMiracle,2);
        checkLeaseTermsNotTouchedForReconciliation(leaseHello3, 2);
        checkLeaseTermsNotTouchedForReconciliation(leaseDago, 4);
        checkLeaseTermsNotTouchedForReconciliation(leaseNlBank, 2);
        checkLeaseTermsNotTouchedForReconciliation(leaseHyper, 2);

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
    private void checkResultsAndTermsForLease(final Lease lease, final BudgetCalculationType budgetCalculationType, final ExpectedTestResult result){

        final List<BudgetCalculationResult> calcsForLease = budgetCalculationResultRepository.allBudgetCalculationResults()
                .stream()
                .filter(r->r.getType()==budgetCalculationType)
                .filter(r -> r.getOccupancy().getLease().equals(lease)).collect(Collectors.toList());
        assertThat(calcsForLease).hasSize(result.getNumberOfResults());

        for (int i=0; i < result.numberOfResults; i++){
            final LeaseTermForServiceCharge term = calcsForLease.get(i).getLeaseTerm();
            assertThat(term.getBudgetedValue()).isEqualTo(result.getBudgetedValues().get(i));
            assertThat(term.getAuditedValue()).isEqualTo(result.getAuditedValues().get(i));
            assertThat(term.getLeaseItem().getCharge()).isEqualTo(result.getInvoiceCharges().get(i));
            assertThat(term.getStartDate()).isEqualTo(budget.getStartDate());
            assertThat(term.getEndDate()).isEqualTo(budget.getEndDate());
        }

    }

    private void checkLeaseTermsNotTouchedForReconciliation(final Lease lease, final int numberOfResultsForAudited) {
        final List<BudgetCalculationResult> calcsForLease = budgetCalculationResultRepository.allBudgetCalculationResults()
                .stream()
                .filter(r->r.getType()==BudgetCalculationType.AUDITED)
                .filter(r -> r.getOccupancy().getLease().equals(lease)).collect(Collectors.toList());
        assertThat(calcsForLease).hasSize(numberOfResultsForAudited);
        for (int i=0; i < numberOfResultsForAudited; i++){
            final LeaseTermForServiceCharge term = calcsForLease.get(i).getLeaseTerm();
            assertThat(term).isNull();
        }
    }

    @Getter
    @AllArgsConstructor
    private class ExpectedTestResult {


        private int numberOfResults;

        private List<BigDecimal> budgetedValues;

        private List<BigDecimal> auditedValues;

        private List<Charge> invoiceCharges;

    }

    @Inject BudgetCalculationService budgetCalculationService;

}

package org.estatio.module.budgetassignment.integtests.scenario;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budget.BudgetRepository;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculation;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationRepository;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationService;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.budget.dom.budgetcalculation.Status;
import org.estatio.module.budget.dom.keytable.KeyTable;
import org.estatio.module.budget.fixtures.budgets.enums.Budget_enum;
import org.estatio.module.budget.fixtures.partitioning.enums.Partitioning_enum;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResult;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResultLink;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResultLinkRepository;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResultRepository;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationRun;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationRunRepository;
import org.estatio.module.budgetassignment.dom.override.BudgetOverrideForFlatRate;
import org.estatio.module.budgetassignment.dom.override.BudgetOverrideForMax;
import org.estatio.module.budgetassignment.dom.override.BudgetOverrideRepository;
import org.estatio.module.budgetassignment.dom.override.BudgetOverrideType;
import org.estatio.module.budgetassignment.dom.override.BudgetOverrideValue;
import org.estatio.module.budgetassignment.dom.override.BudgetOverrideValueRepository;
import org.estatio.module.budgetassignment.dom.service.BudgetAssignmentService;
import org.estatio.module.budgetassignment.dom.service.CalculationResultViewModel;
import org.estatio.module.budgetassignment.dom.service.DetailedCalculationResultViewmodel;
import org.estatio.module.budgetassignment.fixtures.override.enums.BudgetOverrideForFlatRate_enum;
import org.estatio.module.budgetassignment.fixtures.override.enums.BudgetOverrideForMax_enum;
import org.estatio.module.budgetassignment.integtests.BudgetAssignmentModuleIntegTestAbstract;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.charge.fixtures.charges.enums.Charge_enum;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.lease.dom.InvoicingFrequency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItemRepository;
import org.estatio.module.lease.dom.LeaseItemStatus;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.LeaseTermForServiceCharge;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;

import static org.assertj.core.api.Assertions.assertThat;

public class ServiceChargeBudgetScenario_IntegTest extends BudgetAssignmentModuleIntegTestAbstract {

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    BudgetRepository budgetRepository;

    @Inject
    BudgetCalculationService budgetCalculationService;

    @Inject
    BudgetAssignmentService budgetAssignmentService;

    @Inject
    LeaseRepository leaseRepository;

    @Inject
    LeaseItemRepository leaseItemRepository;

    @Inject
    ChargeRepository chargeRepository;

    @Inject
    BudgetOverrideRepository budgetOverrideRepository;

    @Inject
    BudgetOverrideValueRepository budgetOverrideValueRepository;

    @Inject
    BudgetCalculationRunRepository budgetCalculationRunRepository;

    @Inject
    BudgetCalculationResultRepository budgetCalculationResultRepository;

    @Inject
    BudgetCalculationResultLinkRepository budgetCalculationResultLinkRepository;

    @Inject
    BudgetCalculationRepository budgetCalculationRepository;

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
                executionContext.executeChildT(this, BudgetOverrideForFlatRate_enum.BudMiracle002Nl_2015.builder());
                executionContext.executeChildT(this, BudgetOverrideForMax_enum.BudPoison001Nl_2015.builder());
          }
        });
    }


    public static class Calculate extends ServiceChargeBudgetScenario_IntegTest {

        Budget budget;
        List<BudgetCalculation> calculations;
        List<BudgetCalculationRun> calculationRuns;
        List<CalculationResultViewModel> calculationResultViewModels;
        List<DetailedCalculationResultViewmodel> detailedCalculationResultViewmodels;

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

        @Test
        public void fullScenarioTest() throws Exception {
            calculate();
            detailedCalculation();
            calculateResultsForLeases();
            assignResults();
            finalCalculationIsIdemPotent();
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
        public static BigDecimal BVAL_POISON_1 = new BigDecimal("1921.43");
        public static BigDecimal BVAL_MIRACLE_1 = new BigDecimal("1125.00");



        public void calculate() throws Exception {

            // given

            // when
            calculations = wrap(budgetCalculationService).calculatePersistedCalculations(budget);
            calculationRuns = wrap(budgetAssignmentService).calculateResultsForLeases(budget, BudgetCalculationType.BUDGETED);
            calculationResultViewModels = budgetAssignmentService.getCalculationResults(budget);

            // then
            assertThat(calculationRuns.size()).isEqualTo(6);
            assertThat(calculationResultViewModels.size()).isEqualTo(12);
            assertThat(calculations.size()).isEqualTo(33);
            assertThat(budgetedAmountFor(leasePoison, invoiceCharge1)).isEqualTo(U1_BVAL_1);
            assertThat(budgetedAmountFor(leasePoison, invoiceCharge2)).isEqualTo(U1_BVAL_2);
            assertThat(budgetedAmountFor(leaseMiracle, invoiceCharge1)).isEqualTo(U2_BVAL_1);
            assertThat(budgetedAmountFor(leaseMiracle, invoiceCharge2)).isEqualTo(U2_BVAL_2);
            assertThat(budgetedAmountFor(leaseHello3, invoiceCharge1)).isEqualTo(U3_BVAL_1);
            assertThat(budgetedAmountFor(leaseHello3, invoiceCharge2)).isEqualTo(U3_BVAL_2);
            assertThat(budgetedAmountFor(leaseDago, invoiceCharge1)).isEqualTo(new BigDecimal("11214.29"));
            assertThat(budgetedAmountFor(leaseDago, invoiceCharge1)).isEqualTo(U4_BVAL_1.add(U7_BVAL_1));
            assertThat(budgetedAmountFor(leaseDago, invoiceCharge2)).isEqualTo(new BigDecimal("10607.14"));
            assertThat(budgetedAmountFor(leaseDago, invoiceCharge2)).isEqualTo(U4_BVAL_2.add(U7_BVAL_2));
            assertThat(budgetedAmountFor(leaseNlBank, invoiceCharge1)).isEqualTo(U4_BVAL_1);
            assertThat(budgetedAmountFor(leaseNlBank, invoiceCharge2)).isEqualTo(U4_BVAL_2);
            assertThat(budgetedAmountFor(leaseHyper, invoiceCharge1)).isEqualTo(U5_BVAL_1);
            assertThat(budgetedAmountFor(leaseHyper, invoiceCharge2)).isEqualTo(U5_BVAL_2);

        }

        private BigDecimal budgetedAmountFor(final Lease lease, final Charge invoiceCharge){

            BigDecimal resultValue = BigDecimal.ZERO;

            for (CalculationResultViewModel result : resultsForLease(lease.getReference(), invoiceCharge.getReference())){
                resultValue = resultValue.add(result.getBudgetedValue());
            }

            return resultValue;
        }

        private List<CalculationResultViewModel> resultsForLease(final String leaseReference, final String invoiceChargeReference){
            return calculationResultViewModels.stream().filter(x ->x.getLeaseReference().equals(leaseReference) && x.getInvoiceCharge().equals(invoiceChargeReference)).collect(Collectors.toList());
        }

        public void detailedCalculation() throws Exception {


            // when
            detailedCalculationResultViewmodels = budgetAssignmentService.getDetailedCalculationResults(leaseDago, budget, BudgetCalculationType.BUDGETED);

            // then
            assertThat(detailedCalculationResultViewmodels.size()).isEqualTo(8);

            assertThat(detailedCalculationResultViewmodels.get(0).getValueForLease()).isEqualTo(new BigDecimal("1428.571430"));
            assertThat(detailedCalculationResultViewmodels.get(0).getInvoiceCharge()).isEqualTo(invoiceCharge1.getReference());

            assertThat(detailedCalculationResultViewmodels.get(1).getValueForLease()).isEqualTo(new BigDecimal("2285.714288"));
            assertThat(detailedCalculationResultViewmodels.get(1).getInvoiceCharge()).isEqualTo(invoiceCharge1.getReference());

            assertThat(detailedCalculationResultViewmodels.get(2).getValueForLease()).isEqualTo(new BigDecimal("571.428572"));
            assertThat(detailedCalculationResultViewmodels.get(2).getInvoiceCharge()).isEqualTo(invoiceCharge1.getReference());

            assertThat(detailedCalculationResultViewmodels.get(3).getValueForLease()).isEqualTo(new BigDecimal("428.571429"));
            assertThat(detailedCalculationResultViewmodels.get(3).getInvoiceCharge()).isEqualTo(invoiceCharge1.getReference());

            assertThat(detailedCalculationResultViewmodels.get(4).getValueForLease()).isEqualTo(new BigDecimal("2500.000000"));
            assertThat(detailedCalculationResultViewmodels.get(4).getInvoiceCharge()).isEqualTo(invoiceCharge1.getReference());

            assertThat(detailedCalculationResultViewmodels.get(5).getValueForLease()).isEqualTo(new BigDecimal("4000.000000"));
            assertThat(detailedCalculationResultViewmodels.get(5).getInvoiceCharge()).isEqualTo(invoiceCharge1.getReference());

            assertThat(detailedCalculationResultViewmodels.get(6).getValueForLease()).isEqualTo(new BigDecimal("3857.142861"));
            assertThat(detailedCalculationResultViewmodels.get(6).getInvoiceCharge()).isEqualTo(invoiceCharge2.getReference());

            assertThat(detailedCalculationResultViewmodels.get(7).getValueForLease()).isEqualTo(new BigDecimal("6750.000000"));
            assertThat(detailedCalculationResultViewmodels.get(7).getInvoiceCharge()).isEqualTo(invoiceCharge2.getReference());

            // and when
            detailedCalculationResultViewmodels = budgetAssignmentService.getDetailedCalculationResults(leaseMiracle, budget, BudgetCalculationType.BUDGETED);

            // then
            assertThat(detailedCalculationResultViewmodels.size()).isEqualTo(2);

            assertThat(detailedCalculationResultViewmodels.get(0).getEffectiveValueForLease()).isEqualTo(new BigDecimal("1125.00"));
            assertThat(detailedCalculationResultViewmodels.get(0).getValueForLease()).isEqualTo(new BigDecimal("2857.14"));
            assertThat(detailedCalculationResultViewmodels.get(0).getShortfall()).isEqualTo(new BigDecimal("1732.14"));
            assertThat(detailedCalculationResultViewmodels.get(0).getInvoiceCharge()).isEqualTo(invoiceCharge1.getReference());
            assertThat(detailedCalculationResultViewmodels.get(0).getIncomingCharge()).isEqualTo("Override for total NLD_SERVICE_CHARGE");

            assertThat(detailedCalculationResultViewmodels.get(1).getValueForLease()).isEqualTo(new BigDecimal("1928.571417"));
            assertThat(detailedCalculationResultViewmodels.get(1).getInvoiceCharge()).isEqualTo(invoiceCharge2.getReference());

        }

        public void calculateResultsForLeases() throws Exception {

            assertThat(budgetCalculationRunRepository.findByLease(leasePoison).size()).isEqualTo(1);
            assertThat(budgetCalculationRunRepository.findByLease(leaseMiracle).size()).isEqualTo(1);
            assertThat(budgetCalculationRunRepository.findByLease(leaseHello3).size()).isEqualTo(1);
            assertThat(budgetCalculationRunRepository.findByLease(leaseDago).size()).isEqualTo(1);
            assertThat(budgetCalculationRunRepository.findByLease(leaseNlBank).size()).isEqualTo(1);
            assertThat(budgetCalculationRunRepository.findByLease(leaseHyper).size()).isEqualTo(1);
            assertThat(budgetCalculationRunRepository.findByLease(leaseHello6).size()).isEqualTo(0);

            BudgetCalculationRun rPoison = budgetCalculationRunRepository.findByLease(leasePoison).get(0);
            assertThat(rPoison.getType()).isEqualTo(BudgetCalculationType.BUDGETED);
            assertThat(rPoison.getStatus()).isEqualTo(Status.NEW);
            assertThat(rPoison.getBudgetCalculationResults().size()).isEqualTo(2);

            BudgetCalculationResult cResPoison1 = budgetCalculationResultRepository.findUnique(rPoison, invoiceCharge1);
            assertThat(cResPoison1.getValue()).isEqualTo(BVAL_POISON_1);
            assertThat(cResPoison1.getShortfall()).isEqualTo(new BigDecimal("7.14"));

            BudgetCalculationResult cResPoison2 = budgetCalculationResultRepository.findUnique(rPoison, invoiceCharge2);
            assertThat(cResPoison2.getValue()).isEqualTo(U1_BVAL_2.setScale(2, BigDecimal.ROUND_HALF_UP));
            assertThat(cResPoison2.getShortfall()).isEqualTo(new BigDecimal("0.00"));

            BudgetCalculationRun rMiracle = budgetCalculationRunRepository.findByLease(leaseMiracle).get(0);
            assertThat(rMiracle.getType()).isEqualTo(BudgetCalculationType.BUDGETED);
            assertThat(rMiracle.getStatus()).isEqualTo(Status.NEW);
            assertThat(rMiracle.getBudgetCalculationResults().size()).isEqualTo(2);

            BudgetCalculationResult cResMiracle1 = budgetCalculationResultRepository.findUnique(rMiracle, invoiceCharge1);
            assertThat(cResMiracle1.getValue()).isEqualTo(BVAL_MIRACLE_1);
            assertThat(cResMiracle1.getShortfall()).isEqualTo(new BigDecimal("1732.14"));

            BudgetCalculationResult cResMiracle2 = budgetCalculationResultRepository.findUnique(rMiracle, invoiceCharge2);
            assertThat(cResMiracle2.getValue()).isEqualTo(U2_BVAL_2.setScale(2, BigDecimal.ROUND_HALF_UP));
            assertThat(cResMiracle2.getShortfall()).isEqualTo(new BigDecimal("0.00"));

            assertThat(budgetOverrideRepository.findByLease(leasePoison).size()).isEqualTo(1);
            BudgetOverrideForMax oPoison = (BudgetOverrideForMax) budgetOverrideRepository.findByLease(leasePoison).get(0);
            assertThat(oPoison.getType()).isEqualTo(BudgetCalculationType.BUDGETED);
            assertThat(oPoison.getInvoiceCharge()).isEqualTo(invoiceCharge1);
            assertThat(oPoison.getIncomingCharge()).isEqualTo(incomingCharge);
            assertThat(oPoison.getStartDate()).isEqualTo(budget.getStartDate());
            assertThat(oPoison.getEndDate()).isNull();
            assertThat(oPoison.getReason()).isEqualTo(BudgetOverrideType.CEILING.reason);
            assertThat(oPoison.getMaxValue()).isEqualTo(new BigDecimal("350.00"));
            assertThat(oPoison.getValues().size()).isEqualTo(1);
            assertThat(oPoison.getValues().first().getValue()).isEqualTo(new BigDecimal("350.00"));
            assertThat(oPoison.getValues().first().getType()).isEqualTo(BudgetCalculationType.BUDGETED);
            assertThat(oPoison.getValues().first().getStatus()).isEqualTo(Status.NEW);

            assertThat(budgetOverrideRepository.findByLease(leaseMiracle).size()).isEqualTo(1);
            BudgetOverrideForFlatRate oMiracle = (BudgetOverrideForFlatRate) budgetOverrideRepository.findByLease(leaseMiracle).get(0);
            assertThat(oMiracle.getType()).isEqualTo(BudgetCalculationType.BUDGETED);
            assertThat(oMiracle.getInvoiceCharge()).isEqualTo(invoiceCharge1);
            assertThat(oMiracle.getIncomingCharge()).isNull();
            assertThat(oMiracle.getStartDate()).isEqualTo(budget.getStartDate());
            assertThat(oMiracle.getEndDate()).isNull();
            assertThat(oMiracle.getReason()).isEqualTo(BudgetOverrideType.FLATRATE.reason);
            assertThat(oMiracle.getValuePerM2()).isEqualTo(new BigDecimal("12.50"));
            assertThat(oMiracle.getWeightedArea()).isEqualTo(new BigDecimal("90.00"));
            assertThat(oMiracle.getValues().size()).isEqualTo(1);
            assertThat(oMiracle.getValues().first().getValue()).isEqualTo(new BigDecimal("1125.0000"));
            assertThat(oMiracle.getValues().first().getType()).isEqualTo(BudgetCalculationType.BUDGETED);
            assertThat(oMiracle.getValues().first().getStatus()).isEqualTo(Status.NEW);

        }

        public void assignResults() throws Exception {

            // when
            budgetAssignmentService.assign(budget);

            // then
            validateLeaseItemsAndTerms(
                    leasePoison,
                    Arrays.asList(BVAL_POISON_1, U1_BVAL_2.setScale(2, BigDecimal.ROUND_HALF_UP)),
                    Budget_enum.BudBudget2015.getStartDate()
            );
            validateLeaseItemsAndTerms(
                    leaseMiracle,
                    Arrays.asList(BVAL_MIRACLE_1, U2_BVAL_2.setScale(2, BigDecimal.ROUND_HALF_UP)),
                    Budget_enum.BudBudget2015.getStartDate()
            );
            validateLeaseItemsAndTerms(
                    leaseHello3,
                    Arrays.asList(U3_BVAL_1.setScale(2, BigDecimal.ROUND_HALF_UP), U3_BVAL_2.setScale(2, BigDecimal.ROUND_HALF_UP)),
                    leaseHello3.getStartDate()
            );
            validateLeaseItemsAndTerms(
                    leaseDago,
                    Arrays.asList(U4_BVAL_1.add(U7_BVAL_1).setScale(2, BigDecimal.ROUND_HALF_UP), U4_BVAL_2.add(U7_BVAL_2).setScale(2, BigDecimal.ROUND_HALF_UP)),
                    Budget_enum.BudBudget2015.getStartDate()
            );
            validateLeaseItemsAndTerms(
                    leaseNlBank,
                    Arrays.asList(U4_BVAL_1.setScale(2, BigDecimal.ROUND_HALF_UP), U4_BVAL_2.setScale(2, BigDecimal.ROUND_HALF_UP)),
                    leaseNlBank.getStartDate()
            );
            validateLeaseItemsAndTerms(
                    leaseHyper,
                    Arrays.asList(U5_BVAL_1.setScale(2, BigDecimal.ROUND_HALF_UP), U5_BVAL_2.setScale(2, BigDecimal.ROUND_HALF_UP)),
                    leaseHyper.getStartDate()
            );

            assertThat(leaseItemRepository.findLeaseItemsByType(leaseHello6, LeaseItemType.SERVICE_CHARGE_BUDGETED).size()).isEqualTo(0);

        }

        private void validateLeaseItemsAndTerms(final Lease lease, final List<BigDecimal> values, final LocalDate startDate) {

            assertThat(leaseItemRepository.findLeaseItemsByType(lease, LeaseItemType.SERVICE_CHARGE).size()).isEqualTo(2);

            assertThat(lease.getItems().first().getCharge()).isEqualTo(invoiceCharge1);
            assertThat(lease.getItems().first().getStartDate()).isEqualTo(startDate);
            assertThat(lease.getItems().first().getPaymentMethod()).isEqualTo(PaymentMethod.DIRECT_DEBIT);
            assertThat(lease.getItems().first().getInvoicingFrequency()).isEqualTo(InvoicingFrequency.QUARTERLY_IN_ADVANCE);
            assertThat(lease.getItems().first().getStatus()).isEqualTo(LeaseItemStatus.ACTIVE);
            assertThat(lease.getItems().first().getTerms().size()).isEqualTo(1);

            LeaseTermForServiceCharge term1 = (LeaseTermForServiceCharge) lease.getItems().first().getTerms().first();
            assertThat(budgetCalculationResultLinkRepository.findByLeaseTerm(term1).size()).isEqualTo(1);
            assertThat(term1.getBudgetedValue()).isEqualTo(values.get(0));
            assertThat(term1.getAuditedValue()).isNull();
            assertThat(term1.getStartDate()).isEqualTo(startDate);
            assertThat(term1.getEndDate()).isEqualTo(Budget_enum.BudBudget2015.getEndDate());

            assertThat(lease.getItems().last().getCharge()).isEqualTo(invoiceCharge2);
            assertThat(lease.getItems().last().getStartDate()).isEqualTo(startDate);
            assertThat(lease.getItems().last().getPaymentMethod()).isEqualTo(PaymentMethod.DIRECT_DEBIT);
            assertThat(lease.getItems().last().getInvoicingFrequency()).isEqualTo(InvoicingFrequency.QUARTERLY_IN_ADVANCE);
            assertThat(lease.getItems().last().getStatus()).isEqualTo(LeaseItemStatus.ACTIVE);
            assertThat(lease.getItems().last().getTerms().size()).isEqualTo(1);

            LeaseTermForServiceCharge term2 = (LeaseTermForServiceCharge) lease.getItems().last().getTerms().first();
            assertThat(budgetCalculationResultLinkRepository.findByLeaseTerm(term2).size()).isEqualTo(1);
            assertThat(term2.getBudgetedValue()).isEqualTo(values.get(1));
            assertThat(term2.getAuditedValue()).isNull();
            assertThat(term2.getStartDate()).isEqualTo(startDate);
            assertThat(term2.getEndDate()).isEqualTo(Budget_enum.BudBudget2015.getEndDate());

        }

        public void finalCalculationIsIdemPotent() throws Exception {

            BudgetCalculation c1Before;
            BudgetCalculation c1After;
            BudgetCalculationRun r1Before;
            BudgetCalculationRun r1After;
            BudgetCalculationResult res1Before;
            BudgetCalculationResult res1After;
            BudgetOverrideValue v1Before;
            BudgetOverrideValue v1After;
            BudgetCalculationResultLink l1Before;
            BudgetCalculationResultLink l1After;

            // given
            assertThat(budgetCalculationRepository.allBudgetCalculations().size()).isEqualTo(33);
            c1Before = budgetCalculationRepository.allBudgetCalculations().get(0);
            assertThat(c1Before.getStatus()).isEqualTo(Status.ASSIGNED);

            assertThat(budgetCalculationRunRepository.allBudgetCalculationRuns().size()).isEqualTo(6);
            r1Before = budgetCalculationRunRepository.allBudgetCalculationRuns().get(0);
            assertThat(r1Before.getStatus()).isEqualTo(Status.ASSIGNED);

            assertThat(budgetCalculationResultRepository.allBudgetCalculationResults().size()).isEqualTo(12);
            res1Before = budgetCalculationResultRepository.allBudgetCalculationResults().get(0);

            assertThat(budgetOverrideValueRepository.allBudgetOverrideValues().size()).isEqualTo(2);
            v1Before = budgetOverrideValueRepository.allBudgetOverrideValues().get(0);
            assertThat(v1Before.getStatus()).isEqualTo(Status.ASSIGNED);

            assertThat(budgetCalculationResultLinkRepository.allBudgetCalculationResultLinks().size()).isEqualTo(12);
            l1Before = budgetCalculationResultLinkRepository.allBudgetCalculationResultLinks().get(0);
            assertThat(l1Before.getLeaseTermForServiceCharge().getLeaseItem().getStatus()).isEqualTo(LeaseItemStatus.ACTIVE);

            // when
            budgetCalculationService.calculatePersistedCalculations(budget);
            budgetAssignmentService.calculateResultsForLeases(budget, BudgetCalculationType.BUDGETED);
            budgetAssignmentService.assign(budget);

            // then nothing should be changed
            assertThat(budgetCalculationRepository.allBudgetCalculations().size()).isEqualTo(33);
            c1After = budgetCalculationRepository.allBudgetCalculations().get(0);
            assertThat(c1Before).isEqualTo(c1After);

            assertThat(budgetCalculationRunRepository.allBudgetCalculationRuns().size()).isEqualTo(6);
            r1After = budgetCalculationRunRepository.allBudgetCalculationRuns().get(0);
            assertThat(r1Before).isEqualTo(r1After);

            assertThat(budgetCalculationResultRepository.allBudgetCalculationResults().size()).isEqualTo(12);
            res1After = budgetCalculationResultRepository.allBudgetCalculationResults().get(0);
            assertThat(res1Before).isEqualTo(res1After);

            assertThat(budgetOverrideValueRepository.allBudgetOverrideValues().size()).isEqualTo(2);
            v1After = budgetOverrideValueRepository.allBudgetOverrideValues().get(0);
            assertThat(v1Before).isEqualTo(v1After);

            assertThat(budgetCalculationResultLinkRepository.allBudgetCalculationResultLinks().size()).isEqualTo(12);
            l1After = budgetCalculationResultLinkRepository.allBudgetCalculationResultLinks().get(0);
            assertThat(l1Before).isEqualTo(l1After);
            assertThat(l1Before.getLeaseTermForServiceCharge().getLeaseItem()).isEqualTo(l1After.getLeaseTermForServiceCharge().getLeaseItem());

        }

        public void assignBudgetWhenUpdated() throws Exception {

        }

        public void assignBudgetWhenAudited() throws Exception {

        }

        public void assignBudgetWhenAuditedAndUpdated() throws Exception {

        }

    }

}

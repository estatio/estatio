package org.estatio.integtests.budgetassignment;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.budgetassignment.BudgetAssignmentService;
import org.estatio.dom.budgetassignment.calculationresult.BudgetCalculationResult;
import org.estatio.dom.budgetassignment.calculationresult.BudgetCalculationResultLink;
import org.estatio.dom.budgetassignment.calculationresult.BudgetCalculationResultLinkRepository;
import org.estatio.dom.budgetassignment.calculationresult.BudgetCalculationResultRepository;
import org.estatio.dom.budgetassignment.calculationresult.BudgetCalculationRun;
import org.estatio.dom.budgetassignment.calculationresult.BudgetCalculationRunRepository;
import org.estatio.dom.budgetassignment.override.BudgetOverride;
import org.estatio.dom.budgetassignment.override.BudgetOverrideForFlatRate;
import org.estatio.dom.budgetassignment.override.BudgetOverrideForMax;
import org.estatio.dom.budgetassignment.override.BudgetOverrideRepository;
import org.estatio.dom.budgetassignment.override.BudgetOverrideType;
import org.estatio.dom.budgetassignment.override.BudgetOverrideValue;
import org.estatio.dom.budgetassignment.override.BudgetOverrideValueRepository;
import org.estatio.dom.budgetassignment.viewmodels.BudgetCalculationResultViewModel;
import org.estatio.dom.budgetassignment.viewmodels.DetailedBudgetCalculationResultViewmodel;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budget.BudgetRepository;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculation;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationRepository;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationService;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationType;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationViewmodel;
import org.estatio.dom.budgeting.budgetcalculation.Status;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeRepository;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.lease.InvoicingFrequency;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseItemRepository;
import org.estatio.dom.lease.LeaseItemStatus;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.lease.LeaseRepository;
import org.estatio.dom.lease.LeaseTermForServiceCharge;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForBudNl;
import org.estatio.fixture.budget.BudgetForBud;
import org.estatio.fixture.budget.PartitionItemsForBud;
import org.estatio.fixture.charge.ChargeRefData;
import org.estatio.fixture.lease.LeasesForBudNl;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class

ServiceChargeBudgetScenarioTest extends EstatioIntegrationTest {

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
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, new PartitionItemsForBud());
            }
        });
    }


    public static class Calculate extends ServiceChargeBudgetScenarioTest {

        Property property;
        Budget budget;
        List<BudgetCalculation> calculations;
        List<BudgetCalculationRun> calculationRuns;
        List<BudgetOverride> overrides;
        List<BudgetCalculationViewmodel> budgetCalculationViewmodels;
        List<BudgetCalculationResultViewModel> budgetCalculationResultViewModels;
        List<DetailedBudgetCalculationResultViewmodel> detailedBudgetCalculationResultViewmodels;

        @Before
        public void setup() {
            // given
            property = propertyRepository.findPropertyByReference(PropertyForBudNl.REF);
            budget = budgetRepository.findByPropertyAndStartDate(property, BudgetForBud.BUDGET_2015_START_DATE);
            //**IMPORTANT!** truncate keytable
            KeyTable key2 = budget.getKeyTables().last();
            key2.getItems().last().deleteBudgetKeyItem();
        }

        @Test
        public void fullScenarioTest() throws Exception {
            calculate();
            detailedCalculation();
            createPersistedBudgetCalculations();
            calculateResultsForLeases();
            assignResults();
            finalCalculationIsIdemPotent();
//            assignBudgetWhenUpdated();
//            assignBudgetWhenAudited();
//            assignBudgetWhenAuditedAndUpdated();
        }

        public static BigDecimal U1_BVAL_1 = new BigDecimal("1928.571437");
        public static BigDecimal U1_BVAL_2 = new BigDecimal("964.285722");
        public static BigDecimal U2_BVAL_1 = new BigDecimal("2857.142847");
        public static BigDecimal U2_BVAL_2 = new BigDecimal("1928.571417");
        public static BigDecimal U3_BVAL_1 = new BigDecimal("3785.714283");
        public static BigDecimal U3_BVAL_2 = new BigDecimal("2892.857139");
        public static BigDecimal U4_BVAL_1 = new BigDecimal("4714.285719");
        public static BigDecimal U4_BVAL_2 = new BigDecimal("3857.142861");
        public static BigDecimal U5_BVAL_1 = new BigDecimal("5642.857155");
        public static BigDecimal U5_BVAL_2 = new BigDecimal("4821.428583");
        public static BigDecimal U6_BVAL_1 = new BigDecimal("6571.428565");
        public static BigDecimal U6_BVAL_2 = new BigDecimal("5785.714278");
        public static BigDecimal U7_BVAL_1 = new BigDecimal("6500.000000");
        public static BigDecimal U7_BVAL_2 = new BigDecimal("6750.000000");
        public static BigDecimal BVAL_POISON_1 = new BigDecimal("1921.43");
        public static BigDecimal BVAL_MIRACLE_1 = new BigDecimal("1125.00");

        public void calculate() throws Exception {

            // when
            budgetCalculationViewmodels = budgetCalculationService.getAllCalculations(budget);
            budgetCalculationResultViewModels = budgetAssignmentService.getAssignmentResults(budget);

            // then
            assertThat(budgetCalculationViewmodels.size()).isEqualTo(33);
            assertThat(budgetCalculationResultViewModels.size()).isEqualTo(20);
            assertThat(budgetedAmountFor(LeasesForBudNl.REF1, ChargeRefData.NL_SERVICE_CHARGE)).isEqualTo(U1_BVAL_1);
            assertThat(budgetedAmountFor(LeasesForBudNl.REF1, ChargeRefData.NL_SERVICE_CHARGE2)).isEqualTo(U1_BVAL_2);
            assertThat(budgetedAmountFor(LeasesForBudNl.REF2, ChargeRefData.NL_SERVICE_CHARGE)).isEqualTo(U2_BVAL_1);
            assertThat(budgetedAmountFor(LeasesForBudNl.REF2, ChargeRefData.NL_SERVICE_CHARGE2)).isEqualTo(U2_BVAL_2);
            assertThat(budgetedAmountFor(LeasesForBudNl.REF3, ChargeRefData.NL_SERVICE_CHARGE)).isEqualTo(U3_BVAL_1);
            assertThat(budgetedAmountFor(LeasesForBudNl.REF3, ChargeRefData.NL_SERVICE_CHARGE2)).isEqualTo(U3_BVAL_2);
            assertThat(budgetedAmountFor(LeasesForBudNl.REF4, ChargeRefData.NL_SERVICE_CHARGE)).isEqualTo(new BigDecimal("11214.285719"));
            assertThat(budgetedAmountFor(LeasesForBudNl.REF4, ChargeRefData.NL_SERVICE_CHARGE)).isEqualTo(U4_BVAL_1.add(U7_BVAL_1));
            assertThat(budgetedAmountFor(LeasesForBudNl.REF4, ChargeRefData.NL_SERVICE_CHARGE2)).isEqualTo(new BigDecimal("10607.142861"));
            assertThat(budgetedAmountFor(LeasesForBudNl.REF4, ChargeRefData.NL_SERVICE_CHARGE2)).isEqualTo(U4_BVAL_2.add(U7_BVAL_2));
            assertThat(budgetedAmountFor(LeasesForBudNl.REF4A, ChargeRefData.NL_SERVICE_CHARGE)).isEqualTo(U4_BVAL_1);
            assertThat(budgetedAmountFor(LeasesForBudNl.REF4A, ChargeRefData.NL_SERVICE_CHARGE2)).isEqualTo(U4_BVAL_2);
            assertThat(budgetedAmountFor(LeasesForBudNl.REF5, ChargeRefData.NL_SERVICE_CHARGE)).isEqualTo(U5_BVAL_1);
            assertThat(budgetedAmountFor(LeasesForBudNl.REF5, ChargeRefData.NL_SERVICE_CHARGE2)).isEqualTo(U5_BVAL_2);

        }

        private BigDecimal budgetedAmountFor(final String leaseReference, final String invoiceChargeReference){

            BigDecimal resultValue = BigDecimal.ZERO;

            for (BudgetCalculationResultViewModel result : resultsForLease(leaseReference, invoiceChargeReference)){
                resultValue = resultValue.add(result.getBudgetedValue());
            }

            return resultValue;
        }

        private List<BudgetCalculationResultViewModel> resultsForLease(final String leaseReference, final String invoiceChargeReference){
            return budgetCalculationResultViewModels.stream().filter(x ->x.getLeaseReference().equals(leaseReference) && x.getInvoiceCharge().equals(invoiceChargeReference)).collect(Collectors.toList());
        }

        public void detailedCalculation() throws Exception {

            String INCOMING_CHARGE_LABEL_1 = "NLD_INCOMING_CHARGE_1 Incoming Charge 1 (NLD) | budgeted 10000.00 | 100.00 % table1";
            String INCOMING_CHARGE_LABEL_2 = "NLD_INCOMING_CHARGE_2 Incoming Charge 2 (NLD) | budgeted 20000.00 | 80.00 % table1 | 20.00 % table2";
            String INCOMING_CHARGE_LABEL_3 = "NLD_INCOMING_CHARGE_3 Incoming Charge 3 (NLD) | budgeted 30000.00 | 90.00 % table1 | 10.00 % table2";

            // given
            Lease leaseForDago = leaseRepository.findLeaseByReference(LeasesForBudNl.REF4);

            // when
            budgetCalculationViewmodels = budgetCalculationService.getAllCalculations(budget);
            detailedBudgetCalculationResultViewmodels = budgetAssignmentService.getDetailedBudgetAssignmentResults(budget, leaseForDago);

            // then
            assertThat(detailedBudgetCalculationResultViewmodels.size()).isEqualTo(8);

            assertThat(detailedBudgetCalculationResultViewmodels.get(0).getBudgetedValue()).isEqualTo(new BigDecimal("1428.571430"));
            assertThat(detailedBudgetCalculationResultViewmodels.get(0).getIncomingCharge()).isEqualTo(INCOMING_CHARGE_LABEL_1);
            assertThat(detailedBudgetCalculationResultViewmodels.get(0).getInvoiceCharge()).isEqualTo(chargeRepository.findByReference(ChargeRefData.NL_SERVICE_CHARGE).getReference());

            assertThat(detailedBudgetCalculationResultViewmodels.get(1).getBudgetedValue()).isEqualTo(new BigDecimal("2285.714288"));
            assertThat(detailedBudgetCalculationResultViewmodels.get(1).getIncomingCharge()).isEqualTo(INCOMING_CHARGE_LABEL_2);
            assertThat(detailedBudgetCalculationResultViewmodels.get(1).getInvoiceCharge()).isEqualTo(chargeRepository.findByReference(ChargeRefData.NL_SERVICE_CHARGE).getReference());

            assertThat(detailedBudgetCalculationResultViewmodels.get(3).getBudgetedValue()).isEqualTo(new BigDecimal("3857.142861"));
            assertThat(detailedBudgetCalculationResultViewmodels.get(3).getIncomingCharge()).isEqualTo(INCOMING_CHARGE_LABEL_3);
            assertThat(detailedBudgetCalculationResultViewmodels.get(3).getInvoiceCharge()).isEqualTo(chargeRepository.findByReference(ChargeRefData.NL_SERVICE_CHARGE2).getReference());

            assertThat(detailedBudgetCalculationResultViewmodels.get(5).getBudgetedValue()).isEqualTo(new BigDecimal("2500.000000"));
            assertThat(detailedBudgetCalculationResultViewmodels.get(5).getIncomingCharge()).isEqualTo(INCOMING_CHARGE_LABEL_1);
            assertThat(detailedBudgetCalculationResultViewmodels.get(5).getInvoiceCharge()).isEqualTo(chargeRepository.findByReference(ChargeRefData.NL_SERVICE_CHARGE).getReference());

            assertThat(detailedBudgetCalculationResultViewmodels.get(7).getBudgetedValue()).isEqualTo(new BigDecimal("6750.000000"));
            assertThat(detailedBudgetCalculationResultViewmodels.get(7).getIncomingCharge()).isEqualTo(INCOMING_CHARGE_LABEL_3);
            assertThat(detailedBudgetCalculationResultViewmodels.get(7).getInvoiceCharge()).isEqualTo(chargeRepository.findByReference(ChargeRefData.NL_SERVICE_CHARGE2).getReference());

        }

        public void createPersistedBudgetCalculations() throws Exception {

            // when
            calculations = budgetCalculationService.calculatePersistedCalculations(budget);

            // then
            assertThat(calculations.size()).isEqualTo(33);

        }

        public void calculateResultsForLeases() throws Exception {

            // given
            Lease leasePoison = leaseRepository.findLeaseByReference(LeasesForBudNl.REF1);
            Lease leaseMiracle = leaseRepository.findLeaseByReference(LeasesForBudNl.REF2);
            Lease leaseHello3 = leaseRepository.findLeaseByReference(LeasesForBudNl.REF3);
            Lease leaseDago = leaseRepository.findLeaseByReference(LeasesForBudNl.REF4);
            Lease leaseNlBank = leaseRepository.findLeaseByReference(LeasesForBudNl.REF4A);
            Lease leaseHyper = leaseRepository.findLeaseByReference(LeasesForBudNl.REF5);
            Lease leaseHello6 = leaseRepository.findLeaseByReference(LeasesForBudNl.REF6);
            Charge invoiceCharge1 = chargeRepository.findByReference(ChargeRefData.NL_SERVICE_CHARGE);
            Charge invoiceCharge2 = chargeRepository.findByReference(ChargeRefData.NL_SERVICE_CHARGE2);
            Charge incomingCharge = chargeRepository.findByReference(ChargeRefData.NL_INCOMING_CHARGE_1);

            // when
            calculationRuns = budgetAssignmentService.calculateResultsForLeases(budget, BudgetCalculationType.BUDGETED);

            // then
            assertThat(calculationRuns.size()).isEqualTo(6);
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

            // given
            Lease leasePoison = leaseRepository.findLeaseByReference(LeasesForBudNl.REF1);
            Lease leaseMiracle = leaseRepository.findLeaseByReference(LeasesForBudNl.REF2);
            Lease leaseHello3 = leaseRepository.findLeaseByReference(LeasesForBudNl.REF3);
            Lease leaseDago = leaseRepository.findLeaseByReference(LeasesForBudNl.REF4);
            Lease leaseNlBank = leaseRepository.findLeaseByReference(LeasesForBudNl.REF4A);
            Lease leaseHyper = leaseRepository.findLeaseByReference(LeasesForBudNl.REF5);
            Lease leaseHello6 = leaseRepository.findLeaseByReference(LeasesForBudNl.REF6);

            // when
            budgetAssignmentService.assign(budget);

            // then
            validateLeaseItemsAndTerms(
                    leasePoison,
                    Arrays.asList(BVAL_POISON_1, U1_BVAL_2.setScale(2, BigDecimal.ROUND_HALF_UP)),
                    BudgetForBud.BUDGET_2015_START_DATE
            );
            validateLeaseItemsAndTerms(
                    leaseMiracle,
                    Arrays.asList(BVAL_MIRACLE_1, U2_BVAL_2.setScale(2, BigDecimal.ROUND_HALF_UP)),
                    BudgetForBud.BUDGET_2015_START_DATE
            );
            validateLeaseItemsAndTerms(
                    leaseHello3,
                    Arrays.asList(U3_BVAL_1.setScale(2, BigDecimal.ROUND_HALF_UP), U3_BVAL_2.setScale(2, BigDecimal.ROUND_HALF_UP)),
                    leaseHello3.getStartDate()
            );
            validateLeaseItemsAndTerms(
                    leaseDago,
                    Arrays.asList(U4_BVAL_1.add(U7_BVAL_1).setScale(2, BigDecimal.ROUND_HALF_UP), U4_BVAL_2.add(U7_BVAL_2).setScale(2, BigDecimal.ROUND_HALF_UP)),
                    BudgetForBud.BUDGET_2015_START_DATE
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

            Charge invoiceCharge1 = chargeRepository.findByReference(ChargeRefData.NL_SERVICE_CHARGE);
            Charge invoiceCharge2 = chargeRepository.findByReference(ChargeRefData.NL_SERVICE_CHARGE2);

            assertThat(leaseItemRepository.findLeaseItemsByType(lease, LeaseItemType.SERVICE_CHARGE_BUDGETED).size()).isEqualTo(2);

            assertThat(lease.getItems().first().getCharge()).isEqualTo(invoiceCharge1);
            assertThat(lease.getItems().first().getStartDate()).isEqualTo(startDate);
            assertThat(lease.getItems().first().getPaymentMethod()).isEqualTo(PaymentMethod.DIRECT_DEBIT);
            assertThat(lease.getItems().first().getInvoicingFrequency()).isEqualTo(InvoicingFrequency.QUARTERLY_IN_ADVANCE);
            assertThat(lease.getItems().first().getStatus()).isEqualTo(LeaseItemStatus.SUSPENDED);
            assertThat(lease.getItems().first().getTerms().size()).isEqualTo(1);

            LeaseTermForServiceCharge term1 = (LeaseTermForServiceCharge) lease.getItems().first().getTerms().first();
            assertThat(budgetCalculationResultLinkRepository.findByLeaseTerm(term1).size()).isEqualTo(1);
            assertThat(term1.getBudgetedValue()).isEqualTo(values.get(0));
            assertThat(term1.getAuditedValue()).isNull();
            assertThat(term1.getStartDate()).isEqualTo(startDate);
            assertThat(term1.getEndDate()).isEqualTo(BudgetForBud.BUDGET_2015_END_DATE);

            assertThat(lease.getItems().last().getCharge()).isEqualTo(invoiceCharge2);
            assertThat(lease.getItems().last().getStartDate()).isEqualTo(startDate);
            assertThat(lease.getItems().last().getPaymentMethod()).isEqualTo(PaymentMethod.DIRECT_DEBIT);
            assertThat(lease.getItems().last().getInvoicingFrequency()).isEqualTo(InvoicingFrequency.QUARTERLY_IN_ADVANCE);
            assertThat(lease.getItems().last().getStatus()).isEqualTo(LeaseItemStatus.SUSPENDED);
            assertThat(lease.getItems().last().getTerms().size()).isEqualTo(1);

            LeaseTermForServiceCharge term2 = (LeaseTermForServiceCharge) lease.getItems().last().getTerms().first();
            assertThat(budgetCalculationResultLinkRepository.findByLeaseTerm(term2).size()).isEqualTo(1);
            assertThat(term2.getBudgetedValue()).isEqualTo(values.get(1));
            assertThat(term2.getAuditedValue()).isNull();
            assertThat(term2.getStartDate()).isEqualTo(startDate);
            assertThat(term2.getEndDate()).isEqualTo(BudgetForBud.BUDGET_2015_END_DATE);

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
            assertThat(l1Before.getLeaseTermForServiceCharge().getLeaseItem().getStatus()).isEqualTo(LeaseItemStatus.SUSPENDED);

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

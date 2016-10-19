package org.estatio.integtests.budget;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.budgetassignment.BudgetAssignmentService;
import org.estatio.dom.budgetassignment.BudgetCalculationLinkRepository;
import org.estatio.dom.budgetassignment.ServiceChargeItemRepository;
import org.estatio.dom.budgetassignment.viewmodels.BudgetAssignmentResult;
import org.estatio.dom.budgetassignment.viewmodels.DetailedBudgetAssignmentResult;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budget.BudgetRepository;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationRepository;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationService;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationViewmodel;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.charge.ChargeRepository;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseRepository;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForBudNl;
import org.estatio.fixture.budget.BudgetForBud;
import org.estatio.fixture.budget.BudgetItemAllocationsForBud;
import org.estatio.fixture.charge.ChargeRefData;
import org.estatio.fixture.lease.LeasesForBudNl;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class

BudgetCalculationScenarioTest extends EstatioIntegrationTest {

    @Inject
    BudgetCalculationRepository budgetCalculationRepository;

    @Inject
    BudgetCalculationLinkRepository budgetCalculationLinkRepository;

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
    ChargeRepository chargeRepository;

    @Inject
    ServiceChargeItemRepository serviceChargeItemRepository;


    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, new BudgetItemAllocationsForBud());
            }
        });
    }


    public static class Calculate extends BudgetCalculationScenarioTest {

        Property property;
        Budget budget;
        List<BudgetCalculationViewmodel> calculationResults;
        List<BudgetAssignmentResult> budgetAssignmentResults;
        List<DetailedBudgetAssignmentResult> detailedBudgetAssignmentResults;

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
        public void CalculateAndAssign() throws Exception {
            calculate();
            detailedCalculation();
//            persistCalculations();
//            assignBudget();
//            assignBudgetWhenUpdated();
//            assignBudgetWhenAudited();
//            assignBudgetWhenAuditedAndUpdated();
//            assignBudgetWhenAuditedAndUpdatedWithEmptyAuditedValueOnBudgetItem();
//            assignBudgetWhenOccupationEndsInBudgetYear();
        }

        public void calculate() throws Exception {

            // when
            calculationResults = budgetCalculationService.getCalculations(budget);
            budgetAssignmentResults = budgetAssignmentService.getAssignmentResults(budget);

            // then
            assertThat(calculationResults.size()).isEqualTo(35);
            assertThat(budgetAssignmentResults.size()).isEqualTo(14);
            assertThat(budgetedAmountFor(LeasesForBudNl.REF1, ChargeRefData.NL_SERVICE_CHARGE)).isEqualTo(new BigDecimal("1928.571437"));
            assertThat(budgetedAmountFor(LeasesForBudNl.REF1, ChargeRefData.NL_SERVICE_CHARGE2)).isEqualTo(new BigDecimal("964.285722"));
            assertThat(budgetedAmountFor(LeasesForBudNl.REF2, ChargeRefData.NL_SERVICE_CHARGE)).isEqualTo(new BigDecimal("2857.142847"));
            assertThat(budgetedAmountFor(LeasesForBudNl.REF2, ChargeRefData.NL_SERVICE_CHARGE2)).isEqualTo(new BigDecimal("1928.571417"));
            assertThat(budgetedAmountFor(LeasesForBudNl.REF3, ChargeRefData.NL_SERVICE_CHARGE)).isEqualTo(new BigDecimal("3785.714283"));
            assertThat(budgetedAmountFor(LeasesForBudNl.REF3, ChargeRefData.NL_SERVICE_CHARGE2)).isEqualTo(new BigDecimal("2892.857139"));
            assertThat(budgetedAmountFor(LeasesForBudNl.REF4, ChargeRefData.NL_SERVICE_CHARGE)).isEqualTo(new BigDecimal("11214.285719"));
            assertThat(budgetedAmountFor(LeasesForBudNl.REF4, ChargeRefData.NL_SERVICE_CHARGE2)).isEqualTo(new BigDecimal("10607.142861"));
            assertThat(budgetedAmountFor(LeasesForBudNl.REF4A, ChargeRefData.NL_SERVICE_CHARGE)).isEqualTo(new BigDecimal("4714.285719"));
            assertThat(budgetedAmountFor(LeasesForBudNl.REF4A, ChargeRefData.NL_SERVICE_CHARGE2)).isEqualTo(new BigDecimal("3857.142861"));
            assertThat(budgetedAmountFor(LeasesForBudNl.REF5, ChargeRefData.NL_SERVICE_CHARGE)).isEqualTo(new BigDecimal("5642.857155"));
            assertThat(budgetedAmountFor(LeasesForBudNl.REF5, ChargeRefData.NL_SERVICE_CHARGE2)).isEqualTo(new BigDecimal("4821.428583"));


//            assertThat(budgetAssignmentService.getShortFallAmountBudgeted(budget)).isEqualTo(new BigDecimal("31309.39"));


        }

        private BigDecimal budgetedAmountFor(final String leaseReference, final String invoiceChargeReference){

            BigDecimal resultValue = BigDecimal.ZERO;

            for (BudgetAssignmentResult result : resultsForLease(leaseReference, invoiceChargeReference)){
                resultValue = resultValue.add(result.getBudgetedValue());
            }

            return resultValue;
        }

        private List<BudgetAssignmentResult> resultsForLease(final String leaseReference, final String invoiceChargeReference){
            return budgetAssignmentResults.stream().filter(x ->x.getLeaseReference().equals(leaseReference) && x.getInvoiceCharge().equals(invoiceChargeReference)).collect(Collectors.toList());
        }

        public void detailedCalculation() throws Exception {

            String INCOMING_CHARGE_LABEL_1 = "NLD_INCOMING_CHARGE_1 Incoming Charge 1 (NLD) | budgeted 10000.00 | 100.00 % table1";
            String INCOMING_CHARGE_LABEL_2 = "NLD_INCOMING_CHARGE_2 Incoming Charge 2 (NLD) | budgeted 20000.00 | 80.00 % table1 | 20.00 % table2";
            String INCOMING_CHARGE_LABEL_3 = "NLD_INCOMING_CHARGE_3 Incoming Charge 3 (NLD) | budgeted 30000.00 | 90.00 % table1 | 10.00 % table2";

            // given
            Lease leaseForDago = leaseRepository.findLeaseByReference(LeasesForBudNl.REF4);

            // when
            calculationResults = budgetCalculationService.getCalculations(budget);
            detailedBudgetAssignmentResults = budgetAssignmentService.getDetailedBudgetAssignmentResults(budget, leaseForDago);

            // then
            assertThat(detailedBudgetAssignmentResults.size()).isEqualTo(8);

            assertThat(detailedBudgetAssignmentResults.get(0).getBudgetedValue()).isEqualTo(new BigDecimal("1428.571430"));
            assertThat(detailedBudgetAssignmentResults.get(0).getIncomingCharge()).isEqualTo(INCOMING_CHARGE_LABEL_1);
            assertThat(detailedBudgetAssignmentResults.get(0).getInvoiceCharge()).isEqualTo(chargeRepository.findByReference(ChargeRefData.NL_SERVICE_CHARGE).getReference());

            assertThat(detailedBudgetAssignmentResults.get(1).getBudgetedValue()).isEqualTo(new BigDecimal("2285.714288"));
            assertThat(detailedBudgetAssignmentResults.get(1).getIncomingCharge()).isEqualTo(INCOMING_CHARGE_LABEL_2);
            assertThat(detailedBudgetAssignmentResults.get(1).getInvoiceCharge()).isEqualTo(chargeRepository.findByReference(ChargeRefData.NL_SERVICE_CHARGE).getReference());

            assertThat(detailedBudgetAssignmentResults.get(4).getBudgetedValue()).isEqualTo(new BigDecimal("3857.142861"));
            assertThat(detailedBudgetAssignmentResults.get(4).getIncomingCharge()).isEqualTo(INCOMING_CHARGE_LABEL_3);
            assertThat(detailedBudgetAssignmentResults.get(4).getInvoiceCharge()).isEqualTo(chargeRepository.findByReference(ChargeRefData.NL_SERVICE_CHARGE2).getReference());

            assertThat(detailedBudgetAssignmentResults.get(5).getBudgetedValue()).isEqualTo(new BigDecimal("2500.000000"));
            assertThat(detailedBudgetAssignmentResults.get(5).getIncomingCharge()).isEqualTo(INCOMING_CHARGE_LABEL_1);
            assertThat(detailedBudgetAssignmentResults.get(5).getInvoiceCharge()).isEqualTo(chargeRepository.findByReference(ChargeRefData.NL_SERVICE_CHARGE).getReference());

            assertThat(detailedBudgetAssignmentResults.get(7).getBudgetedValue()).isEqualTo(new BigDecimal("6750.000000"));
            assertThat(detailedBudgetAssignmentResults.get(7).getIncomingCharge()).isEqualTo(INCOMING_CHARGE_LABEL_3);
            assertThat(detailedBudgetAssignmentResults.get(7).getInvoiceCharge()).isEqualTo(chargeRepository.findByReference(ChargeRefData.NL_SERVICE_CHARGE2).getReference());

        }

        public void persistCalculations() throws Exception {


        }

        public void assignBudget() throws Exception {


        }

        public void assignBudgetWhenUpdated() throws Exception {

        }

        public void assignBudgetWhenAudited() throws Exception {

        }

        public void assignBudgetWhenAuditedAndUpdated() throws Exception {

        }

        public void assignBudgetWhenAuditedAndUpdatedWithEmptyAuditedValueOnBudgetItem() throws Exception {

        }

        public void assignBudgetWhenOccupationEndsInBudgetYear() throws Exception {

        }

    }

}

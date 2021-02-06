package org.estatio.module.budgetassignment.dom;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import org.assertj.core.util.Lists;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResult;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResultLeaseTermLink;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResultLeaseTermLinkRepository;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.lease.dom.InvoicingFrequency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseTermForServiceCharge;
import org.estatio.module.lease.dom.occupancy.Occupancy;

import static org.assertj.core.api.Assertions.assertThat;

public class BudgetAssignmentService_Test {

    BudgetAssignmentService budgetAssignmentService;

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Before
    public void before() throws Exception {
        budgetAssignmentService = new BudgetAssignmentService();
    }

    @Test
    public void overlappingOccupanciesFoundIn_works() {

        // given
        Occupancy o1 = new Occupancy();
        o1.setStartDate(new LocalDate(2018,01,01));
        Occupancy o2 = new Occupancy();
        o2.setEndDate(new LocalDate(2017, 12, 31));
        Occupancy o3 = new Occupancy();
        o3.setEndDate(new LocalDate(2018,01,01));

        Occupancy o4 = new Occupancy();
        o4.setStartDate(new LocalDate(2018, 1, 2));
        o4.setEndDate(new LocalDate(2018, 1, 31));

        Occupancy o5 = new Occupancy();
        o5.setStartDate(new LocalDate(2018,1, 31));

        List<Occupancy> emptyList = Arrays.asList();
        List<Occupancy> singleOccupancy = Arrays.asList(o1);
        List<Occupancy> noOverlappingOccupancies = Arrays.asList(o1, o2);
        List<Occupancy> noOverlappingOccupancies2 = Arrays.asList(o3, o5);
        List<Occupancy> withOverlappingOccupancies = Arrays.asList(o1, o3);
        List<Occupancy> withOverlappingOccupancies2 = Arrays.asList(o4, o3, o5);

        // when, then
        assertThat(budgetAssignmentService.overlappingOccupanciesFoundIn(emptyList)).isFalse();
        assertThat(budgetAssignmentService.overlappingOccupanciesFoundIn(singleOccupancy)).isFalse();
        assertThat(budgetAssignmentService.overlappingOccupanciesFoundIn(noOverlappingOccupancies)).isFalse();
        assertThat(budgetAssignmentService.overlappingOccupanciesFoundIn(withOverlappingOccupancies)).isTrue();
        assertThat(budgetAssignmentService.overlappingOccupanciesFoundIn(noOverlappingOccupancies2)).isFalse();
        assertThat(budgetAssignmentService.overlappingOccupanciesFoundIn(withOverlappingOccupancies2)).isTrue();
    }



    @Test
    public void findExistingLeaseItemsOrCreateNewForServiceCharge_returns_active_item_when_found() throws Exception {

        // given
        final Charge charge = new Charge();
        BudgetCalculationResult budgetCalculationResult = new BudgetCalculationResult();
        budgetCalculationResult.setInvoiceCharge(charge);
        Budget budget = new Budget();
        budget.setStartDate(new LocalDate(2018,1,1));
        budgetCalculationResult.setBudget(budget);
        LeaseItem itemToBeFound = new LeaseItem(){
            @Override public LocalDateInterval getEffectiveInterval() {
                return LocalDateInterval.including(budget.getStartDate().plusMonths(1), null);
            }
        };
        itemToBeFound.setCharge(charge);
        Lease lease = new Lease(){
            @Override
            public List<LeaseItem> findItemsOfType(final LeaseItemType leaseItemType){
                return Arrays.asList(itemToBeFound);
            }
        };

        // when
        List<LeaseItem> itemsFound = budgetAssignmentService.findExistingLeaseItemsOrCreateNewForServiceCharge(lease, budgetCalculationResult);

        // then
        assertThat(itemsFound).hasSize(1);
        assertThat(itemsFound.get(0)).isEqualTo(itemToBeFound);
        
    }

    @Mock Lease mockLease;

    @Test
    public void findExistingLeaseItemsOrCreateNewForServiceCharge_works_when_item_to_copy_from_found() throws Exception {

        // given
        LeaseItem leaseItemToCopyFrom = new LeaseItem();
        leaseItemToCopyFrom.setInvoicingFrequency(InvoicingFrequency.QUARTERLY_IN_ADVANCE);
        leaseItemToCopyFrom.setPaymentMethod(PaymentMethod.CHEQUE);
        BudgetAssignmentService budgetAssignmentService = new BudgetAssignmentService(){
            @Override
            LeaseItem findItemToCopyFrom(final Lease lease){
                return leaseItemToCopyFrom;
            }
        };
        Charge charge = new Charge();
        BudgetCalculationResult budgetCalculationResult = new BudgetCalculationResult();
        budgetCalculationResult.setInvoiceCharge(charge);
        LocalDate budgetStartDate = new LocalDate(2018,1,1);
        Budget budget = new Budget();
        budget.setStartDate(budgetStartDate);
        budgetCalculationResult.setBudget(budget);

        // expect
        context.checking(new Expectations(){{
            oneOf(mockLease).findItemsOfType(LeaseItemType.SERVICE_CHARGE);
            will(returnValue(Lists.emptyList()));
            oneOf(mockLease).newItem(
                    LeaseItemType.SERVICE_CHARGE,
                    LeaseAgreementRoleTypeEnum.LANDLORD,
                    budgetCalculationResult.getInvoiceCharge(),
                    leaseItemToCopyFrom.getInvoicingFrequency(),
                    leaseItemToCopyFrom.getPaymentMethod(),
                    budgetStartDate);
        }});

        // when
        budgetAssignmentService.findExistingLeaseItemsOrCreateNewForServiceCharge(mockLease, budgetCalculationResult);
    }



    @Test
    public void findExistingLeaseItemsOrCreateNewForServiceCharge_works_when_no_item_to_copy_from_found() throws Exception {

        // given
        BudgetAssignmentService budgetAssignmentService = new BudgetAssignmentService(){
            @Override
            LeaseItem findItemToCopyFrom(final Lease lease){
                return null;
            }
        };
        Charge charge = new Charge();
        BudgetCalculationResult budgetCalculationResult = new BudgetCalculationResult();
        budgetCalculationResult.setInvoiceCharge(charge);
        LocalDate budgetStartDate = new LocalDate(2018,1,1);
        Budget budget = new Budget();
        budget.setStartDate(budgetStartDate);
        budgetCalculationResult.setBudget(budget);

        InvoicingFrequency invoicingFrequencyGuess = InvoicingFrequency.QUARTERLY_IN_ADVANCE;
        PaymentMethod paymentMethodGuess = PaymentMethod.DIRECT_DEBIT;

        // expect
        context.checking(new Expectations(){{
            oneOf(mockLease).findItemsOfType(LeaseItemType.SERVICE_CHARGE);
            will(returnValue(Lists.emptyList()));
            oneOf(mockLease).newItem(
                    LeaseItemType.SERVICE_CHARGE,
                    LeaseAgreementRoleTypeEnum.LANDLORD,
                    budgetCalculationResult.getInvoiceCharge(),
                    invoicingFrequencyGuess,
                    paymentMethodGuess,
                    budgetStartDate);
        }});

        // when
        budgetAssignmentService.findExistingLeaseItemsOrCreateNewForServiceCharge(mockLease, budgetCalculationResult);
    }

    @Test
    public void itemToCopyFrom_when_no_items_on_lease_returns_null() throws Exception {

        // given
        Lease lease = new Lease();
        // when
        LeaseItem itemFound = budgetAssignmentService.findItemToCopyFrom(lease);
        // then
        assertThat(itemFound).isNull();

    }

    @Test
    public void itemToCopyFrom_with_lease_having_service_charge_item_works() throws Exception {

        // expect
        context.checking(new Expectations(){{
            oneOf(mockLease).findFirstItemOfType(LeaseItemType.SERVICE_CHARGE);
        }});

        // when
        budgetAssignmentService.findItemToCopyFrom(mockLease);

    }

    @Test
    public void itemToCopyFrom_with_lease_having_rent_item_works() throws Exception {

        // expect
        context.checking(new Expectations(){{
            oneOf(mockLease).findFirstItemOfType(LeaseItemType.SERVICE_CHARGE);
            will(returnValue(null));
            oneOf(mockLease).findFirstItemOfType(LeaseItemType.RENT);
        }});

        // when
        budgetAssignmentService.findItemToCopyFrom(mockLease);

    }

    @Test
    public void itemToCopyFrom_with_lease_having_no_rent_and_no_service_charge_item_works() throws Exception {

        // given
        LeaseItem anyItemOtherThanRentOrServiceCharge = new LeaseItem();

        // expect
        context.checking(new Expectations(){{
            oneOf(mockLease).findFirstItemOfType(LeaseItemType.SERVICE_CHARGE);
            will(returnValue(null));
            oneOf(mockLease).findFirstItemOfType(LeaseItemType.RENT);
            will(returnValue(null));
            allowing(mockLease).getItems();
            will(returnValue(new TreeSet(Arrays.asList(anyItemOtherThanRentOrServiceCharge))));
        }});

        // when
        LeaseItem itemToCopyFrom = budgetAssignmentService.findItemToCopyFrom(mockLease);

        // then
        assertThat(itemToCopyFrom).isEqualTo(anyItemOtherThanRentOrServiceCharge);

    }

    @Mock LeaseItem mockLeaseItem;
    @Test
    public void upsertLeaseTermForServiceCharge_works_when_new_term_created(){

        // given
        BudgetAssignmentService budgetAssignmentService = new BudgetAssignmentService(){
            @Override void updateLeaseTermsForServiceCharge(
                    final LeaseItem serviceChargeItem, final BudgetCalculationResult result) {
                return;
            }
        };

        BudgetCalculationResult result = new BudgetCalculationResult();
        LocalDate budgetStartDate = new LocalDate(2020,1,1);
        LocalDate budgetEndDate = new LocalDate(2020,12,31);
        Budget budget = new Budget();
        budget.setStartDate(budgetStartDate);
        budget.setEndDate(budgetEndDate);
        result.setBudget(budget);

        // expect
        context.checking(new Expectations(){{
            oneOf(mockLeaseItem).findTermsActiveDuring(budget.getInterval());
            will(returnValue(Lists.emptyList()));
            oneOf(mockLeaseItem).newTerm(budgetStartDate, budgetEndDate);
        }});

        // when
        budgetAssignmentService.upsertLeaseTermForServiceCharge(mockLeaseItem, result);

    }

    @Test
    public void upsertLeaseTermForServiceCharge_works_when_existing_term_found(){

        // given
        BudgetAssignmentService budgetAssignmentService = new BudgetAssignmentService(){
            @Override void updateLeaseTermsForServiceCharge(
                    final LeaseItem serviceChargeItem, final BudgetCalculationResult result) {
                return;
            }
        };

        BudgetCalculationResult result = new BudgetCalculationResult();
        LocalDate budgetStartDate = new LocalDate(2020,1,1);
        LocalDate budgetEndDate = new LocalDate(2020,12,31);
        Budget budget = new Budget();
        budget.setStartDate(budgetStartDate);
        budget.setEndDate(budgetEndDate);
        result.setBudget(budget);

        // expect
        context.checking(new Expectations(){{
            oneOf(mockLeaseItem).findTermsActiveDuring(budget.getInterval());
            will(returnValue(Arrays.asList(new LeaseTermForServiceCharge())));
        }});

        // when
        budgetAssignmentService.upsertLeaseTermForServiceCharge(mockLeaseItem, result);

    }

    @Mock BudgetCalculationResultLeaseTermLinkRepository mockBudgetCalculationResultLeaseTermLinkRepository;
    @Mock LeaseTermForServiceCharge term1;
    @Mock LeaseTermForServiceCharge term2;

    @Test
    public void updateLeaseTermForServiceCharge_works_when_no_splitting() throws Exception {

        // given
        BudgetAssignmentService budgetAssignmentService = new BudgetAssignmentService(){
            @Override
            void recalculateTerm(
                    final LeaseTermForServiceCharge term, final BudgetCalculationType budgetCalculationType) {
                return;
            }
        };
        budgetAssignmentService.budgetCalculationResultLeaseTermLinkRepository = mockBudgetCalculationResultLeaseTermLinkRepository;
        BudgetCalculationResult result = new BudgetCalculationResult();
        LocalDate budgetStartDate = new LocalDate(2020,1,1);
        LocalDate budgetEndDate = new LocalDate(2020,12,31);
        Budget budget = new Budget();
        budget.setStartDate(budgetStartDate);
        budget.setEndDate(budgetEndDate);
        result.setBudget(budget);

        // expect
        context.checking(new Expectations(){{
            oneOf(mockLeaseItem).findTermsActiveDuring(budget.getInterval());
            will(returnValue(Arrays.asList(term1, term2)));
            allowing(term1).getStartDate();
            will(returnValue(budgetStartDate));
            allowing(term1).getEndDate();
            will(returnValue(budgetEndDate.minusMonths(1)));
            allowing(term2).getStartDate();
            will(returnValue(budgetEndDate.minusMonths(1).plusDays(1)));
            allowing(term2).getEndDate();
            will(returnValue(budgetEndDate));
            oneOf(mockBudgetCalculationResultLeaseTermLinkRepository).findOrCreate(result, term1);
            oneOf(mockBudgetCalculationResultLeaseTermLinkRepository).findOrCreate(result, term2);
        }});

        // when
        budgetAssignmentService.updateLeaseTermsForServiceCharge(mockLeaseItem, result);

    }

    @Test
    public void updateLeaseTermForServiceCharge_works_when_splitting() throws Exception {

        // given
        BudgetAssignmentService budgetAssignmentService = new BudgetAssignmentService(){
            @Override
            void recalculateTerm(
                    final LeaseTermForServiceCharge term, final BudgetCalculationType budgetCalculationType) {
                return;
            }
        };
        budgetAssignmentService.budgetCalculationResultLeaseTermLinkRepository = mockBudgetCalculationResultLeaseTermLinkRepository;
        BudgetCalculationResult result = new BudgetCalculationResult();
        LocalDate budgetStartDate = new LocalDate(2020,1,1);
        LocalDate budgetEndDate = new LocalDate(2020,12,31);
        Budget budget = new Budget();
        budget.setStartDate(budgetStartDate);
        budget.setEndDate(budgetEndDate);
        result.setBudget(budget);

        // expect
        context.checking(new Expectations(){{
            oneOf(mockLeaseItem).findTermsActiveDuring(budget.getInterval());
            will(returnValue(Arrays.asList(term1, term2)));
            allowing(term1).getStartDate();
            will(returnValue(budgetStartDate.minusDays(1)));
            allowing(term1).getEndDate();
            will(returnValue(budgetEndDate.minusMonths(1)));
            oneOf(term1).split(budgetStartDate);
            will(returnValue(term1));
            allowing(term2).getStartDate();
            will(returnValue(budgetEndDate.minusMonths(1).plusDays(1)));
            allowing(term2).getEndDate();
            will(returnValue(budgetEndDate.plusDays(1)));
            oneOf(term2).split(budgetEndDate.plusDays(1));
            will(returnValue(term2));
            oneOf(mockBudgetCalculationResultLeaseTermLinkRepository).findOrCreate(result, term1);
            oneOf(mockBudgetCalculationResultLeaseTermLinkRepository).findOrCreate(result, term2);
        }});

        // when
        budgetAssignmentService.updateLeaseTermsForServiceCharge(mockLeaseItem, result);

    }

    @Test
    public void recalculateTerm_clears_existing_values_on_term() throws Exception {

        // given
        budgetAssignmentService.budgetCalculationResultLeaseTermLinkRepository = mockBudgetCalculationResultLeaseTermLinkRepository;
        LeaseTermForServiceCharge term = new LeaseTermForServiceCharge();

        // expect
        context.checking(new Expectations(){{
            allowing(mockBudgetCalculationResultLeaseTermLinkRepository).findByLeaseTerm(term);
            will(returnValue(Lists.emptyList())); // just for this test; should not happen in prod
        }});

        // when
        term.setBudgetedValue(new BigDecimal("123.45"));
        term.setAuditedValue(new BigDecimal("234.56"));
        budgetAssignmentService.recalculateTerm(term, BudgetCalculationType.BUDGETED);
        // then
        assertThat(term.getBudgetedValue()).isNull();
        assertThat(term.getAuditedValue()).isNotNull();
        // and when
        term.setBudgetedValue(new BigDecimal("123.45"));
        budgetAssignmentService.recalculateTerm(term, BudgetCalculationType.AUDITED);
        // then
        assertThat(term.getBudgetedValue()).isNotNull();
        assertThat(term.getAuditedValue()).isNull();

    }

    @Test
    public void recalculateTerm_works() throws Exception {

        // given
        budgetAssignmentService.budgetCalculationResultLeaseTermLinkRepository = mockBudgetCalculationResultLeaseTermLinkRepository;
        LeaseTermForServiceCharge term = new LeaseTermForServiceCharge();
        BudgetCalculationResult result1 = new BudgetCalculationResult();
        result1.setValue(new BigDecimal("123.45"));
        BudgetCalculationResult result2 = new BudgetCalculationResult();
        result2.setValue(new BigDecimal("100.00"));

        // expect
        context.checking(new Expectations(){{
            allowing(mockBudgetCalculationResultLeaseTermLinkRepository).findByLeaseTerm(term);
            will(returnValue(Arrays.asList(new BudgetCalculationResultLeaseTermLink(result1, term), new BudgetCalculationResultLeaseTermLink(result2, term))));
        }});

        // when
        result1.setType(BudgetCalculationType.BUDGETED);
        result2.setType(BudgetCalculationType.BUDGETED);
        budgetAssignmentService.recalculateTerm(term, BudgetCalculationType.BUDGETED);

        // then
        assertThat(term.getBudgetedValue()).isEqualTo(result1.getValue().add(result2.getValue()));
        assertThat(term.getBudgetedValue()).isEqualTo(new BigDecimal("223.45"));

        // when
        result1.setType(BudgetCalculationType.AUDITED);
        result2.setType(BudgetCalculationType.AUDITED);
        budgetAssignmentService.recalculateTerm(term, BudgetCalculationType.AUDITED);

        // then
        assertThat(term.getAuditedValue()).isEqualTo(result1.getValue().add(result2.getValue()));
        assertThat(term.getAuditedValue()).isEqualTo(new BigDecimal("223.45"));

        // when
        result1.setType(BudgetCalculationType.BUDGETED);
        result2.setType(BudgetCalculationType.AUDITED);
        budgetAssignmentService.recalculateTerm(term, BudgetCalculationType.AUDITED);

        // then
        assertThat(term.getAuditedValue()).isEqualTo(result2.getValue());
        assertThat(term.getAuditedValue()).isEqualTo(new BigDecimal("100.00"));

    }

} 

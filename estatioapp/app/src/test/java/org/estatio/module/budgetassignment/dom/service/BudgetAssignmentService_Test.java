package org.estatio.module.budgetassignment.dom.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

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
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResultRepository;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.lease.dom.InvoicingFrequency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseTerm;
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
    public void findOrCreateLeaseItemForServiceCharge_returns_active_item_when_found() throws Exception {

        // given
        LeaseItem itemToBeFound = new LeaseItem();
        Lease lease = new Lease(){
            @Override
            public LeaseItem findFirstActiveItemOfTypeAndChargeInInterval(final LeaseItemType leaseItemType, final Charge charge, final LocalDateInterval interval){
                return itemToBeFound;
            }
        };
        BudgetCalculationResult budgetCalculationResult = new BudgetCalculationResult();
        Budget budget = new Budget();
        budget.setStartDate(new LocalDate(2018,1,1));
        budgetCalculationResult.setBudget(budget);

        // when
        LeaseItem itemFound = budgetAssignmentService.findOrCreateLeaseItemForServiceCharge(lease, budgetCalculationResult);

        // then
        assertThat(itemFound).isEqualTo(itemToBeFound);
        
    }

    @Mock Lease mockLease;

    @Test
    public void findOrCreateLeaseItemForServiceCharge_works_when_item_to_copy_from_found() throws Exception {

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
            oneOf(mockLease).findFirstActiveItemOfTypeAndChargeInInterval(LeaseItemType.SERVICE_CHARGE, charge, budget.getInterval());
            will(returnValue(null));
            oneOf(mockLease).newItem(
                    LeaseItemType.SERVICE_CHARGE,
                    LeaseAgreementRoleTypeEnum.LANDLORD,
                    budgetCalculationResult.getInvoiceCharge(),
                    leaseItemToCopyFrom.getInvoicingFrequency(),
                    leaseItemToCopyFrom.getPaymentMethod(),
                    budgetStartDate);
        }});

        // when
        budgetAssignmentService.findOrCreateLeaseItemForServiceCharge(mockLease, budgetCalculationResult);
    }



    @Test
    public void findOrCreateLeaseItemForServiceCharge_works_when_no_item_to_copy_from_found() throws Exception {

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
            oneOf(mockLease).findFirstActiveItemOfTypeAndChargeInInterval(LeaseItemType.SERVICE_CHARGE, charge, budget.getInterval());
            will(returnValue(null));
            oneOf(mockLease).newItem(
                    LeaseItemType.SERVICE_CHARGE,
                    LeaseAgreementRoleTypeEnum.LANDLORD,
                    budgetCalculationResult.getInvoiceCharge(),
                    invoicingFrequencyGuess,
                    paymentMethodGuess,
                    budgetStartDate);
        }});

        // when
        budgetAssignmentService.findOrCreateLeaseItemForServiceCharge(mockLease, budgetCalculationResult);
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

    @Mock BudgetCalculationResultRepository mockBudgetCalculationResultRepository;

    @Test
    public void upsertLeaseTermForServiceCharge_works_when_new_term_created(){

        // given
        budgetAssignmentService.budgetCalculationResultRepository = mockBudgetCalculationResultRepository;

        BudgetCalculationResult result = new BudgetCalculationResult();
        Budget budget = new Budget();
        LocalDate startDate = new LocalDate(2018, 1, 1);
        LocalDate endDate = new LocalDate(2018, 12, 31);
        budget.setStartDate(startDate);
        budget.setEndDate(endDate);
        result.setBudget(budget);
        result.setType(BudgetCalculationType.ACTUAL);
        result.setValue(new BigDecimal("123.45"));

        BudgetCalculationResult previousResult1 = new BudgetCalculationResult();
        previousResult1.setBudget(budget);
        previousResult1.setType(BudgetCalculationType.BUDGETED);
        previousResult1.setValue(new BigDecimal("55.55"));
        BudgetCalculationResult previousResult2 = new BudgetCalculationResult();
        previousResult2.setBudget(budget);
        previousResult2.setType(BudgetCalculationType.BUDGETED);
        previousResult2.setValue(new BigDecimal("40.00"));


        final LeaseTermForServiceCharge termForServiceCharge = new LeaseTermForServiceCharge();

        final LeaseItem serviceChargeItem = new LeaseItem(){
            @Override
            public LeaseTerm newTerm(final LocalDate startDate,
                    final LocalDate endDate){
                return termForServiceCharge;
            }
        };
        serviceChargeItem.setType(LeaseItemType.SERVICE_CHARGE);

        // expect
        context.checking(new Expectations(){{
            oneOf(mockBudgetCalculationResultRepository).findByLeaseTerm(with(any(LeaseTermForServiceCharge.class)));
            will(returnValue(Arrays.asList(result, previousResult1, previousResult2)));
        }});

        // when
        budgetAssignmentService.upsertLeaseTermForServiceCharge(serviceChargeItem, result);

        // then
        assertThat(result.getLeaseTerm()).isEqualTo(termForServiceCharge);
        assertThat(termForServiceCharge.getAuditedValue()).isEqualTo(result.getValue());
        assertThat(termForServiceCharge.getBudgetedValue()).isEqualTo(previousResult1.getValue().add(previousResult2.getValue()));
    }

    @Test
    public void upsertLeaseTermForServiceCharge_works_when_existing_term_found(){

        // given
        budgetAssignmentService.budgetCalculationResultRepository = mockBudgetCalculationResultRepository;

        BudgetCalculationResult result = new BudgetCalculationResult();
        Budget budget = new Budget();
        LocalDate startDate = new LocalDate(2018, 1, 1);
        LocalDate endDate = new LocalDate(2018, 12, 31);
        budget.setStartDate(startDate);
        budget.setEndDate(endDate);
        result.setBudget(budget);
        result.setType(BudgetCalculationType.ACTUAL);
        result.setValue(new BigDecimal("123.45"));

        BudgetCalculationResult previousResult1 = new BudgetCalculationResult();
        previousResult1.setBudget(budget);
        previousResult1.setType(BudgetCalculationType.BUDGETED);
        previousResult1.setValue(new BigDecimal("55.55"));
        BudgetCalculationResult previousResult2 = new BudgetCalculationResult();
        previousResult2.setBudget(budget);
        previousResult2.setType(BudgetCalculationType.BUDGETED);
        previousResult2.setValue(new BigDecimal("40.00"));


        final LeaseTermForServiceCharge termForServiceCharge = new LeaseTermForServiceCharge();
        termForServiceCharge.setBudgetedValue(BigDecimal.TEN);
        termForServiceCharge.setAuditedValue(BigDecimal.TEN);

        final LeaseItem serviceChargeItem = new LeaseItem(){
            @Override
            public LeaseTerm findTerm(final LocalDate startDate){
                return termForServiceCharge;
            }
        };
        serviceChargeItem.setType(LeaseItemType.SERVICE_CHARGE);

        // expect
        context.checking(new Expectations(){{
            oneOf(mockBudgetCalculationResultRepository).findByLeaseTerm(with(any(LeaseTermForServiceCharge.class)));
            will(returnValue(Arrays.asList(result, previousResult1, previousResult2)));
        }});

        // when
        budgetAssignmentService.upsertLeaseTermForServiceCharge(serviceChargeItem, result);

        // then
        assertThat(result.getLeaseTerm()).isEqualTo(termForServiceCharge);
        assertThat(termForServiceCharge.getAuditedValue()).isEqualTo(result.getValue());
        assertThat(termForServiceCharge.getBudgetedValue()).isEqualTo(previousResult1.getValue().add(previousResult2.getValue()));
    }

} 

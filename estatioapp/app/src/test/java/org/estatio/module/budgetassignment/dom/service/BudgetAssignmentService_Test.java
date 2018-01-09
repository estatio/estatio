package org.estatio.module.budgetassignment.dom.service;

import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResult;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.lease.dom.InvoicingFrequency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.LeaseStatus;
import org.estatio.module.lease.dom.occupancy.Occupancy;

import static org.assertj.core.api.Assertions.assertThat;

public class BudgetAssignmentService_Test {

    BudgetAssignmentService budgetAssignmentService;
    Budget budget;
    Lease leaseWith1ActiveOccupancy;
    Lease leaseWith2ActiveOccupancies;
    Lease leaseWithNoActiveOccupancies;
    Lease leaseTerminated;
    Occupancy o1;
    Occupancy o2;
    Occupancy o3;
    Occupancy o4;
    Occupancy o5;

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Before
    public void before() throws Exception {
        budgetAssignmentService = new BudgetAssignmentService();

        o1 = new Occupancy();
        leaseWith1ActiveOccupancy = new Lease(){
            @Override
            public SortedSet<Occupancy> getOccupancies(){
                return new TreeSet<>(Arrays.asList(o1));
            }
        };

        o2 = new Occupancy();
        o3 = new Occupancy();
        leaseWith2ActiveOccupancies = new Lease(){
            @Override
            public SortedSet<Occupancy> getOccupancies(){
                return new TreeSet<>(Arrays.asList(o2, o3));
            }
        };

        o4 = new Occupancy();
        leaseWithNoActiveOccupancies = new Lease(){
            @Override
            public SortedSet<Occupancy> getOccupancies(){
                return new TreeSet<>(Arrays.asList(o4));
            }
        };

        o5 = new Occupancy();
        leaseTerminated = new Lease(){
            @Override
            public SortedSet<Occupancy> getOccupancies(){
                return new TreeSet<>(Arrays.asList(o5));
            }
        };

        budget = new Budget();
        LocalDate startDate = new LocalDate(2015,01,01);
        LocalDate endDate = new LocalDate(2015,12,31);
        budget.setStartDate(startDate);
        budget.setEndDate(endDate);
        LeaseRepository leaseRepository = new LeaseRepository(){
            @Override
            public List<Lease> findLeasesByProperty(final Property property) {
                return Arrays.asList(
                        leaseWith1ActiveOccupancy,
                        leaseWith2ActiveOccupancies,
                        leaseWithNoActiveOccupancies,
                        leaseTerminated);
            }
        };
        budgetAssignmentService.leaseRepository = leaseRepository;

    }

    @Test
    public void leasesWithActiveOccupanciesTest() {

        // given
        o1.setStartDate(new LocalDate(2015,01,01));
        o2.setStartDate(new LocalDate(2015,01,01));
        o3.setStartDate(new LocalDate(2015,01,01));
        o4.setEndDate(new LocalDate(2014,12,31));
        o5.setStartDate(new LocalDate(2015,01,01));
        leaseTerminated.setStatus(LeaseStatus.TERMINATED);

        // when
        List<Lease> leasesfound = budgetAssignmentService.leasesWithActiveOccupations(budget);

        // then
        assertThat(leasesfound.size()).isEqualTo(2);
        assertThat(leasesfound.get(0)).isEqualTo(leaseWith1ActiveOccupancy);
        assertThat(leasesfound.get(1)).isEqualTo(leaseWith2ActiveOccupancies);
    }


    @Test
    public void findOrCreateLeaseItemForServiceChargeBudgeted_returns_active_item_when_found() throws Exception {

        // given
        LeaseItem itemToBeFound = new LeaseItem();
        Lease lease = new Lease(){
            @Override
            public LeaseItem findFirstActiveItemOfTypeAndChargeOnDate(final LeaseItemType leaseItemType, final Charge charge, final LocalDate date){
                return itemToBeFound;
            }
        };
        BudgetCalculationResult budgetCalculationResult = new BudgetCalculationResult();
        LocalDate termStartDate = new LocalDate(2018,1,1);

        // when
        LeaseItem itemFound = budgetAssignmentService.findOrCreateLeaseItemForServiceChargeBudgeted(lease, budgetCalculationResult, termStartDate);

        // then
        assertThat(itemFound).isEqualTo(itemToBeFound);
        
    }

    @Mock Lease mockLease;

    @Test
    public void findOrCreateLeaseItemForServiceChargeBudgeted_works_when_item_to_copy_from_found() throws Exception {

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
        LocalDate termStartDate = new LocalDate(2018,1,1);

        // expect
        context.checking(new Expectations(){{
            oneOf(mockLease).findFirstActiveItemOfTypeAndChargeOnDate(LeaseItemType.SERVICE_CHARGE, charge, termStartDate);
            will(returnValue(null));
            oneOf(mockLease).newItem(
                    LeaseItemType.SERVICE_CHARGE,
                    LeaseAgreementRoleTypeEnum.LANDLORD,
                    budgetCalculationResult.getInvoiceCharge(),
                    leaseItemToCopyFrom.getInvoicingFrequency(),
                    leaseItemToCopyFrom.getPaymentMethod(),
                    termStartDate);
        }});

        // when
        budgetAssignmentService.findOrCreateLeaseItemForServiceChargeBudgeted(mockLease, budgetCalculationResult, termStartDate);
    }

    @Test
    public void findOrCreateLeaseItemForServiceChargeBudgeted_works_when_no_item_to_copy_from_found() throws Exception {

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
        LocalDate termStartDate = new LocalDate(2018,1,1);

        InvoicingFrequency invoicingFrequencyGuess = InvoicingFrequency.QUARTERLY_IN_ADVANCE;
        PaymentMethod paymentMethodGuess = PaymentMethod.DIRECT_DEBIT;

        // expect
        context.checking(new Expectations(){{
            oneOf(mockLease).findFirstActiveItemOfTypeAndChargeOnDate(LeaseItemType.SERVICE_CHARGE, charge, termStartDate);
            will(returnValue(null));
            oneOf(mockLease).newItem(
                    LeaseItemType.SERVICE_CHARGE,
                    LeaseAgreementRoleTypeEnum.LANDLORD,
                    budgetCalculationResult.getInvoiceCharge(),
                    invoicingFrequencyGuess,
                    paymentMethodGuess,
                    termStartDate);
        }});

        // when
        budgetAssignmentService.findOrCreateLeaseItemForServiceChargeBudgeted(mockLease, budgetCalculationResult, termStartDate);
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

} 

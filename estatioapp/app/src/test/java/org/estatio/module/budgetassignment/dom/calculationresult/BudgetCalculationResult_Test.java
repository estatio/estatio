package org.estatio.module.budgetassignment.dom.calculationresult;

import java.util.Arrays;
import java.util.TreeSet;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.agreement.dom.Agreement;
import org.estatio.module.asset.dom.Unit;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.occupancy.Occupancy;

public class BudgetCalculationResult_Test {

    BudgetCalculationResult budgetCalculationResult;

    @Test
    public void occupancyCoversLeaseEffectiveInterval_works() {

        // given
        final LocalDate startDate = new LocalDate(2020, 1, 1);
        final LocalDate endDate = new LocalDate(2023, 1, 1);
        Occupancy occupancy = new Occupancy();
        occupancy.setStartDate(startDate);
        Lease lease = new Lease();
        lease.setStartDate(startDate);
        lease.setTenancyEndDate(endDate);
        occupancy.setLease(lease);
        budgetCalculationResult = new BudgetCalculationResult();
        budgetCalculationResult.setOccupancy(occupancy);
        // when
        occupancy.setEndDate(null);
        // then
        Assertions.assertThat(budgetCalculationResult.occupancyCoversLeaseEffectiveInterval()).isTrue();
        // and when
        occupancy.setEndDate(endDate);
        // then
        Assertions.assertThat(budgetCalculationResult.occupancyCoversLeaseEffectiveInterval()).isTrue();
        // and when
        occupancy.setEndDate(endDate.minusDays(1));
        // then
        Assertions.assertThat(budgetCalculationResult.occupancyCoversLeaseEffectiveInterval()).isFalse();
        // and when
        occupancy.setStartDate(startDate.plusDays(1));
        occupancy.setEndDate(endDate);
        // then
        Assertions.assertThat(budgetCalculationResult.occupancyCoversLeaseEffectiveInterval()).isFalse();

    }

    @Test
    public void leaseCoversBudgetInterval_works() throws Exception {

        // given
        final LocalDate startDate = new LocalDate(2020, 1, 1);
        final LocalDate endDate = new LocalDate(2023, 1, 1);
        final Budget budget = new Budget();
        budget.setStartDate(startDate);
        budget.setEndDate(endDate);
        Lease lease = new Lease();
        lease.setStartDate(startDate);
        Occupancy occupancy = new Occupancy();
        occupancy.setLease(lease);
        budgetCalculationResult = new BudgetCalculationResult();
        budgetCalculationResult.setOccupancy(occupancy);
        budgetCalculationResult.setBudget(budget);

        // when
        lease.setTenancyEndDate(null);
        // then
        Assertions.assertThat(budgetCalculationResult.leaseCoversBudgetInterval()).isTrue();
        // and when
        lease.setTenancyEndDate(endDate);
        // then
        Assertions.assertThat(budgetCalculationResult.leaseCoversBudgetInterval()).isTrue();
        // and when
        lease.setTenancyEndDate(endDate.minusDays(1));
        // then
        Assertions.assertThat(budgetCalculationResult.leaseCoversBudgetInterval()).isFalse();
        // and when
        lease.setStartDate(startDate.plusDays(1));
        lease.setTenancyEndDate(endDate);
        // then
        Assertions.assertThat(budgetCalculationResult.leaseCoversBudgetInterval()).isFalse();


    }

    @Test
    public void occupanciesOfleaseAndLeaseRenewalsCoverBudgetInterval_no_renewals_works() throws Exception {

        // given
        budgetCalculationResult = new BudgetCalculationResult();
        final LocalDate startDate = new LocalDate(2020, 1, 1);
        final LocalDate endDate = new LocalDate(2023, 1, 1);
        final Budget budget = new Budget();
        budget.setStartDate(startDate);
        budget.setEndDate(endDate);
        budgetCalculationResult.setBudget(budget);
        final Lease lease = new Lease();
        lease.setStartDate(startDate);
        lease.setTenancyEndDate(endDate);
        Occupancy occupancy = new Occupancy();
        occupancy.setLease(lease);
        Unit unit = new Unit();
        occupancy.setUnit(unit);
        budgetCalculationResult.setOccupancy(occupancy);

        // when lease covers budget interval and occupancy covers lease eff interval
        // then
        Assertions.assertThat(budgetCalculationResult.occupanciesOfleaseAndLeaseRenewalsCoverBudgetInterval()).isTrue();

        // when lease covers budget interval and occupancy does not cover lease eff interval
        occupancy.setStartDate(startDate.plusDays(1));
        // then
        Assertions.assertThat(budgetCalculationResult.occupanciesOfleaseAndLeaseRenewalsCoverBudgetInterval()).isFalse();

        // when lease starts during budget interval and no previous
        lease.setStartDate(startDate.plusDays(1));
        // then
        Assertions.assertThat(budgetCalculationResult.occupanciesOfleaseAndLeaseRenewalsCoverBudgetInterval()).isFalse();

        // when lease ends during budget interval and no next
        lease.setStartDate(startDate);
        lease.setTenancyEndDate(endDate.minusDays(1));
        // then
        Assertions.assertThat(budgetCalculationResult.occupanciesOfleaseAndLeaseRenewalsCoverBudgetInterval()).isFalse();

    }

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);
    @Mock
    Lease previousLease;

    @Test
    public void occupanciesOfleaseAndLeaseRenewalsCoverBudgetInterval_has_previous_works() throws Exception {

        // given
        budgetCalculationResult = new BudgetCalculationResult();
        final LocalDate startDate = new LocalDate(2020, 1, 1);
        final LocalDate endDate = new LocalDate(2023, 1, 1);
        final Budget budget = new Budget();
        budget.setStartDate(startDate);
        budget.setEndDate(endDate);
        budgetCalculationResult.setBudget(budget);
        final Lease lease = new Lease(){
            @Override public Agreement getPrevious() {
                return previousLease;
            }
        };
        lease.setTenancyEndDate(endDate);
        Occupancy occupancy = new Occupancy();
        occupancy.setLease(lease);
        Unit unit = new Unit();
        occupancy.setUnit(unit);
        lease.getOccupancies().add(occupancy);
        budgetCalculationResult.setOccupancy(occupancy);

        Occupancy previousOcc = new Occupancy();
        previousOcc.setUnit(unit);

        // expect
        context.checking(new Expectations(){{
            allowing(previousLease).getEffectiveInterval();
            will(returnValue(LocalDateInterval.including(startDate.minusYears(1), startDate)));
            allowing(previousLease).getOccupancies();
            will(returnValue(new TreeSet<>(Arrays.asList(previousOcc))));
            allowing(previousLease).getNext();
            will(returnValue(lease));
        }});

        // when lease starts during budget interval and has previous
        lease.setStartDate(startDate.plusDays(1));
        // then
        Assertions.assertThat(budgetCalculationResult.occupanciesOfleaseAndLeaseRenewalsCoverBudgetInterval()).isTrue();

        // when previous occ not covering prev lease eff interval
        previousOcc.setEndDate(startDate.minusDays(1));
        // then
        Assertions.assertThat(budgetCalculationResult.occupanciesOfleaseAndLeaseRenewalsCoverBudgetInterval()).isFalse();

    }

    @Mock
    Lease nextLease;

    @Test
    public void occupanciesOfleaseAndLeaseRenewalsCoverBudgetInterval_has_next_works() throws Exception {

        // given
        budgetCalculationResult = new BudgetCalculationResult();
        final LocalDate startDate = new LocalDate(2020, 1, 1);
        final LocalDate endDate = new LocalDate(2023, 1, 1);
        final Budget budget = new Budget();
        budget.setStartDate(startDate);
        budget.setEndDate(endDate);
        budgetCalculationResult.setBudget(budget);
        final Lease lease = new Lease(){
            @Override public Agreement getNext() {
                return nextLease;
            }
        };
        lease.setStartDate(startDate);
        Occupancy occupancy = new Occupancy();
        occupancy.setLease(lease);
        Unit unit = new Unit();
        occupancy.setUnit(unit);
        lease.getOccupancies().add(occupancy);
        budgetCalculationResult.setOccupancy(occupancy);

        Occupancy nextOccupancy = new Occupancy();
        nextOccupancy.setUnit(unit);

        // expect
        context.checking(new Expectations(){{
            allowing(nextLease).getEffectiveInterval();
            will(returnValue(LocalDateInterval.including(endDate.minusDays(1), endDate.plusYears(1))));
            allowing(nextLease).getOccupancies();
            will(returnValue(new TreeSet<>(Arrays.asList(nextOccupancy))));
        }});

        // when lease ends during budget interval and has next
        lease.setTenancyEndDate(endDate.minusDays(1));
        // then
        Assertions.assertThat(budgetCalculationResult.occupanciesOfleaseAndLeaseRenewalsCoverBudgetInterval()).isTrue();

        // when next occ not covering prev lease eff interval
        nextOccupancy.setStartDate(endDate.plusDays(1));
        // then
        Assertions.assertThat(budgetCalculationResult.occupanciesOfleaseAndLeaseRenewalsCoverBudgetInterval()).isFalse();

    }



}
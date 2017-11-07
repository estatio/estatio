package org.estatio.dom.budgetassignment;

import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.dom.asset.Property;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.LeaseStatus;
import org.estatio.module.lease.dom.occupancy.Occupancy;

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
        Assertions.assertThat(leasesfound.size()).isEqualTo(2);
        Assertions.assertThat(leasesfound.get(0)).isEqualTo(leaseWith1ActiveOccupancy);
        Assertions.assertThat(leasesfound.get(1)).isEqualTo(leaseWith2ActiveOccupancies);
    }


} 

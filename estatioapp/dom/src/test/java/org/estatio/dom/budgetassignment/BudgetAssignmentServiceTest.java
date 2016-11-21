package org.estatio.dom.budgetassignment;

import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationRepository;
import org.estatio.dom.lease.Occupancy;

public class BudgetAssignmentServiceTest { 

    BudgetAssignmentService budgetAssignmentService;
    Budget budget;
    Occupancy occupancy1;
    Occupancy occupancy2;

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    BudgetCalculationRepository budgetCalculationRepository;

    @Before
    public void before() throws Exception {
        occupancy1 = new Occupancy();
        occupancy2 = new Occupancy();

        budgetAssignmentService = new BudgetAssignmentService();
        budgetAssignmentService.budgetCalculationRepository = budgetCalculationRepository;

        budget = new Budget();

    }

    @Test
    public void recoverableAmountForOccupancyTest() {

    }


} 

package org.estatio.dom.budgetassignment;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import java.util.List;

import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculation;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationRepository;
import org.estatio.dom.lease.Occupancy;

import static org.assertj.core.api.Assertions.assertThat;

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

        budgetAssignmentService = new BudgetAssignmentService(){
            @Override
            List<Occupancy> associatedOccupancies(final BudgetCalculation calculation){
                return Arrays.asList(occupancy1, occupancy2);
            }
        };
        budgetAssignmentService.budgetCalculationRepository = budgetCalculationRepository;

        budget = new Budget();

    }

    @Test
    public void recoverableAmountForOccupancyTest() {

        BigDecimal annualFactor;
        BigDecimal recoverableAmountForOccupancy;

        // given
        budget.setStartDate(new LocalDate(2015, 01, 01));
        budget.setEndDate(new LocalDate(2015, 07, 14)); // 195 days
        annualFactor = new BigDecimal("195").divide(new BigDecimal("365"), MathContext.DECIMAL64);
        occupancy1.setStartDate(new LocalDate(2014, 02, 01));
        BudgetCalculation calculation = new BudgetCalculation(){
            @Override
            public Budget getBudget(){
                return budget;
            }
            @Override
            public BigDecimal getValueForBudgetPeriod() {
                return getValue().multiply(annualFactor);
            }
        };
        calculation.setValue(new BigDecimal("500.00")); // this is an 'annual amount'

        // when
        recoverableAmountForOccupancy = budgetAssignmentService.recoverableAmountForOccupancy(occupancy1, calculation);

        // then the full amount for the budgetPeriod is recoverable
        assertThat(calculation.getValueForBudgetPeriod().setScale(6, BigDecimal.ROUND_HALF_UP)).isEqualTo(new BigDecimal("267.123288"));
        assertThat(recoverableAmountForOccupancy.setScale(6, BigDecimal.ROUND_HALF_UP)).isEqualTo(new BigDecimal("267.123288")); // = 500 * 195 / 365

        // and when
        occupancy1.setEndDate(new LocalDate(2015, 6, 30)); // 14 days non recoverable on 195 budget days
        recoverableAmountForOccupancy = budgetAssignmentService.recoverableAmountForOccupancy(occupancy1, calculation);

        // then part is recoverable
        assertThat(recoverableAmountForOccupancy.setScale(6, BigDecimal.ROUND_HALF_UP)).isEqualTo(new BigDecimal("247.945205")); // = 267.12 - (267.12 * 14 / 195)

    }

//TODO: handle shortfall calculations in later stadium
//    @Test
//    public void shortFallForCalculationTest() {
//
//        ShortFall shortFall;
//
//        // given
//        budget.setStartDate(new LocalDate(2015, 01, 01));
//        budget.setEndDate(new LocalDate(2015, 07, 14)); // 195 days
//        occupancy1.setStartDate(new LocalDate(2014, 02, 01));
//        occupancy1.setEndDate(new LocalDate(2015, 06, 29)); // 180 overlap days with budget
//        occupancy2.setStartDate(new LocalDate(2015, 7, 14)); // 1 overlap day with budget so total of 14 unrecoverable days like recoverableAmountForOccupancyTest
//        BudgetCalculation calculation = new BudgetCalculation(){
//            public Budget getBudget(){
//                return budget;
//            }
//
//            public BigDecimal getAnnualFactor(){
//                return new BigDecimal("195").divide(new BigDecimal("365"), MathContext.DECIMAL64);
//            }
//        };
//        calculation.setValue(new BigDecimal("500.00"));
//        calculation.setCalculationType(BudgetCalculationType.BUDGETED);
//
//        // when
//        shortFall = budgetAssignmentService.getShortFall(calculation);
//        BigDecimal recoverableAmountForOccupancy1 = budgetAssignmentService.recoverableAmountForOccupancy(occupancy1, calculation);
//        BigDecimal recoverableAmountForOccupancy2 = budgetAssignmentService.recoverableAmountForOccupancy(occupancy2, calculation);
//
//        // then
//        Assertions.assertThat(
//                shortFall.getBudgetedShortFall().setScale(6, BigDecimal.ROUND_HALF_UP))
//                .isEqualTo(new BigDecimal("35.897436") //
//                );
//        Assertions.assertThat(
//                shortFall.getAuditedShortFall())
//                .isEqualTo(new BigDecimal("0")
//                );
//        assertThat(recoverableAmountForOccupancy1.setScale(6, BigDecimal.ROUND_HALF_UP)).isEqualTo(new BigDecimal("246.575342"));
//        assertThat(recoverableAmountForOccupancy2.setScale(6, BigDecimal.ROUND_HALF_UP)).isEqualTo(new BigDecimal("1.369863"));
//        assertThat(recoverableAmountForOccupancy1
//                .add(recoverableAmountForOccupancy2)
//                .add(shortFall.getBudgetedShortFall())
//                .setScale(6, BigDecimal.ROUND_HALF_UP)).isEqualTo(new BigDecimal("267.123288")); // = 500 * 195 / 365
//
//    }

} 

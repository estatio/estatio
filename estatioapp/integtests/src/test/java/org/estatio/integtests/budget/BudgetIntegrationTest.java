package org.estatio.integtests.budget;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.wrapper.HiddenException;
import org.apache.isis.applib.services.wrapper.InvalidException;

import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.budgetassignment.BudgetAssignmentContributions;
import org.estatio.dom.budgetassignment.BudgetCalculationLinkRepository;
import org.estatio.dom.budgetassignment.ServiceChargeItemRepository;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budget.BudgetRepository;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationRepository;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.budgeting.keytable.FoundationValueType;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.budgeting.keytable.KeyTableRepository;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.lease.LeaseRepository;
import org.estatio.dom.lease.Occupancy;
import org.estatio.dom.lease.OccupancyRepository;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForOxfGb;
import org.estatio.fixture.budget.BudgetItemAllocationsForOxf;
import org.estatio.fixture.budget.BudgetsForOxf;
import org.estatio.fixture.lease.LeaseForOxfTopModel001Gb;
import org.estatio.fixture.lease.LeaseItemForServiceChargeBudgetedForOxfTopModel001Gb;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class BudgetIntegrationTest extends EstatioIntegrationTest {

    @Inject
    BudgetRepository budgetRepository;

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    KeyTableRepository keyTableRepository;

    @Inject
    BudgetCalculationRepository budgetCalculationRepository;

    @Inject
    BudgetCalculationLinkRepository budgetCalculationLinkRepository;

    @Inject
    BudgetAssignmentContributions budgetAssignmentContributions;

    @Inject
    ServiceChargeItemRepository serviceChargeItemRepository;

    @Inject
    LeaseRepository leaseRepository;

    @Inject
    OccupancyRepository occupancyRepository;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, new BudgetItemAllocationsForOxf());
                executionContext.executeChild(this, new LeaseItemForServiceChargeBudgetedForOxfTopModel001Gb());
            }
        });
    }

    public static class RemoveBudget extends BudgetIntegrationTest {

        Property propertyOxf;
        List<Budget> budgetsForOxf;
        Budget budget2015;
        LeaseItem topmodelBudgetServiceChargeItem;
        Occupancy topmodelOccupancy;

        @Rule
        public ExpectedException expectedException = ExpectedException.none();

        @Before
        public void setUp() throws Exception {
            propertyOxf = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
            budgetsForOxf = budgetRepository.findByProperty(propertyOxf);
            budget2015 = budgetRepository.findByPropertyAndStartDate(propertyOxf, BudgetsForOxf.BUDGET_2015_START_DATE);

            // calculate and assign budget2015
            budget2015.calculate();
            budgetAssignmentContributions.assignCalculations(budget2015);

            topmodelBudgetServiceChargeItem = leaseRepository.findLeaseByReference(LeaseForOxfTopModel001Gb.REF).findFirstItemOfType(LeaseItemType.SERVICE_CHARGE_BUDGETED);
            topmodelOccupancy = occupancyRepository.findByLease(topmodelBudgetServiceChargeItem.getLease()).get(0);

        }

        @Test
        public void removeBudget() throws Exception {
            // given
            assertThat(budgetsForOxf.size()).isEqualTo(2);
            assertThat(budgetCalculationRepository.allBudgetCalculations().size()).isEqualTo(75);
            assertThat(budget2015.getKeyTables().size()).isEqualTo(2);
//            assertThat(budgetCalculationLinkRepository.allBudgetCalculationLinks().size()).isEqualTo(3);
            assertThat(serviceChargeItemRepository.findByOccupancy(topmodelOccupancy).size()).isEqualTo(1);

            // when
            budget2015.removeBudget(true);

            // then
            assertThat(budgetRepository.findByProperty(propertyOxf).size()).isEqualTo(1);
            assertThat(budgetCalculationRepository.allBudgetCalculations().size()).isEqualTo(0);
            assertThat(budgetCalculationLinkRepository.allBudgetCalculationLinks().size()).isEqualTo(0);
            assertThat(topmodelBudgetServiceChargeItem.getTerms().size()).isEqualTo(0);
            assertThat(keyTableRepository.allKeyTables().size()).isEqualTo(0);
            assertThat(serviceChargeItemRepository.findByOccupancy(topmodelOccupancy).size()).isEqualTo(0);
        }

        @Test
        public void removeBudgetOnlyInPrototypeMode() throws Exception {

            //then
            expectedException.expect(HiddenException.class);
            expectedException.expectMessage("Reason: Prototyping action not visible in production mode.");
            // when
            wrap(budget2015).removeBudget(true);

        }

    }

    public static class NextBudgetTest extends BudgetIntegrationTest {

        Property propertyOxf;
        List<Budget> budgetsForOxf;
        Budget budget2015;
        Budget budget2015New;
        Budget budget2015NewInBetween;
        Budget budget2016;
        Budget budget2017;
        LocalDate newStartDate;
        LocalDate newInBetweenStartDate;

        @Before
        public void setUp() throws Exception {
            propertyOxf = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
            budgetsForOxf = budgetRepository.findByProperty(propertyOxf);
            budget2015 = budgetRepository.findByPropertyAndStartDate(propertyOxf, BudgetsForOxf.BUDGET_2015_START_DATE);
            budget2016 = budgetRepository.findByPropertyAndStartDate(propertyOxf, BudgetsForOxf.BUDGET_2016_START_DATE);
        }

        @Test
        public void nextBudgetTest() throws Exception {

            // given
            assertThat(budgetsForOxf.size()).isEqualTo(2);

            // when
            newStartDate = new LocalDate(2015, 07, 01);
            budget2015New = budget2015.createNextBudget(newStartDate);

            // then
            assertThat(budgetRepository.findByProperty(propertyOxf).size()).isEqualTo(3);
            assertThat(budget2015.getEndDate()).isEqualTo(new LocalDate(2015, 06, 30));
            assertThat(budget2015New.getStartDate()).isEqualTo(newStartDate);
            assertThat(budget2015New.getEndDate()).isEqualTo(new LocalDate(2015,12,31));

            assertThat(budget2015New.getItems().size()).isEqualTo(budget2015.getItems().size());
            BudgetItem firstNewItem = budget2015New.getItems().first();
            BudgetItem lastNewItem = budget2015New.getItems().last();
            assertThat(firstNewItem.getBudgetItemAllocations().size()).isEqualTo(1);
            assertThat(lastNewItem.getBudgetItemAllocations().size()).isEqualTo(2);
            assertThat(lastNewItem.getBudgetItemAllocations().last().getPercentage()).isEqualTo(new BigDecimal("20.000000"));

            assertThat(budget2015New.getKeyTables().size()).isEqualTo(budget2015.getKeyTables().size());
            KeyTable firstNewKeyTable = budget2015New.getKeyTables().first();
            KeyTable lastNewKeyTable = budget2015New.getKeyTables().last();
            assertThat(firstNewKeyTable.getName()).isEqualTo(budget2015.getKeyTables().first().getName());
            assertThat(lastNewKeyTable.getName()).isEqualTo(budget2015.getKeyTables().last().getName());
            assertThat(firstNewKeyTable.getFoundationValueType()).isEqualTo(FoundationValueType.AREA);
            assertThat(lastNewKeyTable.getFoundationValueType()).isEqualTo(FoundationValueType.COUNT);
            assertThat(firstNewKeyTable.getItems().size()).isEqualTo(budget2015.getKeyTables().first().getItems().size());
            assertThat(firstNewKeyTable.getItems().first().getValue()).isEqualTo(new BigDecimal("3.077000"));

            // and when
            newInBetweenStartDate = new LocalDate(2015, 04, 14);
            budget2015NewInBetween = budget2015.createNextBudget(newInBetweenStartDate);

            // then
            assertThat(budget2015.getEndDate()).isEqualTo(new LocalDate(2015, 04, 13));
            assertThat(budget2015NewInBetween.getStartDate()).isEqualTo(newInBetweenStartDate);
            assertThat(budget2015NewInBetween.getEndDate()).isEqualTo(new LocalDate(2015, 06, 30));
            assertThat(budgetRepository.findByProperty(propertyOxf).size()).isEqualTo(4);

            // and when
            budget2017 = budget2016.createNextBudget(new LocalDate(2017, 01 ,01));

            // then
            assertThat(budget2017.getStartDate()).isEqualTo(new LocalDate(2017, 01, 01));
            assertThat(budget2017.getEndDate()).isEqualTo(new LocalDate(2017, 12, 31));
            assertThat(budgetRepository.findByProperty(propertyOxf).size()).isEqualTo(5);
            assertThat(budget2017.getItems().size()).isEqualTo(budget2016.getItems().size());


        }

        @Rule
        public ExpectedException expectedException = ExpectedException.none();

        @Test
        public void nextBudgetEndDateBeforeStartDateTest() {

            //then
            expectedException.expect(InvalidException.class);
            expectedException.expectMessage("Reason: New start date should be after current start date");
            // when
            wrap(budget2015).createNextBudget(new LocalDate(2014,12,31));

        }

        @Test
        public void nextBudgetEndDateAfterEndDateTest() {

            //then
            expectedException.expect(InvalidException.class);
            expectedException.expectMessage("Reason: New start date cannot be after current end date or first day of next year");
            // when
            wrap(budget2015).createNextBudget(new LocalDate(2016,01,02));

        }

        @Test
        public void nextBudgetAlreadyExistsTest(){

            //then
            expectedException.expect(InvalidException.class);
            expectedException.expectMessage("Reason: This budget already exists");
            // when
            wrap(budget2015).createNextBudget(new LocalDate(2016,01,01));
        }


    }

}

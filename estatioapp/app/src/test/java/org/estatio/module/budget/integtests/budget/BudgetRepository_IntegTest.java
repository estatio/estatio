package org.estatio.module.budget.integtests.budget;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.wrapper.InvalidException;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budget.BudgetRepository;
import org.estatio.module.budget.fixtures.budgets.enums.Budget_enum;
import org.estatio.module.budget.integtests.BudgetModuleIntegTestAbstract;

import static org.assertj.core.api.Assertions.assertThat;

public class BudgetRepository_IntegTest extends BudgetModuleIntegTestAbstract {

    @Inject
    BudgetRepository budgetRepository;

    @Inject
    PropertyRepository propertyRepository;

    @Inject

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, Budget_enum.OxfBudget2015.builder());
                executionContext.executeChild(this, Budget_enum.OxfBudget2016.builder());

            }
        });
    }

    public static class FindByProperty extends BudgetRepository_IntegTest {

        @Test
        public void happyCase() throws Exception {
            // given
            Property property = Property_enum.OxfGb.findUsing(serviceRegistry);
            // when
            final List<Budget> budgetList = budgetRepository.findByProperty(property);
            // then
            assertThat(budgetList.size()).isEqualTo(2);

        }

    }

    public static class FindByPropertyAndStartDate extends BudgetRepository_IntegTest {

        @Test
        public void happyCase() throws Exception {
            // given
            Property property = Property_enum.OxfGb.findUsing(serviceRegistry);
            // when
            final Budget budget = budgetRepository.findByPropertyAndStartDate(property, new LocalDate(2015, 01, 01));
            // then
            assertThat(budget.getProperty()).isEqualTo(property);
            assertThat(budget.getStartDate()).isEqualTo(new LocalDate(2015, 01, 01));
            assertThat(budget.getEndDate()).isEqualTo(new LocalDate(2015, 12, 31));

            // and when
            final Budget budgetNotToBeFound = budgetRepository.findByPropertyAndStartDate(property, new LocalDate(2015, 01, 02));
            //then
            assertThat(budgetNotToBeFound).isEqualTo(null);
        }
    }

    public static class FindOrCreateBudget extends BudgetRepository_IntegTest {

        @Test
        public void happyCase() throws Exception {
            // given
            Property property = Property_enum.OxfGb.findUsing(serviceRegistry);

            // when (case existing budget found)
            final Budget budget = budgetRepository.findOrCreateBudget(property, new LocalDate(2015, 01, 01), new LocalDate(2015, 12, 31));
            // then
            assertThat(budget.getProperty()).isEqualTo(property);
            assertThat(budget.getStartDate()).isEqualTo(new LocalDate(2015, 01, 01));
            assertThat(budget.getEndDate()).isEqualTo(new LocalDate(2015, 12, 31));

            // and when (case no existing budget found)
            final Budget budgetToBeCreated = budgetRepository.findOrCreateBudget(property, new LocalDate(2017, 01, 01), new LocalDate(2017, 12, 31));
            //then
            assertThat(budgetToBeCreated.getProperty()).isEqualTo(property);
            assertThat(budgetToBeCreated.getStartDate()).isEqualTo(new LocalDate(2017, 01, 01));
            assertThat(budgetToBeCreated.getEndDate()).isEqualTo(new LocalDate(2017, 12, 31));
            final List<Budget> budgetList = budgetRepository.findByProperty(property);
            assertThat(budgetList.size()).isEqualTo(3);
        }

    }

    public static class FindByPropertyAndDate extends BudgetRepository_IntegTest {

        @Test
        public void happyCase() throws Exception {
            // given
            Property property = Property_enum.OxfGb.findUsing(serviceRegistry);
            // when
            final Budget budget = budgetRepository.findByPropertyAndDate(property, new LocalDate(2015, 01, 01));
            // then
            assertThat(budget.getProperty()).isEqualTo(property);
            assertThat(budget.getStartDate()).isEqualTo(new LocalDate(2015, 01, 01));
            assertThat(budget.getEndDate()).isEqualTo(new LocalDate(2015, 12, 31));

            // and when end date is given
            final Budget budget2 = budgetRepository.findByPropertyAndDate(property, new LocalDate(2015, 12, 31));
            // then find the same budget
            assertThat(budget2).isEqualTo(budget);

            // and when end some date in interval is given
            final Budget budget3 = budgetRepository.findByPropertyAndDate(property, new LocalDate(2015, 7, 1));
            // then find the same budget
            assertThat(budget3).isEqualTo(budget);

            // and when
            final Budget budgetNotToBeFound = budgetRepository.findByPropertyAndDate(property, new LocalDate(2014, 12, 31));
            //then
            assertThat(budgetNotToBeFound).isEqualTo(null);
        }
    }

    public static class NewBudgetValidationWorks extends BudgetRepository_IntegTest {

        @Rule
        public ExpectedException expectedException = ExpectedException.none();

        @Test
        public void budgetPeriodCannotExceedYear() throws Exception {

            // given
            final Property property = Property_enum.OxfGb.findUsing(serviceRegistry);

            //then
            expectedException.expect(InvalidException.class);
            expectedException.expectMessage("Reason: A budget should have an end date in the same year as start date");

            // when
            wrap(budgetRepository).newBudget(property, new LocalDate(2010,01,01), new LocalDate(2011,01,01));

        }

        @Test
        public void emptyStartDate() {

            // given
            final Property property = Property_enum.OxfGb.findUsing(serviceRegistry);

            //then
            expectedException.expect(InvalidException.class);

            // when
            wrap(budgetRepository).newBudget(property, null, new LocalDate(2010,12,31));
        }

        @Test
        public void wrongBudgetDates() {

            // given
            final Property property = Property_enum.OxfGb.findUsing(serviceRegistry);

            //then
            expectedException.expect(InvalidException.class);
            expectedException.expectMessage("Reason: End date can not be before start date");

            // when
            wrap(budgetRepository).newBudget(property, new LocalDate(2010,01,03), new LocalDate(2010,01,01));

        }

        @Test
        public void overlappingDates() {

            // given
            final Property property = Property_enum.OxfGb.findUsing(serviceRegistry);
            final Budget budget = budgetRepository.findByPropertyAndDate(property, new LocalDate(2015, 01, 01));

            //then
            expectedException.expect(InvalidException.class);
            expectedException.expectMessage("Reason: A budget cannot overlap an existing budget");

            //when
            wrap(budgetRepository).newBudget(property, new LocalDate(2015,12,30), new LocalDate(2015,12,31));

        }

    }

}

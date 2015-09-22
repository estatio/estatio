package org.estatio.integtests.budget;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budget.Budgets;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForOxfGb;
import org.estatio.fixture.budget.BudgetsForOxf;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by jodo on 19/08/15.
 */
public class BudgetRepositoryTest extends EstatioIntegrationTest {

    @Inject
    Budgets budgetRepository;

    @Inject
    PropertyRepository propertyRepository;

    @Inject

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, new BudgetsForOxf());
            }
        });
    }

    public static class FindByProperty extends BudgetRepositoryTest {

        @Test
        public void happyCase() throws Exception {
            // given
            Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
            // when
            final List<Budget> budgetList = budgetRepository.findByProperty(property);
            // then
            assertThat(budgetList.size()).isEqualTo(2);

        }

    }

    public static class FindByPropertyAndStartDate extends BudgetRepositoryTest {

        @Test
        public void happyCase() throws Exception {
            // given
            Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
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

    public static class FindOrCreateBudget extends BudgetRepositoryTest {

        @Test
        public void happyCase() throws Exception {
            // given
            Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);

            // when (case existing budget found)
            final Budget budget = budgetRepository.findOrCreateBudget(property, new LocalDate(2015, 01, 01), new LocalDate(2015,12,31));
            // then
            assertThat(budget.getProperty()).isEqualTo(property);
            assertThat(budget.getStartDate()).isEqualTo(new LocalDate(2015, 01, 01));
            assertThat(budget.getEndDate()).isEqualTo(new LocalDate(2015, 12, 31));

            // and when (case no existing budget found)
            final Budget budgetToBeCreated = budgetRepository.findOrCreateBudget(property, new LocalDate(2017, 01, 01), new LocalDate(2017,12,31));
            //then
            assertThat(budgetToBeCreated.getProperty()).isEqualTo(property);
            assertThat(budgetToBeCreated.getStartDate()).isEqualTo(new LocalDate(2017, 01, 01));
            assertThat(budgetToBeCreated.getEndDate()).isEqualTo(new LocalDate(2017, 12, 31));
            final List<Budget> budgetList = budgetRepository.findByProperty(property);
            assertThat(budgetList.size()).isEqualTo(3);
        }

    }



}

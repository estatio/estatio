package org.estatio.integtests.budget;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budget.BudgetRepository;
import org.estatio.dom.charge.Charge;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForOxfGb;
import org.estatio.fixture.budget.BudgetItemAllocationsForOxf;
import org.estatio.fixture.budget.BudgetsForOxf;
import org.estatio.fixture.charge.ChargeRefData;
import org.estatio.integtests.EstatioIntegrationTest;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by jodo on 19/08/15.
 */
public class BudgetRepositoryTest extends EstatioIntegrationTest {

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
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, new BudgetsForOxf());
                executionContext.executeChild(this, new BudgetItemAllocationsForOxf());
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

    public static class FindByPropertyAndDate extends BudgetRepositoryTest {

        @Test
        public void happyCase() throws Exception {
            // given
            Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
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


    public static class GetTargetCharges extends BudgetRepositoryTest {

        @Test
        public void happyCase() throws Exception {

            // given
            final Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
            final Budget budget = budgetRepository.findByPropertyAndDate(property, new LocalDate(2015, 01, 01));

            // when
            List<Charge> targetCharges = budget.getTargetCharges();

            // then
            assertThat(targetCharges.size()).isEqualTo(1);
            assertThat(targetCharges.get(0).getReference()).isEqualTo(ChargeRefData.GB_SERVICE_CHARGE);

        }

    }


}

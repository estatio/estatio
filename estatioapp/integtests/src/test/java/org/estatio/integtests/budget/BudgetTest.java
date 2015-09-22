package org.estatio.integtests.budget;

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
public class BudgetTest extends EstatioIntegrationTest {

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

    public static class ValidateChangeDates extends BudgetTest {

        @Test
        public void happyCase() throws Exception {
            // given (2nd budget for Oxf)
            Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
            LocalDate startDate = new LocalDate(2016,01,01);
            LocalDate endDate = new LocalDate(2016,12,31);
            Budget secondBudgetForOxf = budgetRepository.findByPropertyAndStartDate(property, startDate);

            // when (overlapping startdate)
            LocalDate overlappingStartDate = new LocalDate(2015,12,31);
            final String validateString0 = secondBudgetForOxf.validateChangeDates(overlappingStartDate, endDate);

            // then
            assertThat(validateString0).isEqualTo("A budget cannot overlap an existing budget.");

            // when (overlapping enddate)
            LocalDate nonOverlappingStartDate1 = new LocalDate(2014,01,01);
            LocalDate overlappingEndDate = new LocalDate(2015,01,01);
            final String validateString1 = secondBudgetForOxf.validateChangeDates(nonOverlappingStartDate1, overlappingEndDate);

            // then
            assertThat(validateString1).isEqualTo("A budget cannot overlap an existing budget.");

            // and when (not overlapping)
            LocalDate nonOverlappingStartDate2 = new LocalDate(2016,01,02);
            final String validateString2 = secondBudgetForOxf.validateChangeDates(nonOverlappingStartDate2, endDate);

            // then
            assertThat(validateString2).isEqualTo(null);

        }

    }

}

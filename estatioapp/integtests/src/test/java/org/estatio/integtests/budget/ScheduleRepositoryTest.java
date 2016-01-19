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
import org.estatio.dom.budgeting.budget.BudgetRepository;
import org.estatio.dom.budgeting.schedule.Schedule;
import org.estatio.dom.budgeting.schedule.Schedules;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.Charges;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForOxfGb;
import org.estatio.fixture.budget.SchedulesForOxf;
import org.estatio.fixture.charge.ChargeRefData;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by jodo on 19/08/15.
 */
public class ScheduleRepositoryTest extends EstatioIntegrationTest {

    @Inject
    Schedules scheduleRepository;

    @Inject
    BudgetRepository budgetRepository;

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    Charges chargeRepository;


    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, new SchedulesForOxf());
            }
        });
    }

    public static class FindByProperty extends ScheduleRepositoryTest {

        @Test
        public void happyCase() throws Exception {
            // given
            Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
            // when
            final List<Schedule> scheduleList = scheduleRepository.findByProperty(property);
            // then
            assertThat(scheduleList.size()).isEqualTo(1);

        }

    }

    public static class FindByBudget extends ScheduleRepositoryTest {

        @Test
        public void happyCase() throws Exception {
            // given
            Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
            Budget budget = budgetRepository.findByPropertyAndStartDate(property, new LocalDate(2015, 01, 01));
            // when
            final List<Schedule> scheduleList = scheduleRepository.findByBudget(budget);
            // then
            assertThat(scheduleList.size()).isEqualTo(1);

        }

    }

    public static class FindByPropertyAndCharge extends ScheduleRepositoryTest {

        @Test
        public void happyCase() throws Exception {
            // given
            Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
            Budget budget = budgetRepository.findByPropertyAndStartDate(property, new LocalDate(2015, 01, 01));
            Charge charge = chargeRepository.findByReference(ChargeRefData.GB_SERVICE_CHARGE);
            // when
            final List<Schedule> scheduleList = scheduleRepository.findByPropertyAndCharge(property, charge);
            // then
            assertThat(scheduleList.size()).isEqualTo(1);

        }

    }

    public static class FindUniqueSchedule extends ScheduleRepositoryTest {

        @Test
        public void happyCase() throws Exception {
            // given
            Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
            Budget budget = budgetRepository.findByPropertyAndStartDate(property, new LocalDate(2015, 01, 01));
            Charge charge = chargeRepository.findByReference(ChargeRefData.GB_SERVICE_CHARGE);
            LocalDate startDate = new LocalDate(2015,01,01);
            LocalDate endDate = new LocalDate(2015,12,31);
            // when
            final Schedule schedule = scheduleRepository.findUniqueSchedule(property, charge, startDate, endDate);
            // then
            assertThat(schedule.getProperty()).isEqualTo(property);
            assertThat(schedule.getCharge()).isEqualTo(charge);
            assertThat(schedule.getStartDate()).isEqualTo(startDate);
            assertThat(schedule.getEndDate()).isEqualTo(endDate);
        }

    }

    public static class FindOrCreateSchedule extends ScheduleRepositoryTest {

        @Test
        public void happyCase() throws Exception {
            // given
            Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
            Budget budget = budgetRepository.findByPropertyAndStartDate(property, new LocalDate(2015, 01, 01));
            Charge charge = chargeRepository.findByReference(ChargeRefData.GB_SERVICE_CHARGE);
            LocalDate startDate = new LocalDate(2015,01,01);
            LocalDate endDate = new LocalDate(2015,12,31);

            // when (case existing schedule found)
            final Schedule schedule = scheduleRepository.findOrCreateSchedule(
                    property,
                    budget,
                    startDate,
                    endDate,
                    charge,
                    Schedule.Status.OPEN
            );
            // then
            assertThat(schedule.getProperty()).isEqualTo(property);
            assertThat(schedule.getStartDate()).isEqualTo(startDate);
            assertThat(schedule.getEndDate()).isEqualTo(endDate);

            // and when (case no existing schedule found)
            Budget budget2 = budgetRepository.findByPropertyAndStartDate(property, new LocalDate(2016, 01, 01));
            LocalDate startDate2 = new LocalDate(2016,01,01);
            LocalDate endDate2 = new LocalDate(2016,12,31);
            final Schedule scheduleToBeCreated = scheduleRepository.findOrCreateSchedule(
                    property,
                    budget2,
                    startDate2,
                    endDate2,
                    charge,
                    Schedule.Status.OPEN
            );
            //then
            assertThat(scheduleToBeCreated.getProperty()).isEqualTo(property);
            assertThat(scheduleToBeCreated.getBudget()).isEqualTo(budget2);
            assertThat(scheduleToBeCreated.getCharge()).isEqualTo(charge);
            assertThat(scheduleToBeCreated.getStartDate()).isEqualTo(startDate2);
            assertThat(scheduleToBeCreated.getEndDate()).isEqualTo(endDate2);
            final List<Schedule> schdeuleList = scheduleRepository.findByProperty(property);
            assertThat(schdeuleList.size()).isEqualTo(2);
        }

    }



}

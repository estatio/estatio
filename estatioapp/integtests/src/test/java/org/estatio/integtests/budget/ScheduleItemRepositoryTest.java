package org.estatio.integtests.budget;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budget.BudgetRepository;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.budgeting.keytable.KeyTableRepository;
import org.estatio.dom.budgeting.schedule.Schedule;
import org.estatio.dom.budgeting.schedule.Schedules;
import org.estatio.dom.budgeting.scheduleitem.ScheduleItem;
import org.estatio.dom.budgeting.scheduleitem.ScheduleItems;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForOxfGb;
import org.estatio.fixture.budget.SchedulesForOxf;
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
public class ScheduleItemRepositoryTest extends EstatioIntegrationTest {

    @Inject
    ScheduleItems scheduleItemRepository;

    @Inject
    Schedules scheduleRepository;

    @Inject
    BudgetRepository budgetRepository;

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    KeyTableRepository keytablesRepository;


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

    public static class validateNewScheduleItem extends ScheduleItemRepositoryTest {

        @Test
        public void doubleScheduleItem() throws Exception {

            // given
            Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
            Schedule schedule = scheduleRepository.findByProperty(property).get(0);
            ScheduleItem scheduleItem = schedule.getScheduleItems().first();

            //when, then
            assertThat(scheduleItemRepository
                    .validateNewScheduleItem(
                            schedule,scheduleItem.getKeyTable(),
                            scheduleItem.getBudgetItem(),
                            null)
            ).isEqualTo("This schedule item already exists");

        }

    }

    public static class FindBySchedule extends ScheduleItemRepositoryTest {

        @Test
        public void happyCase() throws Exception {
            // given
            Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
            Schedule schedule = scheduleRepository.findByProperty(property).get(0);
            // when
            final List<ScheduleItem> scheduleItemList = scheduleItemRepository.findBySchedule(schedule);
            // then
            assertThat(scheduleItemList.size()).isEqualTo(3);

        }

    }

    public static class FindByBudgetItem extends ScheduleItemRepositoryTest {

        @Test
        public void happyCase() throws Exception {
            // given
            Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
            Budget budget = budgetRepository.findByPropertyAndStartDate(property, new LocalDate(2015, 01, 01));
            BudgetItem budgetItem = budget.getItems().last();
            // when
            final List<ScheduleItem> scheduleItemList = scheduleItemRepository.findByBudgetItem(budgetItem);
            // then
            assertThat(scheduleItemList.size()).isEqualTo(2);

        }

    }


    public static class FindByKeyTable extends ScheduleItemRepositoryTest {

        @Test
        public void happyCase() throws Exception {
            // given
            Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
            KeyTable keyTable = keytablesRepository.findByProperty(property).get(0);
            // when
            final List<ScheduleItem> scheduleItemList = scheduleItemRepository.findByKeyTable(keyTable);
            // then
            assertThat(scheduleItemList.size()).isEqualTo(2);

        }

    }

    public static class FindByScheduleAndBudgetItemAndKeyTable extends ScheduleItemRepositoryTest {

        @Test
        public void happyCase() throws Exception {
            // given
            Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
            Schedule schedule = scheduleRepository.findByProperty(property).get(0);
            Budget budget = budgetRepository.findByProperty(property).get(0);
            BudgetItem budgetItem = budget.getItems().first();
            KeyTable keyTable = keytablesRepository.findByProperty(property).get(0);
            // when
            final ScheduleItem unqiueSchedule = scheduleItemRepository.findByScheduleAndBudgetItemAndKeyTable(schedule, budgetItem, keyTable);
            // then
            assertThat(unqiueSchedule.getSchedule()).isEqualTo(schedule);
            assertThat(unqiueSchedule.getBudgetItem()).isEqualTo(budgetItem);
            assertThat(unqiueSchedule.getKeyTable()).isEqualTo(keyTable);
        }

    }

}

package org.estatio.dom.budgeting.scheduleitem.contributed;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.budgeting.budgetitem.BudgetItems;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.budgeting.keytable.KeyTables;
import org.estatio.dom.budgeting.schedule.Schedule;
import org.estatio.dom.budgeting.schedule.Schedules;
import org.estatio.dom.budgeting.scheduleitem.ScheduleItem;
import org.estatio.dom.budgeting.scheduleitem.ScheduleItems;

/**
 * Created by jodo on 10/09/15.
 */
@DomainService(nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY)
public class ScheduleContributions {


    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public ScheduleItem createScheduleItem(
            final Schedule schedule,
            final KeyTable keyTable,
            final BudgetItem budgetItem,
            final BigDecimal percentage
    ) {
        return scheduleItems.newScheduleItem(schedule, keyTable,budgetItem, percentage);
    }

    public List<Schedule> autoComplete0CreateScheduleItem(
            String search) {
        return schedules.allSchedules();
    }

    public List<KeyTable> choices1CreateScheduleItem(
            final Schedule schedule,
            final KeyTable keyTable,
            final BudgetItem budgetItem,
            final BigDecimal percentage) {
        return keyTables.findByProperty(schedule.getProperty());
    }

    public List<BudgetItem> choices2CreateScheduleItem(
            final Schedule schedule,
            final KeyTable keyTable,
            final BudgetItem budgetItem,
            final BigDecimal percentage) {
        return budgetItems.findByBudget(schedule.getBudget());
    }

    public BigDecimal default3CreateScheduleItem(
            final Schedule schedule,
            final KeyTable keyTable,
            final BudgetItem budgetItem,
            final BigDecimal percentage) {
        return new BigDecimal(100);
    }

    public String validateCreateScheduleItem(
            final Schedule schedule,
            final KeyTable keyTable,
            final BudgetItem budgetItem,
            final BigDecimal percentage
    ){
        return scheduleItems.validateNewScheduleItem(schedule,keyTable,budgetItem, percentage);
    }

    @Inject
    private ScheduleItems scheduleItems;

    @Inject
    private BudgetItems budgetItems;

    @Inject
    private Schedules schedules;

    @Inject
    private KeyTables keyTables;

}

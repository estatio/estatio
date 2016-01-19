package org.estatio.dom.budgeting.schedule.contributed;

import com.google.common.collect.Lists;
import org.apache.isis.applib.annotation.*;
import org.estatio.dom.asset.Property;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budget.BudgetRepository;
import org.estatio.dom.budgeting.schedule.Schedule;
import org.estatio.dom.budgeting.schedule.Schedules;
import org.estatio.dom.budgeting.scheduleitem.ScheduleItem;
import org.estatio.dom.budgeting.scheduleitem.ScheduleItems;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.valuetypes.LocalDateInterval;
import org.joda.time.LocalDate;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@DomainService(nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY)
public class PropertyAndBudgetContributions {


    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    @MemberOrder(name = "schedules", sequence = "1")
    public Schedule createSchedule(
            final Property property,
            final Budget budget,
            final LocalDate startDate,
            final LocalDate endDate,
            final Charge charge) {
        return schedules.newSchedule(property, budget, startDate, endDate, charge, Schedule.Status.OPEN);
    }

    public Property default0CreateSchedule(
            final Property property,
            final Budget budget,
            final LocalDate startDate,
            final LocalDate endDate,
            final Charge charge) {
        return budget.getProperty();
    }

    public List<Property> choices0CreateSchedule(
            final Property property,
            final Budget budget,
            final LocalDate startDate,
            final LocalDate endDate,
            final Charge charge) {
        return Lists.newArrayList(budget.getProperty());
    }

    public List<Budget> choices1CreateSchedule(
            final Property property,
            final Budget budget,
            final LocalDate startDate,
            final LocalDate endDate,
            final Charge charge) {
        return budgetRepository.findByProperty(property);
    }

    public String validateCreateSchedule(
            final Property property,
            final Budget budget,
            final LocalDate startDate,
            final LocalDate endDate,
            final Charge charge) {
        if (!new LocalDateInterval(startDate, endDate).isValid()) {
            return "End date can not be before start date";
        }

        for (Schedule schedule : schedules.findByPropertyAndCharge(property, charge)) {
            if (schedule.getInterval().overlaps(new LocalDateInterval(startDate, endDate))) {
                return "A new schedule cannot overlap an existing schedule for this charge.";
            }
        }

        return null;
    }


    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(render = RenderType.LAZILY)
    public List<Schedule> schedules(final Property property) {
        return schedules.findByProperty(property);
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(render = RenderType.EAGERLY)
    public List<ScheduleItem> scheduleItems(final Budget budget) {
        List<ScheduleItem> results = new ArrayList<>();
        for (Schedule schedule : schedules.findByBudget(budget)) {
            results.addAll(scheduleItems.findBySchedule(schedule));
        }
        return results;
    }

    @Inject
    BudgetRepository budgetRepository;

    @Inject
    Schedules schedules;

    @Inject
    ScheduleItems scheduleItems;

}

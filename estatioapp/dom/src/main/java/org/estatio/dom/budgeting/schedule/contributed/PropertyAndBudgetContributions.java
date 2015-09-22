package org.estatio.dom.budgeting.schedule.contributed;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.asset.Property;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budget.Budgets;
import org.estatio.dom.budgeting.schedule.Schedule;
import org.estatio.dom.budgeting.schedule.Schedules;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.valuetypes.LocalDateInterval;

/**
 * Created by jodo on 10/09/15.
 */
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
        return budgets.findByProperty(property);
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
    public List<Schedule> schedules(final Budget budget) {
        return schedules.findByBudget(budget);
    }


    @Inject
    Budgets budgets;

    @Inject
    Schedules schedules;

}

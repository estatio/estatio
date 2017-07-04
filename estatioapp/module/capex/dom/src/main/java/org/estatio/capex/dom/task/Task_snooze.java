package org.estatio.capex.dom.task;

import java.util.List;

import javax.annotation.Nullable;

import org.assertj.core.util.Lists;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

@Mixin(method = "act")
public class Task_snooze extends Task_abstract {

    public Task_snooze(final Task task) {
        super(task);
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ACTION, cssClassFa = "fa-bell-o")
    public Task act(
            @Nullable final LocalDate until,
            @Nullable final Integer daysFromNow
            ) {
        final LocalDateTime snoozeUntil = until != null
                ? until.toLocalDateTime(task.getCreatedOn().toLocalTime())
                : task.getCreatedOn().plusDays(daysFromNow);

        task.setCreatedOn(snoozeUntil);
        return task;
    }

    public String validateAct(
            final LocalDate until,
            final Integer daysFromNow) {

        if(until == null && daysFromNow == null) {
            return "Specify when to snooze until";
        }
        return null;
    }


    public LocalDate default0Act() {
        return task.getCreatedOn().plusDays(3).toLocalDate();
    }

    public List<Integer> choices1Act() {
        return Lists.newArrayList(1,2,3,4,7,14,21,28);
    }


}

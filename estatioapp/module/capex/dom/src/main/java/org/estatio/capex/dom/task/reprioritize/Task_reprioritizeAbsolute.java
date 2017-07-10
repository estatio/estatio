package org.estatio.capex.dom.task.reprioritize;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.task.Task;
import org.estatio.capex.dom.task.Task_abstract;

@Mixin(method = "act")
public class Task_reprioritizeAbsolute extends Task_abstract {

    public Task_reprioritizeAbsolute(final Task task) {
        super(task);
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ACTION, cssClassFa = "fa-bell-o")
    public Task act( final LocalDate newDate) {
        final LocalDateTime newDateTime = newDate.toLocalDateTime(task.getCreatedOn().toLocalTime());

        task.setCreatedOn(newDateTime);
        return task;
    }

    public LocalDate default0Act() {
        return task.getCreatedOn().toLocalDate();
    }

}

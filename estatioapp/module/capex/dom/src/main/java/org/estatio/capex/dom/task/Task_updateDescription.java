package org.estatio.capex.dom.task;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

/**
 * TODO: inline this mixin.
 */
@Mixin(method = "act")
public class Task_updateDescription {

    protected final Task task;

    public Task_updateDescription(final Task task) {
        this.task = task;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Task act(final String description) {
        task.setDescription(description);
        return task;
    }

    public String default0Act() {
        return task.getDescription();
    }

    public String disableAct() {
        return task.isCompleted() ? "Task has already been completed" : null;
    }

}

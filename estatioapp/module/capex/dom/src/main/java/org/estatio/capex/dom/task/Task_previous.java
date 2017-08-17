package org.estatio.capex.dom.task;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

/**
 * TODO: inline this mixin (though note that it inherits functionality from superclass).
 */
@Mixin(method = "act")
public class Task_previous extends Task_abstract {

    public Task_previous(final Task task) {
        super(task);
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ACTION, cssClassFa = "fa-step-backward")
    public Task act() {
        return this.nextTaskBefore(this.task);
    }

    public String disableAct() {
        return act() == task ?  "No more tasks" : null;
    }


}

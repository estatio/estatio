package org.estatio.module.capex.dom.state;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.capex.dom.task.Task;

/**
 * TODO: inline this mixin.
 */
@Mixin(method = "act")
public class Task_viewTransition {

    private final Task task;
    public Task_viewTransition(final Task task) {
        this.task = task;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Object act() {
        return stateTransitionService.findFor(task);
    }

    @Inject
    StateTransitionService stateTransitionService;

}

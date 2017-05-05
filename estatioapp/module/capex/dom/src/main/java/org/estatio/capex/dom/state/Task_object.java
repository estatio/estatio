package org.estatio.capex.dom.state;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.task.Task;

@Mixin(method = "prop")
public class Task_object {

    private final Task task;
    public Task_object(final Task task) {
        this.task = task;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public Object prop() {
        final StateTransition stateTransition = stateTransitionService.findFor(task);
        return stateTransition != null ? stateTransition.getDomainObject() : null;
    }

    @Inject
    StateTransitionService stateTransitionService;

}

package org.estatio.capex.dom.task;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.state.State;
import org.estatio.capex.dom.state.StateTransition;
import org.estatio.capex.dom.state.StateTransitionService;
import org.estatio.capex.dom.state.StateTransitionType;

@Mixin(method = "act")
public class Task_checkState {

    private final Task task;

    public Task_checkState(final Task task) {
        this.task = task;
    }

    @Action(
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(
            contributed= Contributed.AS_ACTION,
            cssClassFa = "fa-question-circle" // override isis-non-changing.properties
    )
    public <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
    > Task act() {

        ST st = stateTransitionService.findFor(task);

        stateTransitionService.checkState(st);

        return task;
    }

    @Inject
    StateTransitionService stateTransitionService;

}

package org.estatio.capex.dom.task;

import org.estatio.capex.dom.state.State;
import org.estatio.capex.dom.state.StateTransition;
import org.estatio.capex.dom.state.StateTransitionType;
import org.estatio.dom.roles.EstatioRole;

public interface NewTaskMixin<
        DO,
        ST extends StateTransition<DO, ST, STT, S>,
        STT extends StateTransitionType<DO, ST, STT, S>,
        S extends State<S>
    > {

    Task newTask(
            final EstatioRole assignTo,
            final STT transitionType,
            final String description);

}

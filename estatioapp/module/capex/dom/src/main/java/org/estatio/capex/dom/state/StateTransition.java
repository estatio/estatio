package org.estatio.capex.dom.state;

import org.estatio.capex.dom.task.Task;

public interface StateTransition<
        DO,
        ST extends StateTransition<DO, ST, STT, S>,
        STT extends StateTransitionType<DO, ST, STT, S>,
        S extends State<S>
        > {

    DO getDomainObject();

    Task getTask();

    STT getTransitionType();

}

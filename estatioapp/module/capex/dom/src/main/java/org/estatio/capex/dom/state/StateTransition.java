package org.estatio.capex.dom.state;

import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.capex.dom.task.Task;

/**
 * A persisted record of some state transition of a domain object, either to occur in the future, or that has occurred.
 *
 * The corresponding {@link Task} indicaties the user/role that is to/did perform the state transition.
 */
public interface StateTransition<
        DO,
        ST extends StateTransition<DO, ST, STT, S>,
        STT extends StateTransitionType<DO, ST, STT, S>,
        S extends State<S>
        > {

    DO getDomainObject();

    Task getTask();

    S getFromState();

    STT getTransitionType();

    S getToState();

    @Programmatic
    void completed();
}

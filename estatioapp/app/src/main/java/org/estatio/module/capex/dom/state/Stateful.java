package org.estatio.module.capex.dom.state;

import org.apache.isis.applib.annotation.Programmatic;

/**
 * For domain objects to optionally implement indicating that they are aware of their own state
 * (with respect to a particular state machine, as identified by the class of {@link StateTransition}).
 */
public interface Stateful {

    /**
     * Returns the state of the domain object, or null if the particular
     * {@link StateTransition state machine} isn't recognised.
     */
    @Programmatic
    public <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
    > S getStateOf(Class<ST> stateTransitionClass);

    /**
     * To "push" the state into the stateful object.
     */
    @Programmatic
    public <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
    > void setStateOf(Class<ST> stateTransitionClass, S newState);

}

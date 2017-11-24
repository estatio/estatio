package org.estatio.module.capex.dom.state;

import org.estatio.module.capex.dom.task.Task;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a domain object (which is also treated as the event source) transitioning from one {@link State} to another.
 *
 * <p>
 *     There may or may not be a corresponding persisted {@link StateTransition} entity (and associated {@link Task})
 *     for this transition of the domain object's state.
 * </p>
 */
public abstract class StateTransitionEvent<
        DO,
        ST extends StateTransition<DO, ST, STT, S>,
        STT extends StateTransitionType<DO, ST, STT, S>,
        S extends State<S>
        > extends java.util.EventObject {

    public enum Phase {
        TRANSITIONING,
        TRANSITIONED
    }

    private final DO domainObject;
    private final ST stateTransitionIfAny;
    private final STT transitionType;

    public StateTransitionEvent(
            final DO domainObject,
            final ST stateTransitionIfAny,
            final STT transitionType) {
        super(domainObject);
        this.domainObject = domainObject;
        this.stateTransitionIfAny = stateTransitionIfAny;
        this.transitionType = transitionType;
    }

    @Override
    public DO getSource() {
        return (DO) super.getSource();
    }

    public DO getDomainObject() {
        return domainObject;
    }

    public ST getStateTransitionIfAny() {
        return (ST) getSource();
    }

    public STT getTransitionType() {
        return transitionType;
    }

    @Getter @Setter
    private Phase phase;

}

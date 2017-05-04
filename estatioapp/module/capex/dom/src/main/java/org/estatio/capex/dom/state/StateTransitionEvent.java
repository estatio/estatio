package org.estatio.capex.dom.state;

import org.estatio.capex.dom.task.Task;

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
public class StateTransitionEvent<
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
    private final STT transitionType;
    private final ST stateTransitionIfAny;

    public StateTransitionEvent(final DO domainObject, final STT transitionType, final ST stateTransitionIfAny) {
        super(domainObject);
        this.domainObject = domainObject;
        this.transitionType = transitionType;
        this.stateTransitionIfAny = stateTransitionIfAny;
    }

    @Override
    public DO getSource() {
        return (DO) super.getSource();
    }

    public DO getDomainObject() {
        return domainObject;
    }

    public STT getTransitionType() {
        return transitionType;
    }

    public ST getStateTransitionIfAny() {
        return (ST) getSource();
    }

    @Getter @Setter
    private Phase phase;

}

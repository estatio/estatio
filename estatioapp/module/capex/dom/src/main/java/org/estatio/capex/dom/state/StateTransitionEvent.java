package org.estatio.capex.dom.state;

import lombok.Getter;
import lombok.Setter;

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

    public StateTransitionEvent(final ST stateTransition) {
        super(stateTransition);
    }

    public ST getStateTransition() {
        return (ST) getSource();
    }

    @Getter @Setter
    private Phase phase;

}

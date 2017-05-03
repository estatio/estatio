package org.estatio.capex.dom.task;

import org.estatio.capex.dom.state.State;
import org.estatio.capex.dom.state.StateOwner;
import org.estatio.capex.dom.state.StateTransitionType;

public interface StateTransition<
        T extends StateTransition<T, DO, TT, TS>,
        DO extends StateOwner<DO, TS>,
        TT extends StateTransitionType<DO, TT, TS>,
        TS extends State<DO, TS>
        > extends StateOwner<DO, TS> {

    DO getDomainObject();

    TS getFromState();

    TT getTransitionType();

    TS getToState();


}

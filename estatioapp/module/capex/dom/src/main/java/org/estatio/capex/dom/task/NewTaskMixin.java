package org.estatio.capex.dom.task;

import org.estatio.capex.dom.state.State;
import org.estatio.capex.dom.state.StateOwner;
import org.estatio.capex.dom.state.StateTransitionType;
import org.estatio.dom.roles.EstatioRole;

public interface NewTaskMixin<SO extends StateOwner<SO, TS>, TT extends StateTransitionType<SO, TT, TS>, TS extends State<SO, TS>> {

    Task<?, SO, TT, TS> newTask(
            final EstatioRole assignTo,
            final TT taskTransition,
            final String description);

}

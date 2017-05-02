package org.estatio.capex.dom.task;

import org.estatio.dom.roles.EstatioRole;

public interface NewTaskMixin<DO extends TaskStateOwner<DO, TS>, TT extends TaskTransition<DO, TT, TS>, TS extends TaskState<DO, TS>> {

    Task<?, DO, TT, TS> newTask(
            final EstatioRole assignTo,
            final TT taskTransition,
            final String description);

}

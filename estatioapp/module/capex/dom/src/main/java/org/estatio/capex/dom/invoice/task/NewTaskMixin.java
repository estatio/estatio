package org.estatio.capex.dom.invoice.task;

import org.estatio.capex.dom.invoice.rule.TaskState;
import org.estatio.capex.dom.invoice.rule.TaskTransition;
import org.estatio.capex.dom.task.Task;
import org.estatio.dom.roles.EstatioRole;

public interface NewTaskMixin<DO extends TaskState.Owner<DO, S>, S extends TaskState<DO, S>, TT extends TaskTransition<DO, S, TT>> {

    Task<?> newTask(
            final EstatioRole assignTo,
            final String description,
            final TT taskTransition);

}

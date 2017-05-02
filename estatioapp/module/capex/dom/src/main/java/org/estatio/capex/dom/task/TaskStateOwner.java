package org.estatio.capex.dom.task;

import org.apache.isis.applib.annotation.Programmatic;

public interface TaskStateOwner<DO extends TaskStateOwner<DO, S>, S extends TaskState<DO, S>> {

    @Programmatic
    S getTaskState();

    @Programmatic
    void setTaskState(S taskState);

}

package org.estatio.capex.dom.task;

import org.apache.isis.applib.annotation.Programmatic;

public interface StateOwner<DO extends StateOwner<DO, S>, S extends State<DO, S>> {

    @Programmatic
    S getTaskState();

    @Programmatic
    void setTaskState(S taskState);

}

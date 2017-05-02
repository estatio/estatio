package org.estatio.capex.dom.invoice.rule;

import org.apache.isis.applib.annotation.Programmatic;

public interface TaskState<DO extends TaskState.Owner<DO, S>, S extends TaskState<DO, S>> {

    interface Owner<DO extends Owner<DO, S>, S extends TaskState<DO, S>> {

        @Programmatic
        S getTaskState();

        @Programmatic
        void setTaskState(S taskState);

    }

}

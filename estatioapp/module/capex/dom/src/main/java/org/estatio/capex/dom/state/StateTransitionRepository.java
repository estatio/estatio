package org.estatio.capex.dom.state;

import java.util.List;

import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.capex.dom.task.Task;
import org.estatio.dom.roles.EstatioRole;

public interface StateTransitionRepository<
        DO,
        ST extends StateTransition<DO, ST, STT, S>,
        STT extends StateTransitionType<DO, ST, STT, S>,
        S extends State<S>
        > {

    @Programmatic
    List<ST> findByDomainObject(DO domainObject);

    @Programmatic
    ST findByDomainObjectAndIncomplete(final DO domainObject);

    @Programmatic
    ST findByTask(final Task task);

    /**
     * Creates the transition with corresponding {@link Task}.
     */
    @Programmatic
    ST create(
            final DO domainObject,
            final STT transitionType,
            final S fromState,
            final EstatioRole taskAssignToIfAny,
            final String taskDescription);
}

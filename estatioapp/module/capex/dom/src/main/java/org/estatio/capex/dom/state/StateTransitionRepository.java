package org.estatio.capex.dom.state;

import java.util.List;

import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.capex.dom.task.Task;
import org.estatio.dom.party.Person;
import org.estatio.dom.party.role.IPartyRoleType;

public interface StateTransitionRepository<
        DO,
        ST extends StateTransition<DO, ST, STT, S>,
        STT extends StateTransitionType<DO, ST, STT, S>,
        S extends State<S>
        > {

    @Programmatic
    List<ST> listAll();

    @Programmatic
    List<ST> findByDomainObject(DO domainObject);

    @Programmatic
    ST findByDomainObjectAndCompleted(final DO domainObject, final boolean whetherCompleted);

    @Programmatic
    ST findByTask(final Task task);

    @Programmatic
    ST findByDomainObjectAndToState(
            final DO domainObject,
            final S toState);

        /**
         * Creates the transition with corresponding {@link Task}.
         */
    @Programmatic
    ST create(
            final DO domainObject,
            final STT transitionType,
            final S fromState,
            final IPartyRoleType taskAssignToIfAny,
            final Person personToAssignToIfAny, final String taskDescription);

    /**
     * Removes all {@link StateTransition}s for the provided domain object.
     */
    @Programmatic
    void deleteFor(DO domainObject);

}

package org.estatio.module.capex.dom.state;

import java.util.Collections;
import java.util.List;

import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.capex.dom.task.Task;
import org.estatio.module.party.dom.role.IPartyRoleType;

/**
 * Define the mechanism to determine which role (if anyone) to assign a {@link Task} associated with a particular
 * {@link StateTransition}.
 *
 * @param <DO>
 * @param <ST>
 * @param <STT>
 * @param <S>
 */
public interface TaskAssignmentStrategy<
        DO,
        ST extends StateTransition<DO, ST, STT, S>,
        STT extends StateTransitionType<DO, ST, STT, S>,
        S extends State<S>
        >  {

    List<IPartyRoleType> getAssignTo(
            final DO domainObject,
            final ServiceRegistry2 serviceRegistry2);



    public static <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
            > TaskAssignmentStrategy<DO,ST,STT,S> none() {
        return (domainObject, serviceRegistry2) -> Collections.emptyList();
    }

    public static <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
            > TaskAssignmentStrategy<DO,ST,STT,S> to(final IPartyRoleType roleType) {
        return (domainObject, serviceRegistry2) -> Collections.singletonList(roleType);
    }



}

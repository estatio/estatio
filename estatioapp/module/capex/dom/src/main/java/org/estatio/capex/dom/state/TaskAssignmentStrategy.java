package org.estatio.capex.dom.state;

import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.capex.dom.task.Task;
import org.estatio.dom.party.role.IPartyRoleType;

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

    IPartyRoleType getAssignTo(
            final DO domainObject,
            final STT pendingTransitionType,
            final ServiceRegistry2 serviceRegistry2);



    class Util {
        private Util() {}
        public static <
                DO,
                ST extends StateTransition<DO, ST, STT, S>,
                STT extends StateTransitionType<DO, ST, STT, S>,
                S extends State<S>
                > TaskAssignmentStrategy<DO,ST,STT,S> none() {
            return (domainObject, completedTransitionType, serviceRegistry2) -> null;
        }
        public static <
                DO,
                ST extends StateTransition<DO, ST, STT, S>,
                STT extends StateTransitionType<DO, ST, STT, S>,
                S extends State<S>
                > TaskAssignmentStrategy<DO,ST,STT,S> to(final IPartyRoleType roleType) {
            return (domainObject, completedTransitionType, serviceRegistry2) -> roleType;
        }
    }

}

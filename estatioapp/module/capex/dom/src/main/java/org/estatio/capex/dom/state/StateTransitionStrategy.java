package org.estatio.capex.dom.state;

import org.apache.isis.applib.services.registry.ServiceRegistry2;

/**
 * Define the mechanism to determine which transition (if any) to automatically create next after a particular
 * transition has been completed.
 *
 * <p>
 *     Typically this is used in conjunction with transitions that have tasks associated with them.
 * </p>
 *
 * @param <DO>
 * @param <ST>
 * @param <STT>
 * @param <S>
 */
public interface StateTransitionStrategy<
        DO,
        ST extends StateTransition<DO, ST, STT, S>,
        STT extends StateTransitionType<DO, ST, STT, S>,
        S extends State<S>
        >  {

    STT nextTransitionType(
            final DO domainObject,
            final STT completedTransitionType,
            final ServiceRegistry2 serviceRegistry2);


    public static class Util {
        private Util() {}
        public static <
                DO,
                ST extends StateTransition<DO, ST, STT, S>,
                STT extends StateTransitionType<DO, ST, STT, S>,
                S extends State<S>
                > StateTransitionStrategy<DO,ST,STT,S> none() {
            return (domainObject, completedTransitionType, serviceRegistry2) -> null;
        }
        public static <
                DO,
                ST extends StateTransition<DO, ST, STT, S>,
                STT extends StateTransitionType<DO, ST, STT, S>,
                S extends State<S>
                > StateTransitionStrategy<DO,ST,STT,S> next() {

            return (domainObject, requiredTransitionType, serviceRegistry2) -> {

                final StateTransitionService stateTransitionService = serviceRegistry2
                        .lookupService(StateTransitionService.class);

                final S currentState = stateTransitionService.currentStateOf(domainObject, requiredTransitionType);
                final STT[] allTransitionsTypes = stateTransitionService.supportFor(requiredTransitionType)
                        .allTransitionTypes();
                for (STT candidateNextTransitionType : allTransitionsTypes) {

                    if (!stateTransitionService
                            .canTriggerFromState(domainObject, candidateNextTransitionType, currentState)) {
                        continue;
                    }
                    return candidateNextTransitionType;
                }
                return null;
            };

        }
    }

}

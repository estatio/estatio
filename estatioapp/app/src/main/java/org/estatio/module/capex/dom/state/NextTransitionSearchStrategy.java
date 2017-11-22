package org.estatio.module.capex.dom.state;

import java.util.Arrays;
import java.util.List;

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
public interface NextTransitionSearchStrategy<
        DO,
        ST extends StateTransition<DO, ST, STT, S>,
        STT extends StateTransitionType<DO, ST, STT, S>,
        S extends State<S>
        >  {

    STT nextTransitionType(
            final DO domainObject,
            final STT completedTransitionType,
            final ServiceRegistry2 serviceRegistry2);


    public static <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
            > NextTransitionSearchStrategy<DO,ST,STT,S> none() {
        return (domainObject, completedTransitionType, serviceRegistry2) -> null;
    }

    public static <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
            > NextTransitionSearchStrategy<DO,ST,STT,S> firstMatching() {

        return firstMatchingExcluding( /* none */);
    }

    public static <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
            > NextTransitionSearchStrategy<DO,ST,STT,S> firstMatchingExcluding(STT... excluding) {

        final List<STT> excludingList = Arrays.asList(excluding);

        return (domainObject, completedTransitionType, serviceRegistry2) -> {

            final StateTransitionService stateTransitionService = serviceRegistry2
                    .lookupService(StateTransitionService.class);

            final STT[] allTransitionsTypes =
                    stateTransitionService.supportFor(completedTransitionType).allTransitionTypes();
            for (STT candidateNextTransitionType : allTransitionsTypes) {
                if(excludingList.contains(candidateNextTransitionType)) {
                    continue;
                }
                if (candidateNextTransitionType.canTransitionFromStateAndIsMatch(
                        domainObject, completedTransitionType.getToState(), serviceRegistry2)) {
                    return candidateNextTransitionType;
                }
            }
            return null;
        };
    }

    public static <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
            > NextTransitionSearchStrategy<DO,ST,STT,S> exactly(STT next) {
        return (domainObject, completedTransitionType, serviceRegistry2) -> next;
    }

}

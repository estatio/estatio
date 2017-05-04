package org.estatio.capex.dom.state;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.eventbus.EventBusService;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.capex.dom.task.Task;

@DomainService(nature = NatureOfService.DOMAIN)
public class StateTransitionService {

    /**
     * Finds the current (incomplete) {@link StateTransition} (if any) for the supplied domain object and transition type.
     * From this a corresponding {@link Task} may be found.
     *
     * <p>
     *     It is not guaranteed that there will necessarily <i>be</i> such a {@link StateTransition}; we don't mandate
     *     that every
     * </p>
     *
     * <p>
     *     REVIEW: what if there are - somehow - more than one such found?  For now, uses #uniqueMatch so will fail fast; is this appropriate???
     * </p>
     */
    @Programmatic
    public <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
            >
    ST findIncomplete(
            final DO domainObject,
            final STT transitionType) {
        StateTransitionServiceSupport<DO, ST, STT, S> support = supportFor(transitionType);
        return support.findIncomplete(domainObject, transitionType);
    }

    @Programmatic
    public <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
            >
    ST findFor(final Task task) {
        return queryResultsCache.execute(
                () -> doFindFor(task),
                StateTransitionService.class,
                "find", task);
    }

    private <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
    > ST doFindFor(final Task task) {
        StateTransitionServiceSupport support = supportFor(task.getTransitionObjectType());
        return (ST) support.findFor(task);
    }

    /**
     * Whether the domain object can make the suggested transition.
     */
    @Programmatic
    public <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
    > boolean canApply(
            final DO domainObject,
            final STT candidateTransitionType) {

        final S fromState = candidateTransitionType.currentStateOf(domainObject);
        return canTransitionFrom(fromState, candidateTransitionType) &&
                candidateTransitionType.canApply(domainObject, serviceRegistry2);
    }

    /**
     * Apply the transition to the domain object and, if supported, create a {@link Task} for the <i>next</i> transition after that
     *
     * @param domainObject - the domain object whose
     * @param transitionType - the type of transition being applied
     * @param comment
     */
    @Programmatic
    public <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
    > ST apply(
            final DO domainObject,
            final STT transitionType,
            final String comment) {

        final ST incompleteTransition = findIncomplete(domainObject, transitionType);
        if(incompleteTransition == null) {
            return null;
        }

        return apply(incompleteTransition, comment);
    }

    /**
     *
     * @param stateTransition - expected to be incomplete, otherwise is a no-op
     * @param comment
     * @return - the next state transition, or null if the provided one could not be applied.
     */
    public <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
    > ST apply(
            final ST stateTransition,
            final String comment) {

        final DO domainObject = stateTransition.getDomainObject();
        final STT transitionType = stateTransition.getTransitionType();

        S currentState = transitionType.currentStateOf(domainObject);

        if(!canTransitionFrom(currentState, transitionType)) {
            // cannot apply this state
            return null;
        }


        final EventBusService eventBusService = serviceRegistry2.lookupService(EventBusService.class);

        final StateTransitionEvent<DO, ST, STT, S> event = new StateTransitionEvent<>(stateTransition);

        // transitioning
        event.setPhase(StateTransitionEvent.Phase.TRANSITIONING);
        eventBusService.post(event);

        // transition
        transitionType.applyTo(domainObject, serviceRegistry2);
        stateTransition.getTask().completed(comment);

        // for wherever we might go next, we spin through all possible transitions,
        // and create a task for the first one that applies to this particular domain object.
        ST nextTransition = null;
        final STT[] allTransitionsTypes = supportFor(transitionType).allTransitionTypes();
        for (STT candidateNextTransitionType : allTransitionsTypes) {
            if (candidateNextTransitionType.assignTaskTo(serviceRegistry2) == null) {
                continue;
            }
            if (!canApply(domainObject, candidateNextTransitionType)) {
                continue;
            }
            nextTransition = candidateNextTransitionType.createTransition(domainObject, serviceRegistry2);
            if (nextTransition != null) {
                break;
            }
        }

        event.setPhase(StateTransitionEvent.Phase.TRANSITIONED);
        eventBusService.post(event);

        return nextTransition;
    }

    // ////////////////////////////////////

    private <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
            >
    StateTransitionServiceSupport<DO,ST,STT,S> supportFor(final STT transitionType) {
        if(supportServices == null) {
            throw new IllegalArgumentException("No implementations of StateTransitionServiceSupport found");
        }
        for (final StateTransitionServiceSupport support : supportServices) {
            if(support.supports(transitionType)) {
                return support;
            }
        }
        throw new IllegalArgumentException("No implementations of StateTransitionServiceSupport found for " + transitionType);
    }


    private <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
            >
    StateTransitionServiceSupport<DO,ST,STT,S> supportFor(final String transitionType) {
        if(supportServices == null) {
            throw new IllegalArgumentException("No implementations of StateTransitionServiceSupport found");
        }
        for (final StateTransitionServiceSupport supportService : supportServices) {
            if(supportService.supports(transitionType)) {
                return supportService;
            }
        }
        throw new IllegalArgumentException("No implementations of StateTransitionServiceSupport found for " + transitionType);
    }

    private static <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
            > boolean canTransitionFrom(
            final S fromState, final STT transitionType) {
        final List<S> fromStates = transitionType.getFromStates();
        return fromStates == null || fromStates.contains(fromState);
    }


    @Inject
    List<StateTransitionServiceSupport> supportServices;

    @Inject
    ServiceRegistry2 serviceRegistry2;

    @Inject
    QueryResultsCache queryResultsCache;

}

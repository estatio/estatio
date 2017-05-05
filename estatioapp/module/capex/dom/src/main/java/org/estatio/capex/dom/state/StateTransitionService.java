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
            STT extends StateTransitionChart<DO, ST, STT, S>,
            S extends State<S>
            >
    ST findIncomplete(
            final DO domainObject,
            final STT transitionType) {
        StateTransitionServiceSupport<DO, ST, STT, S> supportService = supportFor(transitionType);
        return supportService.findIncomplete(domainObject);
    }

    @Programmatic
    public <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionChart<DO, ST, STT, S>,
            S extends State<S>
            >
    ST findFor(final Task task) {
        return queryResultsCache.execute(
                () -> doFindFor(task),
                StateTransitionService.class,
                "find", task);
    }

    /**
     * factored out so can be cached.
     */
    private <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionChart<DO, ST, STT, S>,
            S extends State<S>
            > ST doFindFor(final Task task) {
        StateTransitionServiceSupport supportService = supportFor(task.getTransitionObjectType());
        return (ST) supportService.findFor(task);
    }

    /**
     * Whether the domain object can make the suggested transition.
     */
    @Programmatic
    public <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionChart<DO, ST, STT, S>,
            S extends State<S>
    > boolean canApply(
            final DO domainObject,
            final STT candidateTransitionType) {

        final S currentStateIfAny = currentStateOf(domainObject, candidateTransitionType);
        return canApplyFromState(domainObject, candidateTransitionType, currentStateIfAny);
    }

    private <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionChart<DO, ST, STT, S>,
            S extends State<S>
    >  boolean canApplyFromState(
            final DO domainObject,
            final STT candidateTransitionType,
            final S currentStateIfAny) {
        return canTransitionFrom(currentStateIfAny, candidateTransitionType) &&
               candidateTransitionType.canApply(domainObject, serviceRegistry2);
    }

    /**
     * Obtain the current state of the domain object, which will be the
     * {@link StateTransition#getFromState() from state} of the {@link StateTransition} not yet completed (ie with a
     * {@link StateTransition#getToState() to state} is null.
     *
     * <p>
     * If there is no {@link StateTransition}, then should default to null (indicating that the domain object has only just been instantiated).
     * </p>
     *
     * @param domainObject
     * @param prototype - to specify which {@link StateTransitionChart transition type} we are interested in.
     */
    @Programmatic
    public <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionChart<DO, ST, STT, S>,
            S extends State<S>
    >  S currentStateOf(
            final DO domainObject,
            final STT prototype) {
        final StateTransitionServiceSupport<DO, ST, STT, S> supportService = supportFor(prototype);
        return supportService.currentStateOf(domainObject);
    }

    /**
     * Obtain the current transition of the domain object, which will be the
     * {@link StateTransition#getFromState() from state} of the {@link StateTransition} not yet completed (ie with a
     * {@link StateTransition#getToState() to state} is null.
     *
     * @param domainObject
     * @return the current transition, or possibly null for the very first (INSTANTIATE) transition
     */
    @Programmatic
    public <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionChart<DO, ST, STT, S>,
            S extends State<S>
    > ST currentTransitionOf(
            final DO domainObject,
            final STT prototype) {
        final StateTransitionServiceSupport<DO, ST, STT, S> supportService = supportFor(prototype);
        return supportService.currentTransitionOf(domainObject);
    }

    /**
     * Applies the state transition to the domain object that it refers to, marks the transition as
     * {@link StateTransition#getCompletedOn() complete} and for {@link StateTransition#getTask() corresponding}
     * {@link Task} (if there is one), also marks it as {@link Task#getCompletedBy()} complete}.
     * If there are further available {@link StateTransition}s, then one is created (again, with a corresponding
     * {@link Task} if required).
     *
     * <p>
     *     If the state transition does not apply to the current state of the referred domain object, or
     *     if the state transition's corresponding task is already complete, then does nothing and returns null.
     * </p>
     *
     * @param stateTransition - expected to for a task still incomplete, and applicable to its domain object's state
     * @param comment - used to complete the task.
     *
     * @return - the next state transition, or null if the one provided one
     */
    public <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionChart<DO, ST, STT, S>,
            S extends State<S>
            > ST apply(
            final ST stateTransition,
            final String comment) {

        if(stateTransition.getTask().isCompleted()) {
            return null;
        }

        final DO domainObject = stateTransition.getDomainObject();
        final STT transitionType = stateTransition.getTransitionType();
        return apply(domainObject, transitionType, comment);
    }

    /**
     * Apply the transition to the domain object and, if supported, create a {@link Task} for the <i>next</i> transition after that
     *
     * @param domainObject - the domain object whose
     * @param transitionType - the type of transition being applied (but can be null for very first time)
     * @param comment
     */
    @Programmatic
    public <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionChart<DO, ST, STT, S>,
            S extends State<S>
    > ST apply(
            final DO domainObject,
            final STT transitionType,
            final String comment) {

        S currentState = currentStateOf(domainObject, transitionType);
        if(!canTransitionFrom(currentState, transitionType)) {
            // cannot apply this state
            return null;
        }

        final ST incompleteTransitionIfAny = findIncomplete(domainObject, transitionType);

        final EventBusService eventBusService = serviceRegistry2.lookupService(EventBusService.class);
        final StateTransitionEvent<DO, ST, STT, S> event = new StateTransitionEvent<>(domainObject, transitionType,
                incompleteTransitionIfAny);

        // transitioning
        event.setPhase(StateTransitionEvent.Phase.TRANSITIONING);
        eventBusService.post(event);

        // transition
        transitionType.applyTo(domainObject, serviceRegistry2);

        if(incompleteTransitionIfAny != null) {
            incompleteTransitionIfAny.completed();
            final Task taskIfAny = incompleteTransitionIfAny.getTask();
            if(taskIfAny != null) {
                taskIfAny.completed(comment);
            }
        }

        // update the state of the domain object to be toState of the transition that's just completed
        currentState = transitionType.getToState();

        // for wherever we might go next, we spin through all possible transitions,
        // and create a task for the first one that applies to this particular domain object.
        ST nextTransition = null;
        final STT[] allTransitionsTypes = supportFor(transitionType).allTransitionTypes();
        for (STT candidateNextTransitionType : allTransitionsTypes) {

            if (!canApplyFromState(domainObject, candidateNextTransitionType, currentState)) {
                continue;
            }
            nextTransition = candidateNextTransitionType.createTransition(domainObject, currentState, serviceRegistry2);
            if (nextTransition != null) {
                break;
            }
        }

        event.setPhase(StateTransitionEvent.Phase.TRANSITIONED);
        eventBusService.post(event);

        return nextTransition;
    }


    // ////////////////////////////////////

    // REVIEW: we could cache the result, perhaps (it's idempotent)
    private <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionChart<DO, ST, STT, S>,
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

    // REVIEW: we could cache the result, perhaps (it's idempotent)
    private <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionChart<DO, ST, STT, S>,
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
            STT extends StateTransitionChart<DO, ST, STT, S>,
            S extends State<S>
            > boolean canTransitionFrom(
                final S fromState,
                final STT transitionType) {
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

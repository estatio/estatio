package org.estatio.capex.dom.state;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.eventbus.EventBusService;
import org.apache.isis.applib.services.metamodel.MetaModelService3;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.capex.dom.task.Task;
import org.estatio.dom.roles.EstatioRole;

@DomainService(nature = NatureOfService.DOMAIN)
public class StateTransitionService {

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

    /**
     * factored out so can be cached.
     */
    private <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
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
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
    > boolean canTrigger(
            final DO domainObject,
            final STT candidateTransitionType) {

        final S currentStateIfAny = currentStateOf(domainObject, candidateTransitionType);
        return canTriggerFromState(domainObject, candidateTransitionType, currentStateIfAny);
    }

    @Programmatic
    public <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
    >  boolean canTriggerFromState(
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
     * @param prototype - to specify which {@link StateTransitionType transition type} we are interested in.
     */
    @Programmatic
    public <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
    >  S currentStateOf(
            final DO domainObject,
            final STT prototype) {
        final StateTransitionServiceSupport<DO, ST, STT, S> supportService = supportFor(prototype);
        return supportService.currentStateOf(domainObject);
    }

    /**
     * Overload of {@link #currentStateOf(Object, StateTransitionType)}, but using the class of the
     * {@link StateTransition} rather than a prototype value of the {@link StateTransitionType}.
     */
    @Programmatic
    public <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
    >  S currentStateOf(
            final DO domainObject,
            final Class<ST> stateTransitionClass) {
        final StateTransitionServiceSupport<DO, ST, STT, S> supportService = supportFor(stateTransitionClass);
        return supportService.currentStateOf(domainObject);
    }


    /**
     * Obtain the pending (incomplete) transition of the domain object, which will be the
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
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
    > ST pendingTransitionOf(
            final DO domainObject,
            final STT prototype) {
        final StateTransitionServiceSupport<DO, ST, STT, S> supportService = supportFor(prototype);
        return supportService.pendingTransitionOf(domainObject);
    }

    /**
     * Overload of {@link #pendingTransitionOf(Object, StateTransitionType)}, but using the class of the
     * {@link StateTransition} rather than a prototype value of the {@link StateTransitionType}.
     */
    @Programmatic
    public <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
            > ST pendingTransitionOf(
            final DO domainObject,
            final Class<ST> stateTransitionClass) {
        final StateTransitionServiceSupport<DO, ST, STT, S> supportService = supportFor(stateTransitionClass);
        return supportService.pendingTransitionOf(domainObject);
    }

    /**
     * Obtain the most recently completed transition of the domain object, which will be the
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
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
    > ST mostRecentlyCompletedTransitionOf(
            final DO domainObject,
            final STT prototype) {
        final StateTransitionServiceSupport<DO, ST, STT, S> supportService = supportFor(prototype);
        return supportService.mostRecentlyCompletedTransitionOf(domainObject);
    }

    /**
     * Overload of {@link #mostRecentlyCompletedTransitionOf(Object, StateTransitionType)}, but using the class of the
     * {@link StateTransition} rather than a prototype value of the {@link StateTransitionType}.
     */
    @Programmatic
    public <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
    > ST mostRecentlyCompletedTransitionOf(
            final DO domainObject,
            final Class<ST> stateTransitionClass) {
        final StateTransitionServiceSupport<DO, ST, STT, S> supportService = supportFor(stateTransitionClass);
        return supportService.mostRecentlyCompletedTransitionOf(domainObject);
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
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
            > ST trigger(
            final ST stateTransition,
            final String comment) {

        if(stateTransition.getTask().isCompleted()) {
            return null;
        }

        final DO domainObject = stateTransition.getDomainObject();
        final STT transitionType = stateTransition.getTransitionType();
        return trigger(domainObject, transitionType, comment);
    }

    /**
     * Apply the transition to the domain object and, if supported, create a {@link Task} for the <i>next</i> transition after that
     *
     * @param domainObject - the domain object whose
     * @param requiredTransitionType - the type of transition being applied (but can be null for very first time)
     * @param comment
     */
    @Programmatic
    public <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
    > ST trigger(
            final DO domainObject,
            final STT requiredTransitionType,
            final String comment) {

        S currentState = currentStateOf(domainObject, requiredTransitionType);
        if(!canTransitionFrom(currentState, requiredTransitionType)) {
            // cannot apply this state
            return null;
        }

        ST pendingTransition = pendingTransitionOf(domainObject, requiredTransitionType);

        if(pendingTransition != null) {
            final STT pendingType = pendingTransition.getTransitionType();
            if(pendingType != requiredTransitionType) {
                // we're heading in a different direction than was set up
                // eg we're doing a cancel instead of an approve.

                final Task taskIfAny = pendingTransition.getTask();

                // ... remove the pending, and its task too (if it has one)
                repositoryService.remove(pendingTransition);
                if(taskIfAny != null) {
                    repositoryService.removeAndFlush(taskIfAny);
                }

                pendingTransition = null;
            }
        }

        // there may be no pending transition if either:
        // (a) the most recently completed transition did not define a "next transition" (via StateTransitionType#getTransitionStrategy)
        // (b) there was a pending transition, but was heading in the different direction and was just deleted (above)
        if(pendingTransition == null) {

            final TaskAssignmentStrategy taskAssignmentStrategy = requiredTransitionType.getTaskAssignmentStrategy();
            EstatioRole assignToIfAny = null;
            if (taskAssignmentStrategy != null) {
                assignToIfAny = taskAssignmentStrategy.getAssignTo(domainObject, requiredTransitionType, serviceRegistry2);
            }

            pendingTransition = requiredTransitionType.createTransition(domainObject, currentState,
                    assignToIfAny, serviceRegistry2);

        }

        final StateTransitionEvent<DO, ST, STT, S> event =
                requiredTransitionType.newStateTransitionEvent(domainObject, pendingTransition);

        // transitioning
        final EventBusService eventBusService = serviceRegistry2.lookupService(EventBusService.class);
        event.setPhase(StateTransitionEvent.Phase.TRANSITIONING);
        eventBusService.post(event);

        // transition
        requiredTransitionType.applyTo(domainObject, serviceRegistry2);

        if(pendingTransition != null) {
            pendingTransition.completed();
            final Task taskIfAny = pendingTransition.getTask();
            if(taskIfAny != null) {
                taskIfAny.completed(comment);
            }
        }

        // update the state of the domain object to be toState of the transition that's just completed
        currentState = requiredTransitionType.getToState();

        // for wherever we might go next, we spin through all possible transitions,
        // and create a task for the first one that applies to this particular domain object.
        ST nextTransition = null;

        final StateTransitionStrategy<DO, ST, STT, S> transitionStrategy =
                requiredTransitionType.getTransitionStrategy();
        if(transitionStrategy != null) {
            STT nextTransitionType = transitionStrategy.nextTransitionType(domainObject, requiredTransitionType, serviceRegistry2);
            if(nextTransitionType != null) {
                final TaskAssignmentStrategy<DO, ST, STT, S> taskAssignmentStrategy =
                        nextTransitionType.getTaskAssignmentStrategy();
                EstatioRole assignToIfAny = null;
                if(taskAssignmentStrategy != null) {
                    assignToIfAny = taskAssignmentStrategy
                            .getAssignTo(domainObject, nextTransitionType, serviceRegistry2);
                }
                nextTransition = nextTransitionType.createTransition(domainObject, currentState,  assignToIfAny, serviceRegistry2);
            }
        }

        event.setPhase(StateTransitionEvent.Phase.TRANSITIONED);
        eventBusService.post(event);


        return nextTransition;
    }


    // ////////////////////////////////////

    // REVIEW: we could cache the result, perhaps (it's idempotent)
    @Programmatic
    public <
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

    // REVIEW: we could cache the result, perhaps (it's idempotent)
    @Programmatic
    public <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
            >
    StateTransitionServiceSupport<DO,ST,STT,S> supportFor(final Class<ST> stateTransitionClass) {
        final String transitionType = metaModelService3.toObjectType(stateTransitionClass);
        return supportFor(transitionType);
    }

    // REVIEW: we could cache the result, perhaps (it's idempotent)
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

    @Inject
    RepositoryService repositoryService;

    @Inject
    MetaModelService3 metaModelService3;


}

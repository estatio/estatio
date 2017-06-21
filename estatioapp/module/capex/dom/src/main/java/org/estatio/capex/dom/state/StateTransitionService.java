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
import org.estatio.dom.party.role.IPartyRoleType;

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
    > boolean canTransitionAndMatchAndGuardSatisfied(
            final DO domainObject,
            final STT candidateTransitionType) {

        return candidateTransitionType.canTransitionAndMatchAndGuardSatisfied(domainObject, serviceRegistry2);
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
     * @return - the next state transition, or null if there isn't one defined by the transition just completing/ed
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
     *  @param domainObject - the domain object whose
     * @param stateTransitionClass - identifies the state chart being applied
     * @param requiredTransitionType
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
            final Class<ST> stateTransitionClass,
            final STT requiredTransitionType,
            final String comment) {

        ST completedTransition = completeTransitionIfPossible(domainObject, stateTransitionClass, requiredTransitionType, comment);

        boolean keepTransitioning = (completedTransition != null);
        while(keepTransitioning) {
            ST previousTransition = completedTransition;
            completedTransition = completeTransitionIfPossible(domainObject, stateTransitionClass, null, null);
            keepTransitioning = (completedTransition != null && previousTransition != completedTransition);
        }

        return mostRecentlyCompletedTransitionOf(domainObject, stateTransitionClass);
    }

    private <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
    > ST completeTransitionIfPossible(
            final DO domainObject,
            final Class<ST> stateTransitionClass,
            final STT requestedTransitionTypeIfAny,
            final String comment) {

        // check the override, if any
        if(requestedTransitionTypeIfAny != null) {
            boolean canTransition = requestedTransitionTypeIfAny.canTransitionAndIsMatch(domainObject, serviceRegistry2);
            if(!canTransition) {
                // what's been requested is a no-go.
                return null;
            }
        }

        // determine what previously was determined as the pending (if any)
        ST pendingTransitionIfAny = pendingTransitionOf(domainObject, stateTransitionClass);

        // what we now think as the pending (if any)
        STT nextTransitionType = null;

        // current state
        final ST mostRecentTransitionIfAny = mostRecentlyCompletedTransitionOf(domainObject, stateTransitionClass);
        final S currentStateIfAny =
                mostRecentTransitionIfAny != null
                        ? mostRecentTransitionIfAny.getTransitionType().getToState()
                        : null;

        if (requestedTransitionTypeIfAny != null) {
            nextTransitionType = requestedTransitionTypeIfAny;
        } else {
            if (mostRecentTransitionIfAny != null) {

                // use most recent transition to determine the next transition (since one hasn't been requested)
                final STT mostRecentTransitionType = mostRecentTransitionIfAny.getTransitionType();

                final StateTransitionStrategy<DO, ST, STT, S> transitionStrategy =
                        mostRecentTransitionType.getTransitionStrategy();
                if(transitionStrategy != null) {
                    nextTransitionType =
                            transitionStrategy.nextTransitionType(domainObject, mostRecentTransitionType, serviceRegistry2);
                }

            } else {
                // can't proceed because unable to determine current state, and no transition specified
                return null;
            }
        }

        // if pending has changed, then reconcile
        STT pendingTransitionType = pendingTransitionIfAny != null ? pendingTransitionIfAny.getTransitionType() : null;

        if(pendingTransitionType != nextTransitionType) {
            if(pendingTransitionType != null) {

                // for both nextTransitionType == null and != null

                final Task taskIfAny = pendingTransitionIfAny.getTask();
                repositoryService.remove(pendingTransitionIfAny);
                if(taskIfAny != null) {
                    repositoryService.removeAndFlush(taskIfAny);
                }
                pendingTransitionType = nextTransitionType;
                pendingTransitionIfAny  = createPendingTransition(domainObject, currentStateIfAny, nextTransitionType);


            } else {
                // pendingTransitionType == null, so nextTransitionType != null because of outer if

                pendingTransitionIfAny  = createPendingTransition(domainObject, currentStateIfAny, nextTransitionType);
                pendingTransitionType = nextTransitionType;
            }
        }

        if(pendingTransitionType == null) {
            return null;
        }

        if(! pendingTransitionType.isGuardSatisified(domainObject, serviceRegistry2) ) {
            // cannot apply this state, while there is an available "road" to traverse, it is blocked
            // (there must be a guard prohibiting it for this particular domain object)
            return null;
        }

        if(nextTransitionType.advancePolicyFor(domainObject, serviceRegistry2).isManual() &&
           requestedTransitionTypeIfAny == null) {
            // do not proceed if this is an explicit transition and no explicit transition type provided.
            return null;
        }

        //
        // guard satisfied, so go ahead and complete this pending transition
        //
        final ST completedTransition = completeTransition(domainObject, pendingTransitionIfAny, comment);
        return completedTransition;
    }

    private <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
        > ST completeTransition(
            final DO domainObject,
            final ST transitionToComplete,
            final String comment) {

        final STT transitionType = transitionToComplete.getTransitionType();

        final StateTransitionEvent<DO, ST, STT, S> event =
                transitionType.newStateTransitionEvent(domainObject, transitionToComplete);

        // transitioning
        event.setPhase(StateTransitionEvent.Phase.TRANSITIONING);
        eventBusService.post(event);

        // transition
        transitionType.applyTo(domainObject, serviceRegistry2);

        // mark tasks as complete
        transitionToComplete.completed();
        final Task taskIfAny = transitionToComplete.getTask();
        if(taskIfAny != null) {
            taskIfAny.completed(comment);
        }

        event.setPhase(StateTransitionEvent.Phase.TRANSITIONED);
        eventBusService.post(event);

        return transitionToComplete;
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
        final Class<ST> stateTransitionClass = transitionClassFor(requiredTransitionType);
        return trigger(domainObject, stateTransitionClass, requiredTransitionType, comment);
    }

    private <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
    > Class<ST> transitionClassFor(final STT requiredTransitionType) {
        for (StateTransitionServiceSupport supportService : supportServices) {
            Class<ST> transitionClass = supportService.transitionClassFor(requiredTransitionType);
            if(transitionClass != null) {
                return transitionClass;
            }
        }
        return null;
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
    > ST triggerOLD(
            final DO domainObject,
            final STT requiredTransitionType,
            final String comment) {

        if(!requiredTransitionType.canTransitionAndIsMatch(domainObject, serviceRegistry2)) {
            // cannot apply this state, there is no "road" or isn't a match (as a valid route to traverse, switch stmt)
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

            final S currentState = currentStateOf(domainObject, requiredTransitionType);

            final TaskAssignmentStrategy taskAssignmentStrategy = requiredTransitionType.getTaskAssignmentStrategy();
            IPartyRoleType assignToIfAny = null;
            if (taskAssignmentStrategy != null) {
                assignToIfAny = taskAssignmentStrategy.getAssignTo(domainObject, requiredTransitionType, serviceRegistry2);
            }

            pendingTransition = requiredTransitionType.createTransition(
                                        domainObject, currentState, assignToIfAny, serviceRegistry2);
        }


        //
        // by this stage there will be a pending transition for the required transition type, and we know that
        // it is valid (the very first check we make)
        //
        // however, if there's a guard which isn't yet satisfied, then we quit here.
        //
        if(! requiredTransitionType.isGuardSatisified(domainObject, serviceRegistry2) ) {
            // cannot apply this state, while there is an available "road" to traverse, it is blocked
            // (there must be a guard prohibiting it for this particular domain object)
            return null;
        }


        final StateTransitionEvent<DO, ST, STT, S> event =
                requiredTransitionType.newStateTransitionEvent(domainObject, pendingTransition);

        // transitioning
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


        // for wherever we might go next, we use the transition strategy to create a pending task,
        // and (if necessary) create a task for the first one that applies to this particular domain object.
        ST nextTransition = createPendingTransitionAfter(domainObject, requiredTransitionType);

        event.setPhase(StateTransitionEvent.Phase.TRANSITIONED);
        eventBusService.post(event);


        // do note that it's possible that the domain object may have transitioned beyond this state,
        // eg if there were any subscribers to the transition events that automatically advanced
        // through to next state.
        //
        // we therefore do NOT return the nextTransition, but instead look up the transition
        // on behalf of our caller
        return mostRecentlyCompletedTransitionOf(domainObject, requiredTransitionType);
    }

    @Programmatic
    public <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
            >
    ST checkState(
            final DO domainObject,
            final Class<ST> stateTransitionClass) {

        final ST mostRecentTransition = mostRecentlyCompletedTransitionOf(domainObject, stateTransitionClass);
        final STT mostRecentTransitionType = mostRecentTransition.getTransitionType();

        final StateTransitionEvent<DO, ST, STT, S> event =
                mostRecentTransitionType.newStateTransitionEvent(domainObject, mostRecentTransition);

        ST pendingTransition = createPendingTransitionAfter(domainObject, mostRecentTransitionType);

        // force re-evaluation
        event.setPhase(StateTransitionEvent.Phase.TRANSITIONED);
        eventBusService.post(event);

        return pendingTransition;
    }


    @Programmatic
    public <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
            >
    ST createPendingTransitionIfGuardSatisfied(
            final DO domainObject,
            final STT transitionType) {
        if(!canTransitionAndMatchAndGuardSatisfied(domainObject, transitionType)) {
            return null;
        }
        final S currentState = currentStateOf(domainObject, transitionType);
        return createPendingTransition(domainObject, currentState, transitionType);
    }

    private <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
    > ST createPendingTransitionAfter(
            final DO domainObject,
            final STT transitionType) {
        ST nextTransition = null;
        S currentState = transitionType.getToState();
        final StateTransitionStrategy<DO, ST, STT, S> transitionStrategy = transitionType.getTransitionStrategy();
        if(transitionStrategy != null) {
            STT nextTransitionType = transitionStrategy.nextTransitionType(domainObject, transitionType, serviceRegistry2);
            if(nextTransitionType != null) {
                nextTransition = createPendingTransition(domainObject, currentState, nextTransitionType);
            }
        }
        return nextTransition;
    }

    private <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
            >
    ST createPendingTransition(
            final DO domainObject,
            final S currentState,
            final STT transitionType) {

        final TaskAssignmentStrategy<DO, ST, STT, S> taskAssignmentStrategy =
                transitionType.getTaskAssignmentStrategy();
        IPartyRoleType assignToIfAny = null;
        if(taskAssignmentStrategy != null) {
            assignToIfAny = taskAssignmentStrategy
                    .getAssignTo(domainObject, transitionType, serviceRegistry2);
        }
        return transitionType
                .createTransition(domainObject, currentState, assignToIfAny, serviceRegistry2);
    }


    // ////////////////////////////////////

    // REVIEW: we could cache the result, perhaps (it's idempotent)
    @Programmatic
    <
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
    private <
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

    @Inject
    EventBusService eventBusService;

}

package org.estatio.capex.dom.state;

import java.util.List;

import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.util.Enums;

import org.estatio.capex.dom.task.Task;
import org.estatio.dom.party.Person;
import org.estatio.dom.party.role.IPartyRoleType;
import org.estatio.dom.roles.EstatioRole;

/**
 * An identified sequence of {@link State} transitions.
 *
 * <p>
 * Intended to be implemented by an enum; for this reason an instance of {@link ServiceRegistry2} is passed into
 * most methods (so that the implementation can lookup domain services etc to do its work).
 * </p>
 */
public interface StateTransitionType<
        DO,
        ST extends StateTransition<DO, ST, STT, S>,
        STT extends StateTransitionType<DO, ST, STT, S>,
        S extends State<S>
        > {

    /**
     * Potential {@link State}s from which a {@link StateTransition transition} of this type can actually occur.
     */
    @Programmatic
    List<S> getFromStates();

    /**
     * The resultant {@link State} of the domain object once a {@link StateTransition transition} of this type has occurred.
     */
    @Programmatic
    S getToState();


    /**
     * How the {@link StateTransitionService} should search to create further pending transitions.
     *
     * Otherwise, the "to state" of the transition is considered to be an "end-point".
     * @return true
     */
    @Programmatic
    NextTransitionSearchStrategy<DO, ST, STT, S> getNextTransitionSearchStrategy();

    /**
     * Returns the next transition type after this one.
     *
     * <p>
     *     Note that this method does <i>not</i> double-check the actual current state of the domain object;
     *     this allows it to be used for "what-if" queries;
     *     (as in: "what would the next state be if this one completed?")
     * </p>
     */
    @Programmatic
    default STT nextTransitionType(final DO domainObject, final ServiceRegistry2 serviceRegistry2) {
        STT transitionType = (STT) this;
        return getNextTransitionSearchStrategy().nextTransitionType(
                domainObject, transitionType, serviceRegistry2);
    }

    /**
     * Allows the type to apply changes to the target domain object if necessary.
     *
     * <p>
     *     For implementations that hold the relevant state entirely in the corresponding {@link StateTransition}, this
     *     method will be a no-op.  However, some implementations might want to "push" the current state onto the
     *     domain object (for convenience or as a performance optimisation); in which case this is the place to do it.
     * </p>
     */
    default void applyTo(
            final DO domainObject,
            final Class<ST> stateTransitionClass,
            final ServiceRegistry2 serviceRegistry2) {

        // push the to state into the domain object (if Stateful)
        if(domainObject instanceof Stateful) {
            Stateful stateful = (Stateful) domainObject;
            stateful.setStateOf(stateTransitionClass, this.getToState());
        }
    }

    /**
     * The algorithm to detemine which {@link EstatioRole task role}, if any, to assign to a {@link Task} wrapping this
     * transition.
     */
    @Programmatic
    TaskAssignmentStrategy<DO, ST, STT, S> getTaskAssignmentStrategy();

    @Programmatic
    default IPartyRoleType getAssignTo(DO domainObject, ServiceRegistry2 serviceRegistry2) {
        return getTaskAssignmentStrategy()
                .getAssignTo(domainObject, serviceRegistry2);

    }

    /**
     * Creates a new {@link StateTransition transition}, and optionally {@link Task}, for the domain object.
     *
     * <p>
     *     This method is only intended to be called by {@link StateTransitionService}, which checks that the
     *     domain object is already in the specified fromState, and can otherwise be
     *     {@link #isGuardSatisfied(Object, ServiceRegistry2) applied}.
     * </p>
     */
    @Programmatic
    ST createTransition(
            final DO domainObject,
            final S fromState,
            final IPartyRoleType assignToIfAny,
            final Person personToAssignToIfAny,
            final String taskDescriptionIfAny,
            final ServiceRegistry2 serviceRegistry2);

    /**
     * Creates a (transition-type specific implementation of) {@link StateTransitionEvent} for the
     * {@link StateTransitionService} to emit onto the event bus.
     *
     * <p>
     *     This makes for a better API for potential subscribers - rather than subscribing to the very generic
     *     {@link StateTransitionEvent}, they can subscribe to the particular state transition "chart" that they are
     *     interested in.
     * </p>
     *
     * @param domainObject - upon which the state transition is occurring.
     * @param transitionIfAny - the persistent {@link StateTransition} being completed.  The only time this is null is for the initial "pseudo" transition of the domain object to its initial state.
     * @return
     */
    @Programmatic
    StateTransitionEvent<DO,ST,STT,S> newStateTransitionEvent(DO domainObject, ST transitionIfAny);


    /**
     * Whether this domain object is in a state such that this transition could occur (subject to any additional
     * {@link StateTransitionType#isGuardSatisfied(Object, ServiceRegistry2) guards} also being satisfied).
     */
    @Programmatic
    default <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
    >  boolean canTransitionFromCurrentStateAndIsMatch(
            final DO domainObject,
            final ServiceRegistry2 serviceRegistry2) {

        final STT transitionType = (STT) this;
        final StateTransitionService stateTransitionService = serviceRegistry2.lookupService(StateTransitionService.class);

        final S currentStateIfAny = stateTransitionService.currentStateOf(domainObject, transitionType);

        return canTransitionFromStateAndIsMatch(domainObject, currentStateIfAny, serviceRegistry2);
    }

    /**
     * Whether this domain object could transition from the specified state.
     */
    @Programmatic
    default <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
    >  boolean canTransitionFromStateAndIsMatch(
            final DO domainObject,
            final S startingState,
            final ServiceRegistry2 serviceRegistry2) {
        final STT transitionType = (STT) this;
        return transitionType.canTransition(startingState) &&
                transitionType.isMatch(domainObject, serviceRegistry2);
    }


    /**
     * Whether there is a &quot;road&quot; from the specified state using this transition to some other state.
     *
     * <p>
     *     Note that the transition itself may also have a {@link StateTransitionType#isGuardSatisfied(Object, ServiceRegistry2) guard}
     *     so the &quot;road&quot; may not be immediately traversable if the guard isn't satisfied.
     * </p>
     */
    default <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
        > boolean canTransition(final S fromState) {
        final STT transitionType = (STT) this;
        final List<S> fromStates = transitionType.getFromStates();
        if (fromStates == null) {
            return (fromState == null);
        }
        return fromStates.contains(fromState);
    }

    /**
     * Whether the provided domain object can make <i>this</i> transition at all, based on its (likely immutable) state.
     *
     * <p>
     *     This is similar to {@link #isGuardSatisfied(Object, ServiceRegistry2)}, but is used for initial routing
     *     to set up a pending {@link StateTransition}.  That is, it is expected to be based on state of the domain
     *     object that is unlikely to change over time.  In contrast, {@link #isGuardSatisfied(Object, ServiceRegistry2)}
     *     is continually evaluated to see if the transition can <i>yet</i> be made, and so is based on state that is
     *     mutable.
     * </p>
     *
     * <p>
     *     In practice, this means that this is method is overridden when there is a decision to be made and the
     *     next state to transition to depends upon the state of the domain object
     * </p>
     *
     * @see #isGuardSatisfied(Object, ServiceRegistry2)
     *
     * @param domainObject - being transitioned.
     * @param serviceRegistry2 -to lookup domain services etc
     */
    @Programmatic
    default boolean isMatch(final DO domainObject, final ServiceRegistry2 serviceRegistry2) {
        return true;
    }

    /**
     * Derived from {@link #reasonGuardNotSatisified(Object, ServiceRegistry2)}; do not overrride.
     */
    @Programmatic
    default boolean isGuardSatisfied(final DO domainObject, final ServiceRegistry2 serviceRegistry2) {
        return reasonGuardNotSatisified(domainObject, serviceRegistry2) == null;
    }

    @Programmatic
    AdvancePolicy advancePolicyFor(final DO domainObject, final ServiceRegistry2 serviceRegistry2);

    /**
     * The reason as to why the provided domain object cannot - yet - make <i>this</i> transition.
     *
     * <p>
     *     In practice, this means that this is method is overridden when there is a decision to be made and the
     *     next state to transition to depends upon the state of the domain object
     * </p>
     *
     * @see #isMatch(Object, ServiceRegistry2)
     *
     * @param domainObject - being transitioned.
     * @param serviceRegistry2 -to lookup domain services etc
     */
    /**
     * Kind-a the same as our disableXxx() guards in the framework.
     */
    @Programmatic
    default String reasonGuardNotSatisified(final DO domainObject, final ServiceRegistry2 serviceRegistry2) {
        return null;
    }

    public static class Util {
        public static <
                DO,
                ST extends StateTransition<DO, ST, STT, S> ,
                STT extends Enum & StateTransitionType<DO, ST, STT, S>,
                S extends State<S>
            > String taskDescriptionUsing(
                final String taskDescriptionIfAny,
                final STT transitionType) {
            return Enums.getFriendlyNameOf(transitionType) + (taskDescriptionIfAny != null ? " (" + taskDescriptionIfAny + ")": "");
        }


        }


}

package org.estatio.capex.dom.state;

import java.util.List;

import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.capex.dom.task.Task;
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
     * Whether the provided domain object can make <i>this</i> type of transition.
     *
     * <p>
     *     By default, all transitions are assumed to apply (with respect to their {@link #getFromStates() from} and
     *     {@link #getToState() to} states <i>unless</i> this method is overridden to further constrain whether a
     *     transition applies to a <i>particular</i> domain object.
     * </p>
     *
     * <p>
     *     In practice, this means that this is method is overridden when there is a decision to be made and the
     *     next state to transition to depends upon the state of the domain object
     * </p>
     *
     * @param domainObject - being transitioned.
     * @param serviceRegistry2 -to lookup domain services etc
     */
    @Programmatic
    boolean canApply(final DO domainObject, final ServiceRegistry2 serviceRegistry2);

    /**
     * Allows the type to apply changes to the target domain object if necessary.
     *
     * <p>
     *     For implementations that hold the relevant state entirely in the corresponding {@link StateTransition}, this
     *     method will be a no-op.  However, some implementations might want to "push" the current state onto the
     *     domain object (for convenience or as a performance optimisation); in which case this is the place to do it.
     * </p>
     */
    void applyTo(DO domainObject, final ServiceRegistry2 serviceRegistry2);

    /**
     * The {@link EstatioRole task role}, if any, that any {@link Task} wrapping this transition must be routed to.
     *
     * <p>
     *     Said another way: a {@link Task} can only created as a wrapper around this transition if a
     *     {@link EstatioRole task role} has been provided.
     * </p>
     *
     * @param serviceRegistry2
     */
    @Programmatic
    EstatioRole assignTaskTo(final ServiceRegistry2 serviceRegistry2);

    /**
     * Creates a new {@link StateTransition transition} for the domain object.
     *
     * <p>
     *     This method is only intended to be called by {@link StateTransitionService}, which checks that the
     *     domain object is already in the specified fromState, and can otherwise be
     *     {@link #canApply(Object, ServiceRegistry2) applied}.
     * </p>
     */
    @Programmatic
    ST createTransition(
            final DO domainObject,
            final S fromState,
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
}

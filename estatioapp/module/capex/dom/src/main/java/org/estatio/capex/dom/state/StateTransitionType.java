package org.estatio.capex.dom.state;

import java.util.List;

import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.capex.dom.task.Task;
import org.estatio.dom.roles.EstatioRole;

/**
 * An identified sequence of {@link State} transitions, in other words a <i>state transition chart</i>.
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
     *     At a minimum the implementation should check that the type of the domain object is compatible with the
     *     implementation of this {@link StateTransitionType}.
     * </p>
     *
     * <p>
     *     REVIEW: these words out of date...
     *
     *     The domain object's current state will, at least, be compatible with <i>this</i> transition's
     *     {@link #getFromStates() from state}(s).  It is <i>not</i> necessary for there to be any
     *     {@link #assignTaskTo(ServiceRegistry2) task role} associated with this transition, however.
     * </p>
     */
    @Programmatic
    boolean canApply(final DO domainObject, final ServiceRegistry2 serviceRegistry2);

    void applyTo(DO domainObject, final ServiceRegistry2 serviceRegistry2);

    /**
     * The {@link EstatioRole task role}, if any, that any {@link Task} wrapping this transition must be routed to.
     *
     * <p>
     *     Said another way: a {@link Task} can only created as a wrapper around this transition if a
     *     {@link EstatioRole task role} has been provided.
     * </p>
     * @param serviceRegistry2
     */
    @Programmatic
    EstatioRole assignTaskTo(final ServiceRegistry2 serviceRegistry2);

    /**
     * Only called if {@link #assignTaskTo(ServiceRegistry2)} is non-null, and
     * {@link #canApply(Object, ServiceRegistry2)} also returns <tt>true</tt>.
     *
     * <p>
     *     Typically implementations might want to cache results from
     *     {@link #canApply(Object, ServiceRegistry2)} (lookup {@link QueryResultsCache} from provided
     *     {@link ServiceRegistry2}).
     * </p>
     */
    @Programmatic
    ST createTransition(
            final DO domainObject,
            final ServiceRegistry2 serviceRegistry2, final S fromState);

}

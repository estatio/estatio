
package org.estatio.module.capex.dom.state;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.module.capex.dom.task.Task;

/**
 * SPI service, required to be implemented by every implementation of {@link StateTransition}.
 */
public interface StateTransitionServiceSupport <
        DO,
        ST extends StateTransition<DO, ST, STT, S>,
        STT extends StateTransitionType<DO, ST, STT, S>,
        S extends State<S>
        > {

    /**
     * Whether this service supports the provided {@link StateTransitionType}.
     *
     * <p>
     *  For every such implementation of {@link StateTransitionType} the expectation is that precisely one service
     *  supports it.
     * </p>
     */
    @Programmatic
    boolean supports(final StateTransitionType<?,?,?,?> transitionType);

    /**
     * Whether this service supports the provided transition type (being the
     * {@link DomainObject#objectType() object type} of the corresponding implementation of {@link StateTransition}).
     *
     * <p>
     *  For every such implementation of {@link StateTransitionType} the expectation is that precisely one service
     *  supports it.
     * </p>
     */
    @Programmatic
    boolean supports(final String transitionType);

    /**
     * Supports {@link StateTransitionService#pendingTransitionOf(Object, StateTransitionType)}.
     */
    @Programmatic
    ST pendingTransitionOf(DO domainObject);

    /**
     * Supports {@link StateTransitionService#pendingTransitionOf(Object, StateTransitionType)}.
     */
    @Programmatic
    ST mostRecentlyCompletedTransitionOf(DO domainObject);

    /**
     * Supports {@link StateTransitionService#currentStateOf(Object, StateTransitionType)}.
     * @return
     */
    @Programmatic
    S currentStateOf(DO domainObject);

    /**
     * All available instances of {@link StateTransitionType}.
     *
     * <p>
     *      Used to search for next {@link StateTransitionType transition (type)}s that might occur from a given approval.
     * </p>
     */
    @Programmatic
    STT[] allTransitionTypes();


    @Programmatic
    ST findFor(Task task);

    @Programmatic
    Class<ST> transitionClassFor(final StateTransitionType<?, ?, ?, ?> prototypeTransitionType);
}

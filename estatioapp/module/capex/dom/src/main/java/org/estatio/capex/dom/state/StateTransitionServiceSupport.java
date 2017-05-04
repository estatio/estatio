
package org.estatio.capex.dom.state;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.capex.dom.task.Task;

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
     * Supports {@link StateTransitionService#currentTransitionOf(Object, StateTransitionType)}.
     */
    @Programmatic
    ST currentTransitionOf(DO domainObject);

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
     *      Used to search for next {@link StateTransitionType transition (type)}s that might occur from a given state.
     * </p>
     */
    @Programmatic
    STT[] allTransitionTypes();

    /**
     * Supports {@link StateTransitionService#findIncomplete(Object, StateTransitionType)}.
     */
    @Programmatic
    ST findIncomplete(DO domainObject);

    @Programmatic
    ST findFor(Task task);

}

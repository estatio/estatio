
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
     * All available instances of {@link StateTransitionType}.
     *
     * <p>
     *      Used to search for next {@link StateTransitionType transition (type)}s that might occur from a given state.
     * </p>
     */
    @Programmatic
    STT[] allTransitionTypes();


    @Programmatic
    ST findIncomplete(
            final DO domainObject,
            final STT transitionType);

    @Programmatic
    ST findFor(Task task);

}

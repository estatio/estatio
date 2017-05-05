package org.estatio.capex.dom.state;

/**
 * Enumerates individual states with respect to some {@link StateTransitionChart state transition chart}.
 *
 * <p>
 *     Intended to be implemented by an enum.
 * </p>
 */
public interface State<S extends State<S>> {

}

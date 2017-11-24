package org.estatio.module.capex.dom.state;

/**
 * Enumerates individual states with respect to some {@link StateTransitionType state transition chart}.
 *
 * <p>
 *     Intended to be implemented by an enum.
 * </p>
 */
public interface State<S extends State<S>> {

}

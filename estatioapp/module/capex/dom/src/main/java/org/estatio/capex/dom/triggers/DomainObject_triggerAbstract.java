package org.estatio.capex.dom.triggers;

import java.util.List;

import org.estatio.capex.dom.state.State;
import org.estatio.capex.dom.state.StateTransition;
import org.estatio.capex.dom.state.StateTransitionType;

/**
 * Subclasses should be annotated using: @Mixin(method = "act")
 */
public abstract class DomainObject_triggerAbstract<
        DO,
        ST extends StateTransition<DO, ST, STT, S>,
        STT extends StateTransitionType<DO, ST, STT, S>,
        S extends State<S>
        > extends DomainObject_triggerBaseAbstract<DO,ST,STT,S> {

    protected final DO domainObject;

    protected DomainObject_triggerAbstract(
            final DO domainObject,
            final Class<ST> stateTransitionClass,
            final List<S> fromStates) {
        super(stateTransitionClass, fromStates);
        this.domainObject = domainObject;
    }

    @Override
    public DO getDomainObject() {
        return domainObject;
    }
}

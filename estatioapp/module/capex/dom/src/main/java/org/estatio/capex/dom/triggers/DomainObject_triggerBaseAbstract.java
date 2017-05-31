package org.estatio.capex.dom.triggers;

import javax.inject.Inject;

import org.estatio.capex.dom.state.State;
import org.estatio.capex.dom.state.StateTransition;
import org.estatio.capex.dom.state.StateTransitionService;
import org.estatio.capex.dom.state.StateTransitionType;

/**
 * Subclasses should be annotated using: @Mixin(method = "act")
 */
public abstract class DomainObject_triggerBaseAbstract<
        DO,
        ST extends StateTransition<DO, ST, STT, S>,
        STT extends StateTransitionType<DO, ST, STT, S>,
        S extends State<S>
        > {

    protected final STT transitionType;

    protected DomainObject_triggerBaseAbstract(final STT transitionType) {
        this.transitionType = transitionType;
    }

    protected abstract DO getDomainObject();

    /**
     * Subclasses must call, to ensure that the state transition occurs.
     *
     * @return - the {@link StateTransition} most recently completed for the domain object.
     */
    protected final ST triggerStateTransition(final String comment) {
        return stateTransitionService.trigger(getDomainObject(), transitionType, comment);
    }

    /**
     * Subclasses must call, typically in their <tt>hideAct()</tt> guargs, in order to check whether {@link #triggerStateTransition(String)}.
     */
    protected final boolean cannotTriggerStateTransition() {
        return !canTriggerStateTransition();
    }

    private boolean canTriggerStateTransition() {
        return stateTransitionService.canTrigger(getDomainObject(), transitionType);
    }

    @Inject
    protected StateTransitionService stateTransitionService;

}

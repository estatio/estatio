package org.estatio.capex.dom.triggers;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.SemanticsOf;

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

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public DO act(@Nullable final String comment) {
        stateTransitionService.trigger(getDomainObject(), transitionType, comment);
        return getDomainObject();
    }

    /**
     * Subclasses should override and make <tt>public</tt>.
     */
    protected boolean hideAct() {
        return !stateTransitionService.canTrigger(getDomainObject(), transitionType);
    }

    @Inject
    protected StateTransitionService stateTransitionService;

}

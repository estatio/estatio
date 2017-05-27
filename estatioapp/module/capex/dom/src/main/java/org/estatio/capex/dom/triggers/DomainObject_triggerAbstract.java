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
public abstract class DomainObject_triggerAbstract<
        DO,
        ST extends StateTransition<DO, ST, STT, S>,
        STT extends StateTransitionType<DO, ST, STT, S>,
        S extends State<S>
        > {

    protected final DO domainObject;
    protected final STT transitionType;

    protected DomainObject_triggerAbstract(final DO domainObject, final STT transitionType) {
        this.domainObject = domainObject;
        this.transitionType = transitionType;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public DO act(@Nullable final String comment) {
        stateTransitionService.trigger(domainObject, transitionType, comment);
        return domainObject;
    }

    public boolean hideAct() {
        return !stateTransitionService.canTrigger(domainObject, transitionType);
    }

    @Inject
    protected StateTransitionService stateTransitionService;

}

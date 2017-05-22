package org.estatio.capex.dom.task;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;

import org.estatio.capex.dom.state.State;
import org.estatio.capex.dom.state.StateTransition;
import org.estatio.capex.dom.state.StateTransitionService;
import org.estatio.capex.dom.state.StateTransitionType;

public abstract class AbstractTransitionMixin<
        DO,
        ST extends StateTransition<DO, ST, STT, S>,
        STT extends StateTransitionType<DO, ST, STT, S>,
        S extends State<S>
        > {

    protected final DO domainObject;
    protected final STT transitionType;

    protected AbstractTransitionMixin(final DO domainObject, final STT transitionType) {
        this.domainObject = domainObject;
        this.transitionType = transitionType;
    }

    @Action()
    public DO act(@Nullable final String comment) {
        stateTransitionService.apply(domainObject, transitionType, comment);
        return domainObject;
    }

    public boolean hideAct() {
        return !stateTransitionService.canApply(domainObject, transitionType);
    }

    @Inject
    protected StateTransitionService stateTransitionService;

}

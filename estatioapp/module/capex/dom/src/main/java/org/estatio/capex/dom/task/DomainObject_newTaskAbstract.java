package org.estatio.capex.dom.task;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.services.eventbus.ActionDomainEvent;

import org.estatio.capex.dom.state.State;
import org.estatio.capex.dom.state.StateTransitionAbstract;
import org.estatio.capex.dom.state.StateTransitionRepositoryGeneric;
import org.estatio.capex.dom.state.StateTransitionService;
import org.estatio.capex.dom.state.StateTransitionServiceSupportGeneric;
import org.estatio.capex.dom.state.StateTransitionType;
import org.estatio.dom.roles.EstatioRole;

/**
 * Subclasses should be annotated using: @Mixin(method = "act")
 *
 *
 * hmmm.... I'm deleting the subclasses that I had implemented for this; I'm not 100% certain this is functionality
 * that can/should be made completely generic.  But keeping this abstract class in case I'm wrong...
 */
public abstract class DomainObject_newTaskAbstract<
        DO,
        ST extends StateTransitionAbstract<DO, ST, STT, S>,
        STT extends StateTransitionType<DO, ST, STT, S>,
        S extends State<S>
        > {

    public static class DomainEvent extends ActionDomainEvent<DomainObject_newTaskAbstract> { }

    protected final DO domainObject;
    private final Class<STT> stateTransitionTypeClass;
    private final Class<ST> stateTransitionClass;

    public DomainObject_newTaskAbstract(
            final DO domainObject,
            final Class<ST> stateTransitionClass, final Class<STT> stateTransitionTypeClass) {
        this.domainObject = domainObject;
        this.stateTransitionTypeClass = stateTransitionTypeClass;
        this.stateTransitionClass = stateTransitionClass;
    }

    @Action(
            domainEvent = DomainEvent.class
    )
    @MemberOrder(name = "tasks", sequence = "1")
    public Task act(
            final EstatioRole assignTo,
            final STT transitionType,
            @Nullable
            final String description) {
        final S currState = stateTransitionService.currentStateOf(domainObject, stateTransitionClass);

        final ST stateTransition =
                stateTransitionRepositoryGeneric.create(
                        domainObject, transitionType, currState, assignTo, description, stateTransitionClass);
        return stateTransition.getTask();
    }

    public List<STT> choices1Act() {

        // find all available transitions
        final S currState = stateTransitionService.currentStateOf(domainObject, stateTransitionClass);
        final List<STT> transitionsFromCurrent = stateTransitionServiceSupportGeneric
                .transitionTypesFrom(currState, stateTransitionTypeClass);

        // remove the current
        final ST pendingTransition = stateTransitionRepositoryGeneric
                .findFirstByDomainObject(domainObject, stateTransitionClass);
        if(pendingTransition != null) {
            final STT transitionType = pendingTransition.getTransitionType();
            transitionsFromCurrent.remove(transitionType);
        }

        return transitionsFromCurrent;
    }

    public String disableAct() {
        return choices1Act().isEmpty()
                ? String.format("No other transitions currently apply")
                : null;
    }

    @Inject
    StateTransitionService stateTransitionService;

    @Inject
    StateTransitionRepositoryGeneric stateTransitionRepositoryGeneric;

    @Inject
    StateTransitionServiceSupportGeneric stateTransitionServiceSupportGeneric;


}

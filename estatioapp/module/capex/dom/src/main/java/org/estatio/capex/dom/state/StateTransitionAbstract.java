package org.estatio.capex.dom.state;

import javax.inject.Inject;

import org.joda.time.LocalDateTime;

import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.clock.ClockService;

import org.estatio.capex.dom.task.Task;

public abstract class StateTransitionAbstract<
        DO,
        ST extends StateTransitionAbstract<DO, ST, STT, S>,
        STT extends StateTransitionType<DO, ST, STT, S>,
        S extends State<S>
        > implements StateTransition<DO,ST,STT,S> {

    /**
     * required by DN (because subclasses of this class are persistence capable even though this class is not.
     */
    public StateTransitionAbstract() {}

    public StateTransitionAbstract(
            final DO domainObject,
            final STT transitionType,
            final S fromState,
            final Task taskIfAny) {
        setDomainObject(domainObject);
        setTransitionType(transitionType);
        setFromState(fromState);
        setTask(taskIfAny);
    }


    @Programmatic
    public abstract DO getDomainObject();
    @Programmatic
    public abstract void setDomainObject(DO domainObject);


    public abstract S getFromState();
    protected abstract void setFromState(final S fromState);

    public abstract STT getTransitionType();
    public abstract void setTransitionType(final STT transitionType);

    public abstract S getToState();
    public abstract void setToState(S toState);

    @Override
    public abstract Task getTask();
    public abstract void setTask(final Task task);

    public abstract LocalDateTime getCreatedOn();
    public abstract void setCreatedOn(final LocalDateTime createdOn);

    public abstract LocalDateTime getCompletedOn();
    public abstract void setCompletedOn(final LocalDateTime completedOn);


    public boolean isCompleted() {
        return getCompletedOn() != null;
    }

    @Programmatic
    @Override
    public void completed() {
        setCompletedOn(clockService.nowAsLocalDateTime());
        setToState(getTransitionType().getToState());
    }


    ///////////////

    @Inject
    protected ClockService clockService;

}

package org.estatio.capex.dom.state;

import javax.inject.Inject;

import org.joda.time.LocalDateTime;

import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.util.Enums;
import org.apache.isis.applib.util.ObjectContracts;

import org.isisaddons.module.security.app.user.MeService;

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

    public String title() {
        final StringBuilder buf = new StringBuilder();
        if (isCompleted()) {
            buf.append(nameOf(getToState()));
            if(getTask() != null) {
                buf.append(" (").append(getTask().getCompletedBy()).append(")");
            }
            // buf.append(" on ").append(getCompletedOn());
        } else {
            buf.append("Pending: ");
            buf.append(nameOf(getTransitionType()));
        }
        return buf.toString();
    }

    private static String nameOf(final Object obj) {
        if(obj instanceof Enum) {
            return Enums.getFriendlyNameOf((Enum<?>) obj);
        } else
            return obj.toString();
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

    public abstract String getCompletedBy();
    public abstract void setCompletedBy(final String completedBy);

    public abstract String getComment();
    public abstract void setComment(String comment);

    public abstract boolean isCompleted();
    public abstract void setCompleted(boolean completed);

    @Programmatic
    @Override
    public void completed(final String comment) {
        final LocalDateTime completedOn = clockService.nowAsLocalDateTime();
        final String completedBy = meService.me().getName();

        setCompletedBy(completedBy);
        setCompletedOn(completedOn);
        setComment(comment);

        final Task task = getTask();
        if(task != null) {
            task.setCompletedBy(completedBy);
            task.setCompletedOn(completedOn);
            task.setComment(comment);
        }

        setCompleted(true);
        setToState(getTransitionType().getToState());
    }


    public String toString() {
        return ObjectContracts.toString(this, "fromState","transitionType","toState","domainObject");
    }

    ///////////////

    @Inject
    protected ClockService clockService;
    @Inject
    protected MeService meService;

}

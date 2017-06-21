package org.estatio.capex.dom.task;

/**
 * Base class for mixins on {@link Task} that delegate to a corresponding mixin on some domain object which will
 * result in a {@link Task} being completed.
 */
public abstract class Task_mixinPropAbstract<M, DO> extends Task_mixinAbstract<M,DO> {

    public Task_mixinPropAbstract(final Task task, final Class<M> mixinClass) {
        super(task, mixinClass);
    }

    /**
     * Subclasses should override and make <tt>public</tt>.
     */
    protected boolean hideProp() {
        return task.isCompleted() || getDomainObjectIfAny() == null;
    }


}

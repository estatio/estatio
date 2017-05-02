package org.estatio.capex.dom.invoice.rule;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.wrapper.WrapperFactory;

import org.estatio.capex.dom.task.Task;

// TODO: UNUSED, should probably delete...
public abstract class TaskStateOwner_transitionAbstract<TT extends TaskTransition<DO, S, TT>, S extends TaskState<DO, S>, DO extends TaskState.Owner<DO, S>> {

    protected final DO owner;
    protected final TT prototype;

    public TaskStateOwner_transitionAbstract(final DO owner, final Class<TT> prototype) {
        this.owner = owner;
        this.prototype = prototype.getEnumConstants()[0];
    }

    @Action()
    public DO $$(final TT transition){
        final Task<?> task = TaskTransition.Util.apply(transition, owner, wrapperFactory, factoryService);
        return owner;
    }

    public String disable$$() {
        return choices0$$().isEmpty() ? "No transitions apply": null;
    }
    public List<TT> choices0$$() {
        return TaskTransition.Util.transitionsFrom(prototype, owner.getTaskState());
    }

    @Inject
    private FactoryService factoryService;
    @Inject
    private WrapperFactory wrapperFactory;

}

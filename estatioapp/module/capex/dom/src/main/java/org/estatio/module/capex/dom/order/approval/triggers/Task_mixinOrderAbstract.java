package org.estatio.module.capex.dom.order.approval.triggers;

import javax.inject.Inject;

import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.approval.OrderApprovalStateTransition;
import org.estatio.module.capex.dom.task.Task;
import org.estatio.module.capex.dom.task.Task_mixinActAbstract;

public abstract class Task_mixinOrderAbstract<M>
        extends
        Task_mixinActAbstract<M, Order> {

    public static abstract class ActionDomainEvent<MIXIN>
            extends Task_mixinActAbstract.ActionDomainEvent<MIXIN> {
        public Class<?> getStateTransitionClass() {
            return OrderApprovalStateTransition.class;
        }
    }

    protected final Task task;

    public Task_mixinOrderAbstract(final Task task, final Class<M> mixinClass) {
        super(task, mixinClass);
        this.task = task;
    }

    @Override
    protected Order doGetDomainObjectIfAny() {
        final OrderApprovalStateTransition transition = repository.findByTask(this.task);
        return transition != null ? transition.getOrdr() : null;
    }

    @Inject
    OrderApprovalStateTransition.Repository repository;

}

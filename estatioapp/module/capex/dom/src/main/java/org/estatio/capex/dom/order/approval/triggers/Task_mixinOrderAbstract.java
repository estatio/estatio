package org.estatio.capex.dom.order.approval.triggers;

import javax.inject.Inject;

import org.estatio.capex.dom.order.Order;
import org.estatio.capex.dom.order.approval.OrderApprovalStateTransition;
import org.estatio.capex.dom.task.Task;
import org.estatio.capex.dom.task.Task_mixinActAbstract;

public abstract class Task_mixinOrderAbstract<M>
        extends
        Task_mixinActAbstract<M, Order> {

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

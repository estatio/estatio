package org.estatio.capex.dom.task;

import javax.inject.Inject;

import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;

/**
 * Base class for mixins on {@link Task} that delegate to a corresponding mixin on some domain object which will
 * result in a {@link Task} being completed.
 */
public abstract class Task_mixinAbstract<M, DO> {

    protected final Task task;
    private final Class<M> mixinClass;

    public Task_mixinAbstract(final Task task, final Class<M> mixinClass) {
        this.task = task;
        this.mixinClass = mixinClass;
    }

    protected DO getDomainObjectIfAny() {
        return queryResultsCache.execute(
                this::doGetDomainObjectIfAny,
                getClass(), "getDomainObjectIfAny", task);
    }

    protected M mixin() {
        return factoryService.mixin(mixinClass, doGetDomainObjectIfAny());
    }

    protected abstract DO doGetDomainObjectIfAny();

    @Inject
    TaskRepository taskRepository;

    @Inject
    MessageService messageService;

    @Inject
    FactoryService factoryService;

    @Inject
    QueryResultsCache queryResultsCache;

}

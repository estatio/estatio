package org.estatio.module.capex.dom.task;

import javax.inject.Inject;

import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;

/**
 * Base class for mixins on {@link Task} that delegate to a corresponding mixin on some domain object which will
 * result in a {@link Task} being completed.
 */
public abstract class Task_mixinAbstract<M, DO> extends Task_abstract {

    private final Class<M> mixinClass;

    public Task_mixinAbstract(final Task task, final Class<M> mixinClass) {
        super(task);
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

package org.estatio.module.capex.dom.documents.categorisation.triggers;

import javax.inject.Inject;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.module.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition;
import org.estatio.module.capex.dom.task.Task;
import org.estatio.module.capex.dom.task.Task_mixinActAbstract;

/**
 * This cannot be inlined (needs to be a mixin) because Task does not know about the domain object it refers to.
 */
public abstract class Task_mixinDocumentAbstract<MIXIN>
        extends Task_mixinActAbstract<MIXIN, Document> {

    protected final Task task;

    public Task_mixinDocumentAbstract(final Task task, final Class<MIXIN> mixinClass) {
        super(task, mixinClass);
        this.task = task;
    }

    public static abstract class ActionDomainEvent<MIXIN>
            extends Task_mixinActAbstract.ActionDomainEvent<MIXIN> {
        public Class<?> getStateTransitionClass() {
            return IncomingDocumentCategorisationStateTransition.class;
        }
    }

    @Override
    protected Document doGetDomainObjectIfAny() {
        final IncomingDocumentCategorisationStateTransition transition = repository.findByTask(this.task);
        return transition != null ? transition.getDocument() : null;
    }

    @Inject
    IncomingDocumentCategorisationStateTransition.Repository repository;

}

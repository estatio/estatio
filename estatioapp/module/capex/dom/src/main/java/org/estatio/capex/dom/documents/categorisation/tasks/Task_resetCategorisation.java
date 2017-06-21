package org.estatio.capex.dom.documents.categorisation.tasks;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition;
import org.estatio.capex.dom.documents.categorisation.document.Document_resetCategorisation;
import org.estatio.capex.dom.task.Task;
import org.estatio.capex.dom.task.Task_mixinActAbstract;

@Mixin(method = "act")
public class Task_resetCategorisation
        extends
        Task_mixinActAbstract<Document_resetCategorisation, Document> {

    protected final Task task;

    public Task_resetCategorisation(final Task task) {
        super(task, Document_resetCategorisation.class);
        this.task = task;
    }

    @Action()
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Object act(
            @Nullable final String comment,
            final boolean goToNext) {
//        Object mixinResult = mixin().act(comment);
//        return toReturnElse(goToNext, mixinResult);

        // need to evaluate this first, because it's possible that resetting the categorisation will delete *this* task
        // meaning that it may no longer be interacted with.
        Object nextTaskElseNull = toReturnElse(goToNext, null);
        Object mixinResult = mixin().act(comment);
        return nextTaskElseNull != null ? nextTaskElseNull : mixinResult;

    }

    public boolean default1Act() {
        return true;
    }

    public boolean hideAct() {
        return super.hideAct() || mixin().hideAct();
    }

    @Override
    protected Document doGetDomainObjectIfAny() {
        final IncomingDocumentCategorisationStateTransition transition = repository.findByTask(this.task);
        return transition != null ? transition.getDocument() : null;
    }

    @Inject
    IncomingDocumentCategorisationStateTransition.Repository repository;

}

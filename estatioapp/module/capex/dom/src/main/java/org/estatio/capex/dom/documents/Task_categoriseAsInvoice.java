package org.estatio.capex.dom.documents;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition;
import org.estatio.capex.dom.task.Task;
import org.estatio.capex.dom.task.Task_mixinAbstract;
import org.estatio.dom.asset.Property;

@Mixin(method = "act")
public class Task_categoriseAsInvoice
        extends
        Task_mixinAbstract<Document_categoriseAsInvoice, Document> {

    protected final Task task;

    public Task_categoriseAsInvoice(final Task task) {
        super(task, Document_categoriseAsInvoice.class);
        this.task = task;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Task act(
            @Nullable final Property property,
            @Nullable final String comment,
            final boolean goToNext) {
        mixin().act(property, comment);
        return taskToReturn(goToNext, task);
    }

    public boolean hideAct() {
        if (super.hideAct()) {
            return true;
        }
        if (mixin().hideAct()) {
            return true;
        }
        return false;
    }

    @Override
    protected Document doGetDomainObjectIfAny() {
        final IncomingDocumentCategorisationStateTransition transition = repository.findByTask(this.task);
        return transition != null ? transition.getDocument() : null;
    }

    @Inject
    IncomingDocumentCategorisationStateTransition.Repository repository;

}

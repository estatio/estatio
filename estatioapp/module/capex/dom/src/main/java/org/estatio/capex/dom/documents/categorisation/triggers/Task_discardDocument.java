package org.estatio.capex.dom.documents.categorisation.triggers;

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
import org.estatio.capex.dom.task.Task_mixinActAbstract;

@Mixin(method = "act")
public class Task_discardDocument
        extends Task_mixinActAbstract<Document_discard, Document> {

    protected final Task task;

    public Task_discardDocument(final Task task) {
        super(task, Document_discard.class);
        this.task = task;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    @ActionLayout(contributed = Contributed.AS_ACTION, cssClassFa = "trash-o")
    public Object act(
            @Nullable final String comment,
            final boolean goToNext) {
        Document mixinResult = mixin().act(comment);
        return toReturnElse(goToNext, mixinResult);
    }

    public boolean default1Act() {
        return true;
    }

    public boolean hideAct() {
        return super.hideAct() || mixin().hideAct();
    }

    public String disableAct() {
        if(doGetDomainObjectIfAny() == null) {
            return null;
        }
        return  mixin().disableAct();
    }

    @Override
    protected Document doGetDomainObjectIfAny() {
        final IncomingDocumentCategorisationStateTransition transition = repository.findByTask(this.task);
        return transition != null ? transition.getDocument() : null;
    }

    @Inject
    IncomingDocumentCategorisationStateTransition.Repository repository;

}

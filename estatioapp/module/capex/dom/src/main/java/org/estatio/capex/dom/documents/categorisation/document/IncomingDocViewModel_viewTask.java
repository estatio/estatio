package org.estatio.capex.dom.documents.categorisation.document;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition;

@Mixin(method = "act")
public class IncomingDocViewModel_viewTask {

    protected final IncomingDocViewModel viewModel;

    public IncomingDocViewModel_viewTask(final IncomingDocViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Object act() {
        return pendingTask();
    }

    public boolean hideAct() {
        return pendingTask() == null;
    }

    private Object pendingTask() {
        final Document document = viewModel.getDocument();
        final IncomingDocumentCategorisationStateTransition pendingTransition =
                repository.findByDomainObjectAndCompleted(document, false);
        return pendingTransition != null
                ? pendingTransition.getTask()
                : null;
    }


    @Inject IncomingDocumentCategorisationStateTransition.Repository repository;
}

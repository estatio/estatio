package org.estatio.capex.dom.documents.categorisation.tasks;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition;
import org.estatio.capex.dom.documents.categorisation.document.IncomingDocViewModel;
import org.estatio.capex.dom.task.Task;
import org.estatio.dom.invoice.DocumentTypeData;

public abstract class TaskOrDoc_classifyAsAbstract {

    private final DocumentTypeData documentTypeData;

    public TaskOrDoc_classifyAsAbstract(final DocumentTypeData documentTypeData) {
        this.documentTypeData = documentTypeData;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public IncomingDocViewModel act() {
        IncomingDocViewModel viewModel = doCreate();

        serviceRegistry2.injectServicesInto(viewModel);
        viewModel.inferFixedAssetFromPaperclips();

        // to support 'goToNext' when finished with the view model
        viewModel.setOriginatingTask(getTask());
        return viewModel;
    }

    protected abstract Task getTask();
    protected abstract Document getDocument();
    protected abstract IncomingDocViewModel doCreate();

    public boolean hideAct() {
        return getTask() == null || getTask().isCompleted() || getDocument() == null || !documentTypeData.isDocTypeFor(getDocument());
    }

    @Inject
    protected PaperclipRepository paperclipRepository;

    @Inject
    protected ServiceRegistry2 serviceRegistry2;

    @Inject
    protected IncomingDocumentCategorisationStateTransition.Repository docCategorisationTransitionRepository;

    @Inject
    protected QueryResultsCache queryResultsCache;

}

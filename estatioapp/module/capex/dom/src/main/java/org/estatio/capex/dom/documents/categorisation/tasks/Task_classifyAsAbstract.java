package org.estatio.capex.dom.documents.categorisation.tasks;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition;
import org.estatio.capex.dom.task.Task;
import org.estatio.dom.invoice.DocumentTypeData;

public abstract class Task_classifyAsAbstract extends TaskOrDoc_classifyAsAbstract {

    protected final Task task;

    public Task_classifyAsAbstract(final Task task, final DocumentTypeData documentTypeData) {
        super(documentTypeData);
        this.task = task;
    }

    @Override
    public Task getTask() {
        return task;
    }

    protected Document getDocument() {
        return queryResultsCache.execute(
                this::doGetDocument,
                getClass(), "getDocument", getTask());
    }

    private Document doGetDocument() {
        final IncomingDocumentCategorisationStateTransition transition = docCategorisationTransitionRepository.findByTask(getTask());
        return transition != null ? transition.getDocument() : null;
    }

}

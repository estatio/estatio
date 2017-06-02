package org.estatio.capex.dom.documents.categorisation.document;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition;
import org.estatio.capex.dom.documents.categorisation.tasks.TaskOrDoc_classifyAsAbstract;
import org.estatio.capex.dom.task.Task;
import org.estatio.dom.invoice.DocumentTypeData;

public abstract class Document_classifyAsAbstract extends TaskOrDoc_classifyAsAbstract {

    protected final Document document;

    public Document_classifyAsAbstract(final Document document, final DocumentTypeData documentTypeData) {
        super(documentTypeData);
        this.document = document;
    }

    @Override
    protected Task getTask() {
        return queryResultsCache.execute(
                this::doGetTask,
                getClass(), "getTask", document
                );
    }

    private Task doGetTask() {
        final IncomingDocumentCategorisationStateTransition transition =
                docCategorisationTransitionRepository.findByDomainObjectAndCompleted(document, false);
        return transition != null ? transition.getTask() : null;
    }

    @Override
    protected Document getDocument() {
        return document;
    }


}

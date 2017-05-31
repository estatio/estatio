package org.estatio.capex.dom.documents.categorisation.tasks;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition;
import org.estatio.capex.dom.task.Task;

@DomainService(nature = NatureOfService.DOMAIN)
public class TaskIncomingDocumentService {

    @Programmatic
    public Document lookupFor(final Task task) {
        return queryResultsCache.execute(() -> doProp(task), TaskIncomingDocumentService.class, "lookupFor", task);
    }

    private Document doProp(final Task task) {
        final IncomingDocumentCategorisationStateTransition stateTransition = repository.findByTask(task);
        if (stateTransition != null) {
            return stateTransition.getDocument();
        }
        return null;
    }

    @Inject
    IncomingDocumentCategorisationStateTransition.Repository repository;

    @Inject
    QueryResultsCache queryResultsCache;

}

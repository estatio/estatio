package org.estatio.capex.dom.documents.categorisation.tasks;

import javax.inject.Inject;

import org.wicketstuff.pdfjs.Scale;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.eventbus.ActionDomainEvent;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.wicket.pdfjs.cpt.applib.PdfJsViewer;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition;
import org.estatio.capex.dom.task.Task;

@Mixin(method = "prop")
public class Task_pdf {
    private final Task task;

    public Task_pdf(final Task task) {
        this.task = task;
    }

    public static class DomainEvent extends ActionDomainEvent<Document> {
    }

    @PdfJsViewer(initialPageNum = 1, initialScale = Scale._2_00, initialHeight = 900)
    @Action(semantics = SemanticsOf.SAFE, domainEvent = IncomingDocumentCategorisationStateTransition._pdf.DomainEvent.class)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public Blob prop() {
        return queryResultsCache.execute(this::doProp, Task_pdf.class, "prop", task);
    }

    Blob doProp() {
        final IncomingDocumentCategorisationStateTransition stateTransition = repository.findByTask(task);
        if (stateTransition != null) {
            return stateTransition.getDocument().getBlob();
        }
        return null;
    }

    public boolean hideProp() {
        return prop() == null;
    }

    @Inject
    IncomingDocumentCategorisationStateTransition.Repository repository;

    @Inject
    QueryResultsCache queryResultsCache;

}

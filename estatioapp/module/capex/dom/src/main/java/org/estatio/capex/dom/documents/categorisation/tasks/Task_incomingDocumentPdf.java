package org.estatio.capex.dom.documents.categorisation.tasks;

import javax.inject.Inject;

import org.wicketstuff.pdfjs.Scale;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.eventbus.ActionDomainEvent;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.wicket.pdfjs.cpt.applib.PdfJsViewer;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.task.Task;

@Mixin(method = "prop")
public class Task_incomingDocumentPdf {
    private final Task task;

    public Task_incomingDocumentPdf(final Task task) {
        this.task = task;
    }

    public static class DomainEvent extends ActionDomainEvent<Document> {
    }

    @PdfJsViewer(initialPageNum = 1, initialScale = Scale._2_00, initialHeight = 900)
    @Action(
            semantics = SemanticsOf.SAFE,
            domainEvent = Task_incomingDocumentPdf.DomainEvent.class
    )
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public Blob prop() {
        return taskIncomingDocumentPdfService.lookupPdfFor(task);
    }

    public boolean hideProp() {
        return prop() == null;
    }

    @Inject
    TaskIncomingDocumentPdfService taskIncomingDocumentPdfService;
}

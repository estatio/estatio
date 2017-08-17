package org.estatio.capex.dom.task.pdf;

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

/**
 * Although this mixin could be inlined, probably want to keep separate because there's a chance that Task may move into its own module.
 */
@Mixin(method = "prop")
public class Task_pdf {
    private final Task task;

    public Task_pdf(final Task task) {
        this.task = task;
    }

    public static class DomainEvent extends ActionDomainEvent<Document> {
    }

    @PdfJsViewer(initialPageNum = 1, initialScale = Scale.PAGE_WIDTH, initialHeight = 1500)
    @Action(
            semantics = SemanticsOf.SAFE,
            domainEvent = Task_pdf.DomainEvent.class
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

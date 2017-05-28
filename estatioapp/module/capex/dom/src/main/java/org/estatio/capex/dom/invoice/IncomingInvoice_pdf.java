package org.estatio.capex.dom.invoice;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import org.wicketstuff.pdfjs.Scale;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.wicket.pdfjs.cpt.applib.PdfJsViewer;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentAbstract;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import org.estatio.capex.dom.EstatioCapexDomModule;

@Mixin(method = "prop")
public class IncomingInvoice_pdf {
    private final IncomingInvoice incomingInvoice;

    public IncomingInvoice_pdf(final IncomingInvoice incomingInvoice) {
        this.incomingInvoice = incomingInvoice;
    }

    public static class DomainEvent extends EstatioCapexDomModule.ActionDomainEvent<Document> {
    }

    @PdfJsViewer(initialPageNum = 1, initialScale = Scale._2_00, initialHeight = 900)
    @Action(
            semantics = SemanticsOf.SAFE,
            domainEvent = IncomingInvoice_pdf.DomainEvent.class
    )
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public Blob prop() {
        return queryResultsCache.execute(this::doProp, IncomingInvoice_pdf.class, "prop", incomingInvoice);
    }

    Blob doProp() {
        final List<Paperclip> paperclips = paperclipRepository.findByAttachedTo(incomingInvoice);
        for (Paperclip paperclip : paperclips) {
            final DocumentAbstract document = paperclip.getDocument();
            if(Objects.equals(document.getMimeType(), "application/pdf")) {
                return document.getBlob();
            }
        }
        return null;
    }

    public boolean hideProp() {
        return prop() == null;
    }

    @Inject
    QueryResultsCache queryResultsCache;

    @Inject
    PaperclipRepository paperclipRepository;

}

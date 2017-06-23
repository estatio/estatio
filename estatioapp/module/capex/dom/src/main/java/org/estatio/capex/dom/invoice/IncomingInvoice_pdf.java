package org.estatio.capex.dom.invoice;

import java.util.Optional;

import javax.inject.Inject;

import org.wicketstuff.pdfjs.Scale;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.wicket.pdfjs.cpt.applib.PdfJsViewer;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentAbstract;

import org.estatio.capex.dom.EstatioCapexDomModule;

@Mixin(method = "prop")
public class IncomingInvoice_pdf {
    private final IncomingInvoice incomingInvoice;

    public IncomingInvoice_pdf(final IncomingInvoice incomingInvoice) {
        this.incomingInvoice = incomingInvoice;
    }

    public static class DomainEvent extends EstatioCapexDomModule.ActionDomainEvent<Document> {}

    @PdfJsViewer(initialPageNum = 1, initialScale = Scale._2_00, initialHeight = 900)
    @Action(
            semantics = SemanticsOf.SAFE,
            domainEvent = IncomingInvoice_pdf.DomainEvent.class
    )
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public Blob prop() {
        Optional<Document> documentIfAny = incomingInvoicePdfService.lookupIncomingInvoicePdfFrom(incomingInvoice);
        return documentIfAny.map(DocumentAbstract::getBlob).orElse(null);
    }

    public boolean hideProp() {
        return prop() == null;
    }

    @Inject
    IncomingInvoicePdfService incomingInvoicePdfService;

}

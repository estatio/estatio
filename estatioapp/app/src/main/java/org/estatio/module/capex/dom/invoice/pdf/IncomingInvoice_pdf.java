package org.estatio.module.capex.dom.invoice.pdf;

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

import org.estatio.module.capex.dom.documents.LookupAttachedPdfService;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;

/**
 * TODO: inline this mixin
 */
@Mixin(method = "prop")
public class IncomingInvoice_pdf {
    private final IncomingInvoice incomingInvoice;

    public IncomingInvoice_pdf(final IncomingInvoice incomingInvoice) {
        this.incomingInvoice = incomingInvoice;
    }

    @PdfJsViewer(initialPageNum = 1, initialScale = Scale.PAGE_WIDTH, initialHeight = 1500)
    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public Blob prop() {
        Optional<Document> documentIfAny = lookupAttachedPdfService.lookupIncomingInvoicePdfFrom(incomingInvoice);
        return documentIfAny.map(DocumentAbstract::getBlob).orElse(null);
    }

    public boolean hideProp() {
        return prop() == null;
    }

    @Inject
    LookupAttachedPdfService lookupAttachedPdfService;

}

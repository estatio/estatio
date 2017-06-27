package org.estatio.capex.dom.order;

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

import org.estatio.capex.dom.documents.LookupAttachedPdfService;

@Mixin(method = "prop")
public class Order_pdf {
    private final Order order;

    public Order_pdf(final Order order) {
        this.order = order;
    }

    @PdfJsViewer(initialPageNum = 1, initialScale = Scale.PAGE_WIDTH, initialHeight = 1500)
    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public Blob prop() {
        Optional<Document> documentIfAny = lookupAttachedPdfService.lookupOrderPdfFrom(order);
        return documentIfAny.map(DocumentAbstract::getBlob).orElse(null);
    }

    public boolean hideProp() {
        return prop() == null;
    }

    @Inject
    LookupAttachedPdfService lookupAttachedPdfService;

}

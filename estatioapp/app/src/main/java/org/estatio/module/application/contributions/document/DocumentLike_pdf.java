package org.estatio.module.application.contributions.document;

import org.wicketstuff.pdfjs.Scale;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.eventbus.ActionDomainEvent;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.wicket.pdfjs.cpt.applib.PdfJsViewer;

import org.incode.module.communications.dom.mixins.DocumentConstants;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentAbstract;
import org.incode.module.document.dom.impl.docs.DocumentLike;
import org.incode.module.document.dom.impl.docs.DocumentState;

/**
 * In effect replaces the {@link Document}'s own {@link Document#getBlob() blob} property with a derived
 * {@link DocumentLike_pdf#prop() pdf} property, correctly annotated with the {@link PdfJsViewer} annotation.
 *
 * <p>
 *     This is only done for {@link Document}s that contain a PDF.  A replacement `Document.layout.xml` positions this
 *     new derived property in the correct place.
 * </p>
 */
@Mixin(method="prop")
public class DocumentLike_pdf {

    private final DocumentLike documentLike;
    public DocumentLike_pdf(final DocumentLike documentLike) {
        this.documentLike = documentLike;
    }

    static boolean holdsPdf(final DocumentLike document) {
        return DocumentConstants.MIME_TYPE_APPLICATION_PDF.equals(document.getMimeType());
    }

    public static class DomainEvent extends ActionDomainEvent<Document> {
    }

    @PdfJsViewer(initialPageNum = 1, initialScale = Scale.PAGE_WIDTH, initialHeight = 1500)
    @Action(
            semantics = SemanticsOf.SAFE,
            domainEvent = DomainEvent.class
    )
    @ActionLayout(contributed= Contributed.AS_ASSOCIATION)
    public Blob prop() {
        return documentLike.getBlob();
    }
    public boolean hideProp() {
        return documentLike.getState() != DocumentState.RENDERED || !holdsPdf(this.documentLike);
    }

    @DomainService(nature = NatureOfService.DOMAIN)
    public static class DocumentBlobHideIfPdf extends AbstractSubscriber {

        @Programmatic
        @com.google.common.eventbus.Subscribe
        @org.axonframework.eventhandling.annotation.EventHandler
        public void on(DocumentAbstract.BlobDomainEvent ev) {
            switch (ev.getEventPhase()) {

            case HIDE:
                final DocumentAbstract document = ev.getSource();
                if(document instanceof DocumentLike && holdsPdf((DocumentLike)document)){
                    ev.hide();
                }
                break;
            }
        }
    }
}

package org.estatio.dom.document.pdfviewer;

import org.wicketstuff.pdfjs.Scale;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.eventbus.ActionDomainEvent;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.wicket.pdfjs.cpt.applib.PdfJsViewer;

import org.incode.module.communications.dom.mixins.DocumentConstants;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentState;


public class DocumentPdfJsViewerSupport {

    private DocumentPdfJsViewerSupport(){}

    //region > _pdf (derived property)
    @Mixin(method="prop")
    public static class _pdf {
        private final Document document;
        public _pdf(final Document  document) {
            this. document =  document;
        }
        public static class DomainEvent extends ActionDomainEvent<Document> {
        }

        @PdfJsViewer(initialPageNum = 1, initialScale = Scale._2_00, initialHeight = 900)
        @Action(semantics = SemanticsOf.SAFE, domainEvent = DomainEvent.class)
        @ActionLayout(contributed= Contributed.AS_ASSOCIATION)
        public Blob prop() {
            // TODO: business logic here
            return null;
        }
        public boolean hideProp() {
            return !holdsPdf();
        }

        protected boolean holdsPdf() {
            return document.getState() == DocumentState.RENDERED &&
                   DocumentConstants.MIME_TYPE_APPLICATION_PDF.equals(document.getMimeType());
        }
    }
    //endregion
}



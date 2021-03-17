package org.incode.module.communications.dom.spi;

import org.apache.isis.applib.annotation.Programmatic;

import org.incode.module.communications.dom.mixins.Document_sendByEmail;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.types.DocumentType;

/**
 * As used by the {@link Document_sendByEmail} mixin.
 */
public interface DocumentCommunicationSupport {

    @Programmatic
    DocumentType emailCoverNoteDocumentTypeFor(final Document document);

    @Programmatic
    void inferEmailHeaderFor(final Document document, final CommHeaderForEmail header);

    @Programmatic
    void inferPrintHeaderFor(final Document document, final CommHeaderForPost header);

}

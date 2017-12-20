package org.incode.module.document.dom.spi;

import java.util.List;

import org.apache.isis.applib.annotation.Programmatic;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.module.document.dom.impl.docs.Document_attachSupportingPdf;
import org.incode.module.document.dom.impl.types.DocumentType;

/**
 * Used by {@link Document_attachSupportingPdf} mixin, to obtain the list of available {@link DocumentType}s that can be used
 * to attach existing PDFs to a {@link Document}.
 *
 * <p>
 *     There is no need for the {@link DocumentType}s returned (by {@link #documentTypeChoicesFor(Document)}) to have
 *     corresponding {@link DocumentTemplate}s; no rendering is performed as the PDF already exists.
 *     The {@link DocumentType} just acts as a way of categorising the attached PDF (eg "tax receipt",
 *     "supplier receipt" and so on).
 * </p>
 */
public interface DocumentAttachmentAdvisor {
    @Programmatic
    List<DocumentType> documentTypeChoicesFor(final Document document);
    @Programmatic
    DocumentType documentTypeDefaultFor(final Document document);
    @Programmatic
    List<String> roleNameChoicesFor(final Document document);
    @Programmatic
    String roleNameDefaultFor(final Document document);
}

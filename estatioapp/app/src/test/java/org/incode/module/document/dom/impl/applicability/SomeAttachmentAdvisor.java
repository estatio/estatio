package org.incode.module.document.dom.impl.applicability;

import java.util.List;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;

public class SomeAttachmentAdvisor implements AttachmentAdvisor {
    @Override public List<PaperclipSpec> advise(
            final DocumentTemplate documentTemplate, final Object domainObject, final Document createdDocument) {
        return null;
    }
}

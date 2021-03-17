package org.incode.module.document.dom.impl.applicability;

import java.util.Collections;
import java.util.List;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;

/**
 * To allow documents to be rendered that are by default attached to no other objects.
 */
public class AttachmentAdvisorAttachToNone extends AttachmentAdvisorAbstract<Object> {

    public AttachmentAdvisorAttachToNone() {
        super(Object.class);
    }

    @Override
    protected List<PaperclipSpec> doAdvise(
            final DocumentTemplate documentTemplate,
            final Object domainObject, final Document createdDocument) {
        return Collections.emptyList();
    }
}

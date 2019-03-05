package org.incode.module.document.dom.impl.applicability;

import java.util.List;

import org.apache.isis.applib.annotation.Programmatic;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;

import lombok.Data;

/**
 * TODO: once have moved to DocumentTypeData, DocumentTemplateData and RenderingStrategyData, ought to be able to collapse this responsibility into DocumentTemplateData
 *
 * Implementation is responsible for providing a set of {@link AttachmentAdvisor.PaperclipSpec}s which describe how to
 * attach a (newly created) {@link Document} to existing domain objects.
 *
 * <p>
 *     (Class name is) referenced by {@link Applicability#getAttachmentAdvisorClassName()} ()}.
 * </p>
 */
public interface AttachmentAdvisor {

    @Data
    public static class PaperclipSpec {
        private final String roleName;
        private final Object attachTo;
        /**
         * Note that this may be <tt>null</tt> when the advisor is being asked if it <i>could</i> be used to attach for the domain object.
         */
        private final Document createdDocument;
    }

    /**
     * @param documentTemplate - to which this implementation applies, as per {@link DocumentTemplate#getAppliesTo()} and {@link Applicability#getAttachmentAdvisorClassName()}
     * @param domainObject - acting as the context for document created, from which derive the objects to attach the newly created {@link Document}.
     * @param createdDocument - note that this may be <tt>null</tt> when the advisor is being asked if it <i>could</i> be used to attach for the domain object.
     */
    @Programmatic
    List<PaperclipSpec> advise(
            final DocumentTemplate documentTemplate,
            final Object domainObject,
            final Document createdDocument);

}

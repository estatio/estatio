package org.incode.module.document.dom.impl.applicability;

import java.util.List;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;

/**
 * C
 * @param <T>
 */
public abstract class AttachmentAdvisorAbstract<T> implements AttachmentAdvisor {

    private final Class<T> expectedInputType;

    public AttachmentAdvisorAbstract(Class<T> expectedInputType) {
        this.expectedInputType = expectedInputType;
    }

    public final List<PaperclipSpec> advise(
            final DocumentTemplate documentTemplate,
            final Object domainObject,
            final Document createdDocument) {

        checkInputClass(domainObject);

        return doAdvise(documentTemplate, (T)domainObject, createdDocument);
    }

    /**
     * Optional hook; default implementation checks that the input type is of the correct type.
     */
    protected void checkInputClass(final Object domainObject) {
        final Class<?> actualInputType = domainObject.getClass();
        if(!(expectedInputType.isAssignableFrom(actualInputType))) {
            throw new IllegalArgumentException("The input document is required to be of type: " + expectedInputType.getName());
        }
    }

    /**
     * Mandatory hook.
     *
     * @param documentTemplate - to which this implementation applies, as per {@link DocumentTemplate#getAppliesTo()} and {@link Applicability#getAttachmentAdvisorClassName()}
     * @param domainObject - acting as the context for document created, from which derive the objects to attach the newly created {@link Document}.
     * @param createdDocument - note that this may be <tt>null</tt> when the advisor is being asked if it <i>could</i> be used to attach for the domain object.
     */
    protected abstract List<PaperclipSpec> doAdvise(
            final DocumentTemplate documentTemplate,
            final T domainObject,
            final Document createdDocument);


}

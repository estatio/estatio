package org.incode.module.document.dom.impl.applicability;

import org.incode.module.document.dom.impl.docs.DocumentTemplate;

/**
 * C
 * @param <T>
 */
public abstract class RendererModelFactoryAbstract<T> implements RendererModelFactory {

    private final Class<T> expectedInputType;

    public RendererModelFactoryAbstract(Class<T> expectedInputType) {
        this.expectedInputType = expectedInputType;
    }

    public final Object newRendererModel(
            final DocumentTemplate documentTemplate,
            final Object domainObject) {

        checkInputClass(domainObject);

        return doNewRendererModel(documentTemplate, (T)domainObject);
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
     */
    protected abstract Object doNewRendererModel(
            final DocumentTemplate documentTemplate,
            final T domainObject);


}

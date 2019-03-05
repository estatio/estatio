package org.incode.module.document.dom.impl.applicability;

import org.apache.isis.applib.annotation.Programmatic;

import org.incode.module.document.dom.impl.docs.DocumentTemplate;

/**
 * TODO: once have moved to DocumentTypeData, DocumentTemplateData and RenderingStrategyData, ought to be able to collapse this responsibility into DocumentTemplateData
 *
 * Implementation is responsible for creating the appropriate model object to feed into the
 * {@link DocumentTemplate#getContentRenderingStrategy() rendering}
 * {@link DocumentTemplate#getNameRenderingStrategy() strategies} of the supplied {@link DocumentTemplate}, obtaining
 * information from the supplied domainObject.
 *
 * <p>
 *     (Class name is) referenced by {@link Applicability#getRendererModelFactoryClassName()}.
 * </p>
 */
public interface RendererModelFactory {

    /**
     * @param documentTemplate - to which this implementation applies, as per {@link DocumentTemplate#getAppliesTo()} and {@link Applicability#getRendererModelFactoryClassName()}
     * @param domainObject - provides the input for the renderer model.
     */
    @Programmatic
    Object newRendererModel(
            final DocumentTemplate documentTemplate,
            final Object domainObject);

}

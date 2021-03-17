package org.incode.module.document.dom.mixins;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.linking.DeepLinkService;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.registry.ServiceRegistry;

import org.incode.module.document.DocumentModule;
import org.incode.module.document.dom.impl.docs.DocumentState;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;

/**
 * Subclasses should be annotated with <code>@Mixin(method='act')</code>
 */
public abstract class T_preview<T,P extends DocumentPreview<T>> {

    protected final T domainObject;

    public T_preview(final T domainObject) {
        this.domainObject = domainObject;
    }


    public static class ActionDomainEvent extends DocumentModule.ActionDomainEvent<T_preview> {}

    @Action(
            domainEvent = ActionDomainEvent.class,
            semantics = SemanticsOf.SAFE
    )
    public URL act(final DocumentTemplate template) throws IOException {
        final P preview = createPreview(template);
        final URI uri = deepLinkService.deepLinkFor(preview);
        return uri.toURL();
    }

    @Programmatic
    public P createPreview(final DocumentTemplate template) {
        final Object rendererModel = template.newRendererModel(domainObject);
        final P preview = serviceRegistry.injectServicesInto(newPreview());
        preview.setDomainObject(domainObject);
        preview.setType(template.getType());
        template.renderContent(preview, rendererModel);
        preview.setState(DocumentState.RENDERED);
        return preview;
    }

    protected abstract P newPreview();

    public boolean hideAct() {
        return choices0Act().isEmpty();
    }

    public DocumentTemplate default0Act() {
        final List<DocumentTemplate> documentTemplates = choices0Act();
        return documentTemplates.size() == 1 ? documentTemplates.get(0): null;
    }

    /**
     * All templates which are applicable to the domain object's atPath, and which can be previewed.
     */
    public List<DocumentTemplate> choices0Act() {
        return queryResultsCache.execute(
                () -> documentTemplateService.documentTemplatesForCreateAndAttach(domainObject),
                getClass(), "act", domainObject);
    }


    @Inject
    DocumentTemplateForAtPathService documentTemplateService;

    @Inject
    QueryResultsCache queryResultsCache;

    @Inject
    ServiceRegistry serviceRegistry;

    @Inject
    DeepLinkService deepLinkService;

}

package org.incode.module.document.dom.mixins;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.registry.ServiceRegistry;

import org.incode.module.document.DocumentModule;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;

/**
 * Subclasses should be annotated with <code>@Mixin(method='act')</code>
 */
public abstract class T_preview<T> {

    protected final T domainObject;

    public T_preview(final T domainObject) {
        this.domainObject = domainObject;
    }


    public static class ActionDomainEvent extends DocumentModule.ActionDomainEvent<T_preview> {}

    @Action(
            domainEvent = ActionDomainEvent.class,
            semantics = SemanticsOf.SAFE
    )
    @ActionLayout(
            contributed = Contributed.AS_ACTION
    )
    public DocumentPreview act(final DocumentTemplate template) throws IOException {
        final Object rendererModel = template.newRendererModel(domainObject);
        final DocumentPreview preview = serviceRegistry.injectServicesInto(new DocumentPreview());
        template.renderContent(preview, rendererModel);
        preview.setType(template.getType());
        return preview;
    }

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

}

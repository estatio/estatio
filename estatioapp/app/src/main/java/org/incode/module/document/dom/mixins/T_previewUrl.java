package org.incode.module.document.dom.mixins;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;

import org.incode.module.document.DocumentModule;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;
import org.incode.module.document.dom.services.ClassService;

public abstract class T_previewUrl<T> {

    protected final T domainObject;

    public T_previewUrl(final T domainObject) {
        this.domainObject = domainObject;
    }


    public static class ActionDomainEvent extends DocumentModule.ActionDomainEvent<T_previewUrl> {}

    @Action(
            domainEvent = ActionDomainEvent.class,
            semantics = SemanticsOf.SAFE
    )
    @ActionLayout(
            contributed = Contributed.AS_ACTION
    )
    @MemberOrder(name = "documents", sequence = "2")
    public URL $$(final DocumentTemplate template) throws IOException {
        final Object rendererModel = template.newRendererModel(domainObject);
        return template.previewUrl(rendererModel);
    }

    public boolean hide$$() {
        return choices0$$().isEmpty();
    }

    public DocumentTemplate default0$$() {
        final List<DocumentTemplate> documentTemplates = choices0$$();
        return documentTemplates.size() == 1 ? documentTemplates.get(0): null;
    }

    /**
     * All templates which are applicable to the domain object's atPath, and which can be previewed.
     */
    public List<DocumentTemplate> choices0$$() {
        return queryResultsCache.execute(
                () -> documentTemplateService.documentTemplatesForPreview(domainObject),
                getClass(), "$$", domainObject);
    }


    //region > injected services

    @Inject
    DocumentTemplateForAtPathService documentTemplateService;

    @Inject
    PaperclipRepository paperclipRepository;

    @Inject
    DocumentTypeRepository documentTypeRepository;

    @Inject
    ClassService classService;

    @Inject
    QueryResultsCache queryResultsCache;

    //endregion

}

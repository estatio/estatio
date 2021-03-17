package org.incode.module.document.dom.mixins;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.background.BackgroundCommandService;
import org.apache.isis.applib.services.background.BackgroundService2;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;

import org.incode.module.document.DocumentModule;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.services.DocumentCreatorService;

/**
 * Create a {@link Document} and attach using a {@link Paperclip}, then schedule the document to be rendered in the background.
 */
public abstract class T_createAndAttachDocumentAndScheduleRender<T>  {

    protected final T domainObject;

    public T_createAndAttachDocumentAndScheduleRender(final T domainObject) {
        this.domainObject = domainObject;
    }



    public static class ActionDomainEvent extends DocumentModule.ActionDomainEvent<T_createAndAttachDocumentAndScheduleRender> {}

    /**
     * Create a {@link Document} and attach using a {@link Paperclip}.
     */
    @Action(
            domainEvent = ActionDomainEvent.class,
            semantics = SemanticsOf.NON_IDEMPOTENT
    )
    @ActionLayout(
            contributed = Contributed.AS_ACTION
    )
    @MemberOrder(name = "documents", sequence = "3.2")
    public Object $$(final DocumentTemplate template) throws IOException {

        final Document document =
                documentCreatorService.createDocumentAndAttachPaperclips(domainObject, template);

        render(template, document);

        return document;
    }

    public boolean hide$$() {
        return choices0$$().isEmpty();
    }

    public TranslatableString disable$$() {
        return backgroundCommandService == null
                ? TranslatableString.tr("Application is not configured to support background rendering")
                : null;
    }

    public DocumentTemplate default0$$() {
        final List<DocumentTemplate> documentTemplates = choices0$$();
        return documentTemplates.size() == 1 ? documentTemplates.get(0): null;
    }

    /**
     * All templates which are applicable to the domain object's atPath, and which can be created and attached to at
     * least one domain object.
     */
    public List<DocumentTemplate> choices0$$() {
        return queryResultsCache.execute(
                () -> documentTemplateService.documentTemplatesForCreateAndAttach(domainObject),
                getClass(), "$$", domainObject);
    }

    protected void render(
            final DocumentTemplate template,
            final Document document) {
        backgroundService2.execute(document).render(template, domainObject);
    }

    //region > injected services

    @Inject
    DocumentTemplateForAtPathService documentTemplateService;

    @Inject
    DocumentCreatorService documentCreatorService;

    @Inject
    QueryResultsCache queryResultsCache;

    @Inject
    BackgroundCommandService backgroundCommandService;

    @Inject
    BackgroundService2 backgroundService2;

    //endregion

}

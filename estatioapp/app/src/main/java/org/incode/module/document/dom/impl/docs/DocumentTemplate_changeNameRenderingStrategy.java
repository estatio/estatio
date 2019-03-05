package org.incode.module.document.dom.impl.docs;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.document.DocumentModule;
import org.incode.module.document.dom.impl.rendering.RenderingStrategy;
import org.incode.module.document.dom.impl.rendering.RenderingStrategyRepository;

/**
 * TODO: remove this once move to RenderingStrategyData
 */
@Mixin
public class DocumentTemplate_changeNameRenderingStrategy {

    //region > constructor
    private final DocumentTemplate documentTemplate;

    public DocumentTemplate_changeNameRenderingStrategy(final DocumentTemplate documentTemplate) {
        this.documentTemplate = documentTemplate;
    }
    //endregion


    public static class ActionDomainEvent extends DocumentModule.ActionDomainEvent<DocumentTemplate_changeNameRenderingStrategy>  { }

    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            domainEvent = ActionDomainEvent.class
    )
    public DocumentTemplate $$(
            final RenderingStrategy renderingStrategy) {
        documentTemplate.setNameRenderingStrategy(renderingStrategy);
        return documentTemplate;
    }

    public RenderingStrategy default0$$() {
        return currentNameRenderingStrategy();
    }

    public List<RenderingStrategy> choices0$$() {
        return renderingStrategyRepository.findForUseWithSubjectText();
    }

    private RenderingStrategy currentNameRenderingStrategy() {
        return documentTemplate.getNameRenderingStrategy();
    }


    @Inject
    private DocumentTemplateRepository documentTemplateRepository;
    @Inject
    private RenderingStrategyRepository renderingStrategyRepository;


}

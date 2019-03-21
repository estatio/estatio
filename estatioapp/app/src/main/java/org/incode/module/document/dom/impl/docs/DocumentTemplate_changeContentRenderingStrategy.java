package org.incode.module.document.dom.impl.docs;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.i18n.TranslatableString;

import org.incode.module.document.DocumentModule;
import org.incode.module.document.dom.impl.rendering.RenderingStrategy;

/**
 * TODO: remove this once move to RenderingStrategyData
 */
@Mixin
public class DocumentTemplate_changeContentRenderingStrategy {

    //region > constructor
    private final DocumentTemplate documentTemplate;

    public DocumentTemplate_changeContentRenderingStrategy(final DocumentTemplate documentTemplate) {
        this.documentTemplate = documentTemplate;
    }
    //endregion


    public static class ActionDomainEvent extends DocumentModule.ActionDomainEvent<DocumentTemplate_changeContentRenderingStrategy>  { }

    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            domainEvent = ActionDomainEvent.class
    )
    public DocumentTemplate $$(
            final RenderingStrategy renderingStrategy) {
        documentTemplate.setContentRenderingStrategy(renderingStrategy);
        return documentTemplate;
    }

    public RenderingStrategy default0$$() {
        return currentContentRenderingStrategy();
    }

    public TranslatableString validate0$$(final RenderingStrategy proposedRenderingStrategy) {
        if(currentContentRenderingStrategy().getInputNature() != proposedRenderingStrategy.getInputNature()) {
            return TranslatableString.tr("The input nature of the new rendering strategy (binary or characters) must be the same as the current");
        }
        return null;
    }

    private RenderingStrategy currentContentRenderingStrategy() {
        return documentTemplate.getContentRenderingStrategy();
    }


    @Inject
    private DocumentTemplateRepository documentTemplateRepository;


}

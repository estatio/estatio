package org.incode.module.document.dom.impl.rendering;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.i18n.TranslatableString;

import org.incode.module.document.dom.DocumentModule;
import org.incode.module.document.dom.impl.renderers.Renderer;
import org.incode.module.document.dom.services.ClassNameViewModel;
import org.incode.module.document.dom.spi.RendererClassNameService;
import org.incode.module.document.dom.types.NameType;

@Mixin
public class RenderingStrategy_changeRenderer {

    //region > constructor
    private final RenderingStrategy renderingStrategy;

    public RenderingStrategy_changeRenderer(final RenderingStrategy renderingStrategy) {
        this.renderingStrategy = renderingStrategy;
    }
    //endregion


    public static class ActionDomainEvent extends DocumentModule.ActionDomainEvent<RenderingStrategy_changeRenderer>  { }

    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            domainEvent = ActionDomainEvent.class
    )
    public RenderingStrategy $$(
            @Parameter(maxLength = NameType.Meta.MAX_LEN, mustSatisfy = RenderingStrategy.RendererClassNameType.Meta.Specification.class)
            @ParameterLayout(named = "Renderer class name")
            final ClassNameViewModel classViewModel) {

        final Class<? extends Renderer> rendererClass =
                rendererClassNameService.asClass(classViewModel.getFullyQualifiedClassName());
        renderingStrategy.setRendererClassName(rendererClass.getName());
        return renderingStrategy;
    }

    public TranslatableString disable$$() {
        return rendererClassNameService == null
                ? TranslatableString.tr(
                "No RendererClassNameService registered to locate implementations of Renderer")
                : null;
    }

    public List<ClassNameViewModel> choices0$$() {
        return rendererClassNameService.renderClassNamesFor(renderingStrategy.getInputNature(), renderingStrategy.getOutputNature());
    }

    @Inject
    RendererClassNameService rendererClassNameService;

}

package org.incode.module.document.fixture;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.incode.module.document.dom.impl.docs.DocumentNature;
import org.incode.module.document.dom.impl.renderers.Renderer;
import org.incode.module.document.dom.impl.rendering.RenderingStrategy;
import org.incode.module.document.dom.impl.rendering.RenderingStrategyRepository;

public abstract class RenderingStrategyFSAbstract extends FixtureScript {

    @Override
    protected abstract void execute(ExecutionContext executionContext);

    protected RenderingStrategy upsertRenderingStrategy(
            final String reference,
            final String name,
            final DocumentNature inputNature,
            final DocumentNature outputNature,
            final Class<? extends Renderer> rendererClass,
            final ExecutionContext executionContext) {

        RenderingStrategy renderingStrategy = renderingStrategyRepository.findByReference(reference);
        if (renderingStrategy != null) {
            renderingStrategy.setName(name);
            renderingStrategy.setInputNature(inputNature);
            renderingStrategy.setOutputNature(outputNature);
            renderingStrategy.setRendererClassName(rendererClass.getName());
        } else {
            renderingStrategy =
                    renderingStrategyRepository.create(reference, name, inputNature, outputNature, rendererClass);
        }
        return executionContext.addResult(this, renderingStrategy);
    }

    @Inject
    protected RenderingStrategyRepository renderingStrategyRepository;

}

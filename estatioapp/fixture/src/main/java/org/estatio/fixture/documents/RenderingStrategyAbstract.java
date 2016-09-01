package org.estatio.fixture.documents;

import javax.inject.Inject;

import org.incode.module.documents.dom.rendering.RenderingStrategy;
import org.incode.module.documents.dom.rendering.RenderingStrategyRepository;

import org.estatio.dom.apptenancy.EstatioApplicationTenancyRepository;
import org.estatio.fixture.EstatioFixtureScript;

public abstract class RenderingStrategyAbstract extends EstatioFixtureScript {

    @Override
    protected abstract void execute(ExecutionContext executionContext);

    protected RenderingStrategy createRenderingStrategy(
            String reference,
            String name,
            final String rendererClassName, ExecutionContext executionContext) {

        final RenderingStrategy renderingStrategy = renderingStrategyRepository.create(reference, name, rendererClassName);
        return executionContext.addResult(this, renderingStrategy);
    }

    @Inject
    protected RenderingStrategyRepository renderingStrategyRepository;

}

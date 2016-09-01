package org.estatio.fixture.documents;

import org.estatio.app.integration.documents.RendererForFreemarker;

public class RenderingStrategyForFreemarker extends RenderingStrategyAbstract {

    public static final String REF = "FREEMARKER";

    @Override
    protected void execute(ExecutionContext executionContext) {

        createRenderingStrategy(
                REF,
                "Freemarker Rendering Strategy",
                RendererForFreemarker.class.getName(),
                executionContext);

    }


}

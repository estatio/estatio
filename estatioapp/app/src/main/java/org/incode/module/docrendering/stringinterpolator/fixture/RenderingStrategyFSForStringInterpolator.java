package org.incode.module.docrendering.stringinterpolator.fixture;

import org.incode.module.document.dom.impl.docs.DocumentNature;
import org.incode.module.document.fixture.RenderingStrategyFSAbstract;

import org.incode.module.docrendering.stringinterpolator.dom.impl.RendererForStringInterpolator;

public class RenderingStrategyFSForStringInterpolator extends RenderingStrategyFSAbstract {

    public static final String REF = "SI";

    @Override
    protected void execute(ExecutionContext executionContext) {
        upsertRenderingStrategy(
                REF,
                "String interpolate",
                DocumentNature.CHARACTERS,
                DocumentNature.CHARACTERS,
                RendererForStringInterpolator.class, executionContext);
    }

}

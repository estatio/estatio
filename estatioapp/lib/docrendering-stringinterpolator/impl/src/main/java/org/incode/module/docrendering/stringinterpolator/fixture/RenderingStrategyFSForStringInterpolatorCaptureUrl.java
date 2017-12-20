package org.incode.module.docrendering.stringinterpolator.fixture;

import org.incode.module.document.dom.impl.docs.DocumentNature;
import org.incode.module.document.fixture.RenderingStrategyFSAbstract;

import org.incode.module.docrendering.stringinterpolator.dom.impl.RendererForStringInterpolatorCaptureUrl;

public class RenderingStrategyFSForStringInterpolatorCaptureUrl extends RenderingStrategyFSAbstract {

    public static final String REF = "SINC";

    @Override
    protected void execute(ExecutionContext executionContext) {
        upsertRenderingStrategy(
                REF,
                "String interpolate URL for Capture (no preview)",
                DocumentNature.CHARACTERS,
                DocumentNature.BYTES,
                RendererForStringInterpolatorCaptureUrl.class, executionContext);
    }

}

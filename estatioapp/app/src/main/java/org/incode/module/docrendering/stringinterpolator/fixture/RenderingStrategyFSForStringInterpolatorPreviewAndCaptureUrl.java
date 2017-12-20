package org.incode.module.docrendering.stringinterpolator.fixture;

import org.incode.module.document.dom.impl.docs.DocumentNature;
import org.incode.module.document.fixture.RenderingStrategyFSAbstract;

import org.incode.module.docrendering.stringinterpolator.dom.impl.RendererForStringInterpolatorPreviewAndCaptureUrl;

public class RenderingStrategyFSForStringInterpolatorPreviewAndCaptureUrl extends RenderingStrategyFSAbstract {

    public static final String REF = "SIPC";

    @Override
    protected void execute(ExecutionContext executionContext) {
        upsertRenderingStrategy(
                REF,
                "String interpolate URL for Preview and Capture",
                DocumentNature.CHARACTERS,
                DocumentNature.BYTES,
                RendererForStringInterpolatorPreviewAndCaptureUrl.class, executionContext);
    }

}

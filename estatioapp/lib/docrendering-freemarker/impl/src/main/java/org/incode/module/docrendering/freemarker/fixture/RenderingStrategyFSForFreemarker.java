package org.incode.module.docrendering.freemarker.fixture;

import org.incode.module.document.dom.impl.docs.DocumentNature;
import org.incode.module.document.fixture.RenderingStrategyFSAbstract;

import org.incode.module.docrendering.freemarker.dom.impl.RendererForFreemarker;

public class RenderingStrategyFSForFreemarker extends RenderingStrategyFSAbstract {

    public static final String REF = "FMK";

    @Override
    protected void execute(ExecutionContext executionContext) {
        upsertRenderingStrategy(
                REF,
                "RendererForFreemarker Rendering Strategy",
                DocumentNature.CHARACTERS, DocumentNature.CHARACTERS,
                RendererForFreemarker.class, executionContext);

    }


}

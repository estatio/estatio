package org.incode.module.docrendering.xdocgoten.fixture;

import org.incode.module.docrendering.xdocgoten.dom.impl.RendererForXDocReportToDocxThenGotenbergToPdf;
import org.incode.module.document.dom.impl.docs.DocumentNature;
import org.incode.module.document.fixture.RenderingStrategyFSAbstract;

public class RenderingStrategyFSForXDocGotenToPdf extends RenderingStrategyFSAbstract {

    public static final String REF = "XGP";

    @Override
    protected void execute(ExecutionContext executionContext) {
        upsertRenderingStrategy(
                REF,
                "XDocReport to .docx, Gotenberg to .pdf",
                DocumentNature.BYTES,
                DocumentNature.BYTES,
                RendererForXDocReportToDocxThenGotenbergToPdf.class, executionContext);
    }

}

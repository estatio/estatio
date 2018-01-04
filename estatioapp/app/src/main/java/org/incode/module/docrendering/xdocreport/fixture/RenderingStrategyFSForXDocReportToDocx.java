package org.incode.module.docrendering.xdocreport.fixture;

import org.incode.module.docrendering.xdocreport.dom.impl.RendererForXDocReportToDocx;
import org.incode.module.document.dom.impl.docs.DocumentNature;
import org.incode.module.document.fixture.RenderingStrategyFSAbstract;

public class RenderingStrategyFSForXDocReportToDocx extends RenderingStrategyFSAbstract {

    public static final String REF = "XDD";

    @Override
    protected void execute(ExecutionContext executionContext) {
        upsertRenderingStrategy(
                REF,
                "XDocReport to .docx",
                DocumentNature.BYTES,
                DocumentNature.BYTES,
                RendererForXDocReportToDocx.class, executionContext);
    }

}

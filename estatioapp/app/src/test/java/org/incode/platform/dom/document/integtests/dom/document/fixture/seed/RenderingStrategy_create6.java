package org.incode.platform.dom.document.integtests.dom.document.fixture.seed;

import org.incode.module.docrendering.freemarker.fixture.RenderingStrategyFSForFreemarker;
import org.incode.module.docrendering.stringinterpolator.fixture.RenderingStrategyFSForStringInterpolator;
import org.incode.module.docrendering.stringinterpolator.fixture.RenderingStrategyFSForStringInterpolatorCaptureUrl;
import org.incode.module.docrendering.stringinterpolator.fixture.RenderingStrategyFSForStringInterpolatorPreviewAndCaptureUrl;
import org.incode.module.docrendering.xdocreport.fixture.RenderingStrategyFSForXDocReportToDocx;
import org.incode.module.docrendering.xdocreport.fixture.RenderingStrategyFSForXDocReportToPdf;
import org.incode.module.document.fixture.DocumentTemplateFSAbstract;

public class RenderingStrategy_create6 extends DocumentTemplateFSAbstract {

    public static final String REF_SIPC = RenderingStrategyFSForStringInterpolatorPreviewAndCaptureUrl.REF;
    public static final String REF_SINC = RenderingStrategyFSForStringInterpolatorCaptureUrl.REF;
    public static final String REF_SI = RenderingStrategyFSForStringInterpolator.REF;
    public static final String REF_FMK = RenderingStrategyFSForFreemarker.REF;
    public static final String REF_XDP = RenderingStrategyFSForXDocReportToPdf.REF;
    public static final String REF_XDD = RenderingStrategyFSForXDocReportToDocx.REF;


    @Override
    protected void execute(final ExecutionContext executionContext) {

        // prereqs

        executionContext.executeChild(this, new RenderingStrategyFSForStringInterpolatorPreviewAndCaptureUrl());
        executionContext.executeChild(this, new RenderingStrategyFSForStringInterpolatorCaptureUrl());
        executionContext.executeChild(this, new RenderingStrategyFSForStringInterpolator());
        executionContext.executeChild(this, new RenderingStrategyFSForFreemarker());
        executionContext.executeChild(this, new RenderingStrategyFSForXDocReportToPdf());
        executionContext.executeChild(this, new RenderingStrategyFSForXDocReportToDocx());

    }


}

package org.incode.platform.dom.communications.integtests.dom.communications.fixture.data.doctypes;

import org.incode.module.docrendering.freemarker.fixture.RenderingStrategyFSForFreemarker;
import org.incode.module.document.fixture.DocumentTemplateFSAbstract;

public class RenderingStrategy_create1 extends DocumentTemplateFSAbstract {

    public static final String REF_FMK = RenderingStrategyFSForFreemarker.REF;


    @Override
    protected void execute(final ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new RenderingStrategyFSForFreemarker());

    }


}

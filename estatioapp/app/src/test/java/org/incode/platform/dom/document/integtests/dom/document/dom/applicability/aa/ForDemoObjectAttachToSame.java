package org.incode.platform.dom.document.integtests.dom.document.dom.applicability.aa;

import java.util.Collections;
import java.util.List;

import org.incode.module.document.dom.impl.applicability.AttachmentAdvisor;
import org.incode.module.document.dom.impl.applicability.AttachmentAdvisorAbstract;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.platform.dom.document.integtests.demo.dom.demowithurl.DemoObjectWithUrl;

import lombok.Value;

public class ForDemoObjectAttachToSame extends AttachmentAdvisorAbstract<DemoObjectWithUrl> {

    public ForDemoObjectAttachToSame() {
        super(DemoObjectWithUrl.class);
    }

    @Override
    protected List<PaperclipSpec> doAdvise(
            final DocumentTemplate documentTemplate,
            final DemoObjectWithUrl demoObject,
            final Document createdDocument) {
        return Collections.singletonList(new AttachmentAdvisor.PaperclipSpec(null, demoObject, createdDocument));
    }

    @Value
    public static class DataModel {
        DemoObjectWithUrl demoObject;
    }

}


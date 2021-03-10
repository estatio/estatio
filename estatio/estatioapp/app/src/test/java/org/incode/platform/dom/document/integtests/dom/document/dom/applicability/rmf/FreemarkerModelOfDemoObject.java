package org.incode.platform.dom.document.integtests.dom.document.dom.applicability.rmf;

import org.incode.module.document.dom.impl.applicability.RendererModelFactoryAbstract;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.platform.dom.document.integtests.demo.dom.demowithurl.DemoObjectWithUrl;

import lombok.Value;

public class FreemarkerModelOfDemoObject extends RendererModelFactoryAbstract<DemoObjectWithUrl> {

    public FreemarkerModelOfDemoObject() {
        super(DemoObjectWithUrl.class);
    }

    @Override protected Object doNewRendererModel(
            final DocumentTemplate documentTemplate, final DemoObjectWithUrl demoObject) {
        return new DataModel(demoObject);
    }

    @Value
    public static class DataModel {
        DemoObjectWithUrl demoObject;
    }

}


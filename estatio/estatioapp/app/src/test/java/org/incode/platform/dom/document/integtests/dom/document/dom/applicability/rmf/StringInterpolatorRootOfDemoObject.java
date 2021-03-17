package org.incode.platform.dom.document.integtests.dom.document.dom.applicability.rmf;

import org.isisaddons.module.stringinterpolator.dom.StringInterpolatorService;

import org.incode.module.document.dom.impl.applicability.RendererModelFactoryAbstract;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.platform.dom.document.integtests.demo.dom.demowithurl.DemoObjectWithUrl;

import lombok.Getter;

public class StringInterpolatorRootOfDemoObject extends RendererModelFactoryAbstract<DemoObjectWithUrl> {

    public StringInterpolatorRootOfDemoObject() {
        super(DemoObjectWithUrl.class);
    }

    @Override
    protected Object doNewRendererModel(
            final DocumentTemplate documentTemplate, final DemoObjectWithUrl demoObject) {
        return new DataModel(demoObject);
    }

    public static class DataModel extends StringInterpolatorService.Root {
        @Getter
        private final DemoObjectWithUrl demoObject;

        public DataModel(final DemoObjectWithUrl demoObject) {
            super(demoObject);
            this.demoObject = demoObject;
        }
    }
}

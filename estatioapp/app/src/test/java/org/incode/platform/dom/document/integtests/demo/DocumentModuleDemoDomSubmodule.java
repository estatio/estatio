package org.incode.platform.dom.document.integtests.demo;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.isis.applib.ModuleAbstract;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.fixturescripts.teardown.TeardownFixtureAbstract2;

import org.incode.platform.dom.document.integtests.demo.dom.demowithnotes.DemoObjectWithNotes;
import org.incode.platform.dom.document.integtests.demo.dom.demowithurl.DemoObjectWithUrl;
import org.incode.platform.dom.document.integtests.demo.dom.invoice.DemoInvoice;
import org.incode.platform.dom.document.integtests.demo.dom.order.DemoOrder;
import org.incode.platform.dom.document.integtests.demo.dom.order.DemoOrderLine;
import org.incode.platform.dom.document.integtests.demo.dom.other.OtherObject;

@XmlRootElement(name = "module")
public class DocumentModuleDemoDomSubmodule extends ModuleAbstract {

    @Override public FixtureScript getTeardownFixture() {
        return new TeardownFixtureAbstract2() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                deleteFrom(DemoInvoice.class);
                deleteFrom(DemoObjectWithNotes.class);
                deleteFrom(DemoObjectWithUrl.class);
                deleteFrom(DemoOrderLine.class);
                deleteFrom(DemoOrder.class);
                deleteFrom(OtherObject.class);
            }
        };
    }
}

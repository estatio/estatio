package org.incode.platform.dom.communications.integtests.demo;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.isis.applib.ModuleAbstract;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.fixturescripts.teardown.TeardownFixtureAbstract2;

import org.incode.platform.dom.communications.integtests.demo.dom.demowithnotes.DemoObjectWithNotes;
import org.incode.platform.dom.communications.integtests.demo.dom.invoice.DemoInvoice;

@XmlRootElement(name = "module")
public class CommunicationsModuleDemoDomSubmodule extends ModuleAbstract {

    @Override
    public FixtureScript getTeardownFixture() {
        return new TeardownFixtureAbstract2() {
            @Override
            protected void execute(FixtureScript.ExecutionContext executionContext) {
                deleteFrom(DemoInvoice.class);
                deleteFrom(DemoObjectWithNotes.class);
            }
        };
    }
}

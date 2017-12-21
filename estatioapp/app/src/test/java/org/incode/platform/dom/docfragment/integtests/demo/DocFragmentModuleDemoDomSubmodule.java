package org.incode.platform.dom.docfragment.integtests.demo;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.isis.applib.ModuleAbstract;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.fixturescripts.teardown.TeardownFixtureAbstract2;

import org.incode.platform.dom.docfragment.integtests.demo.dom.customer.DemoCustomer;
import org.incode.platform.dom.docfragment.integtests.demo.dom.invoicewithatpath.DemoInvoiceWithAtPath;

@XmlRootElement(name = "module")
public class DocFragmentModuleDemoDomSubmodule extends ModuleAbstract {

    @Override public FixtureScript getTeardownFixture() {
        return new TeardownFixtureAbstract2() {
            @Override
            protected void execute(final FixtureScript.ExecutionContext executionContext) {
                deleteFrom(DemoCustomer.class);
                deleteFrom(DemoInvoiceWithAtPath.class);
            }
        };
    }
}

package org.incode.platform.dom.docfragment.integtests.dom.docfragment.fixture;

import org.apache.isis.applib.fixturescripts.teardown.TeardownFixtureAbstract2;

import org.incode.platform.dom.docfragment.integtests.demo.fixture.teardown.sub.DemoCustomer_tearDown;
import org.incode.platform.dom.docfragment.integtests.demo.fixture.teardown.sub.DemoInvoiceWithAtPath_tearDown;
import org.incode.platform.dom.docfragment.integtests.dom.docfragment.fixture.sub.DocFragment_tearDown;

public class DemoModule_and_DocFragment_tearDown extends TeardownFixtureAbstract2 {

    @Override
    protected void execute(ExecutionContext executionContext) {
        executionContext.executeChild(this, new DemoCustomer_tearDown());
        executionContext.executeChild(this, new DemoInvoiceWithAtPath_tearDown());
    }

}

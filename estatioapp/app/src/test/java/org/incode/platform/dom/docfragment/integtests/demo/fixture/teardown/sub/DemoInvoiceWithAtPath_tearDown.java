package org.incode.platform.dom.docfragment.integtests.demo.fixture.teardown.sub;

import org.apache.isis.applib.fixturescripts.teardown.TeardownFixtureAbstract2;

import org.incode.platform.dom.docfragment.integtests.demo.dom.invoicewithatpath.DemoInvoiceWithAtPath;

public class DemoInvoiceWithAtPath_tearDown extends TeardownFixtureAbstract2 {

    @Override
    protected void execute(final ExecutionContext executionContext) {
        deleteFrom(DemoInvoiceWithAtPath.class);
    }


}

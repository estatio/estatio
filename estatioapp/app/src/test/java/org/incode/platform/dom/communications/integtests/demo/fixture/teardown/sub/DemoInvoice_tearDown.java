package org.incode.platform.dom.communications.integtests.demo.fixture.teardown.sub;

import org.apache.isis.applib.fixturescripts.teardown.TeardownFixtureAbstract2;
import org.apache.isis.applib.services.jdosupport.IsisJdoSupport;

import org.incode.platform.dom.communications.integtests.demo.dom.invoice.DemoInvoice;

public class DemoInvoice_tearDown extends TeardownFixtureAbstract2 {

    @Override
    protected void execute(final ExecutionContext executionContext) {
        deleteFrom(DemoInvoice.class);
    }


    @javax.inject.Inject
    IsisJdoSupport isisJdoSupport;

}

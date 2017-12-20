package org.incode.platform.dom.document.integtests.demo.fixture.teardown.sub;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.jdosupport.IsisJdoSupport;

public class DemoInvoice_tearDown extends FixtureScript {

    @Override
    protected void execute(final ExecutionContext executionContext) {
        isisJdoSupport.executeUpdate("delete from \"exampleDemo\".\"DemoInvoice\"");
    }


    @javax.inject.Inject
    IsisJdoSupport isisJdoSupport;

}

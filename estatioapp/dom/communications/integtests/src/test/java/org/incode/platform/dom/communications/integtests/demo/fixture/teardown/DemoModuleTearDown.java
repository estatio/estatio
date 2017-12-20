package org.incode.platform.dom.communications.integtests.demo.fixture.teardown;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.incode.platform.dom.communications.integtests.demo.fixture.teardown.sub.DemoInvoice_tearDown;
import org.incode.platform.dom.communications.integtests.demo.fixture.teardown.sub.DemoObjectWithNotes_tearDown;

public class DemoModuleTearDown extends FixtureScript {

    @Override
    protected void execute(ExecutionContext executionContext) {

        executionContext.executeChild(this, new DemoObjectWithNotes_tearDown());
        executionContext.executeChild(this, new DemoInvoice_tearDown());

    }

}

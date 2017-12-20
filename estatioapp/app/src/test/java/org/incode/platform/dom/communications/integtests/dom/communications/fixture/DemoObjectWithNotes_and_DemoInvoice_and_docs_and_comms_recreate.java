package org.incode.platform.dom.communications.integtests.dom.communications.fixture;

import org.apache.isis.applib.fixturescripts.FixtureScript;

public class DemoObjectWithNotes_and_DemoInvoice_and_docs_and_comms_recreate extends FixtureScript {

    @Override
    protected void execute(final ExecutionContext executionContext) {

        executionContext.executeChild(this, new DemoObjectWithNotes_and_DemoInvoice_and_docs_and_comms_tearDown());
        executionContext.executeChild(this, new DemoObjectWithNotes_and_DemoInvoice_and_docs_and_comms_create());
    }

}

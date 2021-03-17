package org.incode.platform.dom.communications.integtests.demo.fixture.teardown.sub;

import org.apache.isis.applib.fixturescripts.teardown.TeardownFixtureAbstract2;
import org.apache.isis.applib.services.jdosupport.IsisJdoSupport;

import org.incode.platform.dom.communications.integtests.demo.dom.demowithnotes.DemoObjectWithNotes;

public class DemoObjectWithNotes_tearDown extends TeardownFixtureAbstract2 {

    @Override
    protected void execute(final ExecutionContext executionContext) {
        deleteFrom(DemoObjectWithNotes.class);
    }


    @javax.inject.Inject
    IsisJdoSupport isisJdoSupport;

}

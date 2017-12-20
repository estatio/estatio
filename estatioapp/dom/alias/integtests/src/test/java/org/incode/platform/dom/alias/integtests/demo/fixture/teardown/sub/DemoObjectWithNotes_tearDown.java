package org.incode.platform.dom.alias.integtests.demo.fixture.teardown.sub;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.jdosupport.IsisJdoSupport;

public class DemoObjectWithNotes_tearDown extends FixtureScript {

    @Override
    protected void execute(final ExecutionContext executionContext) {
        isisJdoSupport.executeUpdate("delete from \"exampleDemo\".\"DemoObjectWithNotes\"");
    }


    @javax.inject.Inject
    IsisJdoSupport isisJdoSupport;

}

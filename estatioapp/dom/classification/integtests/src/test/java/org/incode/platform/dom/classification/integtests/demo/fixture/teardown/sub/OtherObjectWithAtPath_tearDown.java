package org.incode.platform.dom.classification.integtests.demo.fixture.teardown.sub;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.jdosupport.IsisJdoSupport;

public class OtherObjectWithAtPath_tearDown extends FixtureScript {

    @Override
    protected void execute(final ExecutionContext executionContext) {
        isisJdoSupport.executeUpdate("delete from \"exampleDemo\".\"OtherObjectWithAtPath\"");
    }


    @javax.inject.Inject
    IsisJdoSupport isisJdoSupport;

}

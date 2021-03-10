package org.incode.platform.dom.classification.integtests.demo.fixture.teardown.sub;

import org.apache.isis.applib.fixturescripts.teardown.TeardownFixtureAbstract2;
import org.apache.isis.applib.services.jdosupport.IsisJdoSupport;

import org.incode.platform.dom.classification.integtests.demo.dom.demowithatpath.DemoObjectWithAtPath;

public class DemoObjectWithAtPath_tearDown extends TeardownFixtureAbstract2 {

    @Override
    protected void execute(final ExecutionContext executionContext) {
        deleteFrom(DemoObjectWithAtPath.class);
    }


}

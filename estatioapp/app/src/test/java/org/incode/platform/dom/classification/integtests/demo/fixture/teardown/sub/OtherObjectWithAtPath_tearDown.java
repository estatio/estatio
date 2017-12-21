package org.incode.platform.dom.classification.integtests.demo.fixture.teardown.sub;

import org.apache.isis.applib.fixturescripts.teardown.TeardownFixtureAbstract2;

import org.incode.platform.dom.classification.integtests.demo.dom.otherwithatpath.OtherObjectWithAtPath;

public class OtherObjectWithAtPath_tearDown extends TeardownFixtureAbstract2 {

    @Override
    protected void execute(final ExecutionContext executionContext) {
        deleteFrom(OtherObjectWithAtPath.class);
    }


}
